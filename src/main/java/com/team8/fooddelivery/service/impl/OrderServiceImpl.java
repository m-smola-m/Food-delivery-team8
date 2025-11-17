package com.team8.fooddelivery.service.impl;

import com.team8.fooddelivery.model.CartItem;
import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.order.Order;
import com.team8.fooddelivery.model.order.OrderStatus;
import com.team8.fooddelivery.model.payment.PaymentMethod;
import com.team8.fooddelivery.model.payment.PaymentStatus;
import com.team8.fooddelivery.service.OrderService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * OrderServiceImpl реализован как конечный автомат по статусам заказа.
 * - Чёткая таблица допустимых переходов
 * - Защита от некорректных/повторных действий
 * - CASH становится PAID только при DELIVERY
 * - Атомарность смен статуса (synchronized по объекту заказа)
 */
public class OrderServiceImpl implements OrderService {

    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong orderIdGen = new AtomicLong(1);

    private final NotificationServiceImpl notificationService;

    // Таблица допустимых переходов (FSM)
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(OrderStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(OrderStatus.NEW, Set.of(OrderStatus.PICK_AND_PACKING, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.PICK_AND_PACKING, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED));
        ALLOWED_TRANSITIONS.put(OrderStatus.DELIVERED, Set.of()); // terminal
        ALLOWED_TRANSITIONS.put(OrderStatus.CANCELLED, Set.of()); // terminal
    }

    public OrderServiceImpl(NotificationServiceImpl notificationService) {
        this.notificationService = notificationService;
    }

    // --- CREATE ORDER ---
    @Override
    public Order createOrder(Long clientId,
                             List<CartItem> items,
                             Address deliveryAddress,
                             PaymentMethod paymentMethod) {

        // basic validations
        if (clientId == null) throw new IllegalArgumentException("clientId required");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Корзина пуста");
        if (deliveryAddress == null) throw new IllegalArgumentException("deliveryAddress required");
        if (paymentMethod == null) throw new IllegalArgumentException("paymentMethod required");

        // validate items
        long total = 0L;
        for (CartItem i : items) {
            if (i == null) throw new IllegalArgumentException("CartItem null");
            if (i.getQuantity() <= 0) throw new IllegalArgumentException("Неверное количество товара");
            if (i.getPrice() < 0) throw new IllegalArgumentException("Неверная цена товара");
            total += (long) (i.getPrice() * i.getQuantity());
        }

        long orderId = orderIdGen.getAndIncrement();
        Order order = Order.builder()
                .id(orderId)
                .clientId(clientId)
                .items(Collections.unmodifiableList(new ArrayList<>(items))) // immutable copy
                .totalPrice(total)
                .status(OrderStatus.NEW)
                .// payment starts as PENDING for all methods; for CASH we keep PENDING and only mark PAID on delivery
                        paymentStatus(PaymentStatus.PENDING)
                .deliveryAddress(deliveryAddress)
                .paymentMethod(paymentMethod)
                .createdAt(LocalDateTime.now())
                .estimatedDeliveryTime(LocalDateTime.now().plusHours(2))
                .build();

        orders.put(orderId, order);

        // уведомление о создании заказа (id + сумма)
        notificationService.notifyOrderPlaced(clientId, orderId, total);

        return order;
    }

    // --- PAY ORDER ---
    @Override
    public Order payOrder(Long orderId, PaymentMethod paymentMethod) {
        if (orderId == null) throw new IllegalArgumentException("orderId required");
        if (paymentMethod == null) throw new IllegalArgumentException("paymentMethod required");

        Order order = orders.get(orderId);
        if (order == null) throw new IllegalArgumentException("Заказ не найден: " + orderId);

        synchronized (order) {
            // cannot pay cancelled or delivered
            if (order.getStatus() == OrderStatus.CANCELLED)
                throw new IllegalStateException("Нельзя оплатить отменённый заказ");
            if (order.getStatus() == OrderStatus.DELIVERED)
                throw new IllegalStateException("Нельзя оплатить уже доставленный заказ");

            // already paid?
            if (order.getPaymentStatus() == PaymentStatus.PAID) {
                return order; // уже оплачено
            }

            // attach/validate chosen payment method
            order.setPaymentMethod(paymentMethod);

            String mtype = paymentMethod.getMethodType() == null ? "" : paymentMethod.getMethodType().toUpperCase(Locale.ROOT);

            // ONLINE/CARD -> paid immediately
            if ("CARD".equals(mtype) || "ONLINE".equals(mtype)) {
                order.setPaymentStatus(PaymentStatus.PAID);
                // optional: if order was NEW, progress to PICK_AND_PACKING automatically
                if (order.getStatus() == OrderStatus.NEW) {
                    // try to move to PICK_AND_PACKING (use FSM check)
                    transitionStatusLocked(order, OrderStatus.PICK_AND_PACKING);
                }
                // уведомление об оплате
                notificationService.notifyOrderPaid(order.getClientId(), orderId);
            } else if ("CASH".equals(mtype)) {
                // CASH — keep payment pending until delivery (business rule)
                order.setPaymentStatus(PaymentStatus.PENDING);
                // For cash, we may still allow pickup/packing (no immediate payment)
                // no immediate notifyOrderPaid
            } else {
                // unknown method type: treat as pending, but set method
                order.setPaymentStatus(PaymentStatus.PENDING);
            }

            return order;
        }
    }

    // --- UPDATE STATUS (FSM transition) ---
    @Override
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        if (orderId == null) throw new IllegalArgumentException("orderId required");
        if (newStatus == null) throw new IllegalArgumentException("status required");

        Order order = orders.get(orderId);
        if (order == null) throw new IllegalArgumentException("Заказ не найден: " + orderId);

        synchronized (order) {
            OrderStatus current = order.getStatus();

            // check if already terminal
            if (current == OrderStatus.DELIVERED || current == OrderStatus.CANCELLED) {
                throw new IllegalStateException("Нельзя изменить статус терминального заказа: " + current);
            }

            // check allowed transition
            Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Collections.emptySet());
            if (!allowed.contains(newStatus)) {
                throw new IllegalStateException(String.format("Нельзя перейти из %s в %s", current, newStatus));
            }

            // business rules for specific transitions:

            // PICK_AND_PACKING: allow for CASH even if payment pending; for others require PAID
            if (newStatus == OrderStatus.PICK_AND_PACKING) {
                PaymentMethod pm = order.getPaymentMethod();
                String mtype = pm == null ? "" : pm.getMethodType() == null ? "" : pm.getMethodType().toUpperCase(Locale.ROOT);
                if (!"CASH".equals(mtype) && order.getPaymentStatus() != PaymentStatus.PAID) {
                    throw new IllegalStateException("Нельзя начать сборку: оплата обязательна для этого способа оплаты");
                }
            }

            // SHIPPED: must come from PICK_AND_PACKING (already ensured by allowed transitions)
            // DELIVERED: handle CASH payment finalization
            if (newStatus == OrderStatus.DELIVERED) {
                // At the moment of delivery:
                //  - send delivery notification
                //  - if payment method is CASH and paymentStatus is PENDING -> mark PAID and notify payment
                PaymentMethod pm = order.getPaymentMethod();
                String mtype = pm == null ? "" : pm.getMethodType() == null ? "" : pm.getMethodType().toUpperCase(Locale.ROOT);

                // set status before or after notifications? set after checks, but we'll set now:
                order.setStatus(newStatus); // set delivered

                // notify delivery once
                notificationService.notifyDelivery(order.getClientId(), orderId);

                if ("CASH".equals(mtype) && order.getPaymentStatus() != PaymentStatus.PAID) {
                    order.setPaymentStatus(PaymentStatus.PAID);
                    // now notify payment completed for cash
                    notificationService.notifyOrderPaid(order.getClientId(), orderId);
                }

                // done
                return order;
            }

            // CANCELLED: ensure not paid already
            if (newStatus == OrderStatus.CANCELLED) {
                if (order.getPaymentStatus() == PaymentStatus.PAID) {
                    throw new IllegalStateException("Нельзя отменить уже оплаченный заказ");
                }
            }

            // default transition: set status and trigger notifications if needed
            order.setStatus(newStatus);

            if (newStatus == OrderStatus.PICK_AND_PACKING) {
                // Optional: notify that order started packing
                // Reuse existing template: send same ORDER_PLACED template or implement new one
                notificationService.notifyOrderPlaced(order.getClientId(), orderId, order.getTotalPrice());
            } else if (newStatus == OrderStatus.SHIPPED) {
                // optional notify shipment — could be added
            }

            return order;
        }
    }

    // --- GETTERS ---
    @Override
    public List<Order> getOrders(Long clientId) {
        List<Order> out = new ArrayList<>();
        for (Order o : orders.values()) {
            if (Objects.equals(o.getClientId(), clientId)) out.add(o);
        }
        return out;
    }

    @Override
    public Order getOrder(Long orderId) {
        return orders.get(orderId);
    }

    // Helper to perform transition assuming lock already held on order
    private void transitionStatusLocked(Order order, OrderStatus target) {
        OrderStatus current = order.getStatus();
        Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Collections.emptySet());
        if (!allowed.contains(target)) {
            throw new IllegalStateException(String.format("Нельзя перейти из %s в %s", current, target));
        }
        order.setStatus(target);
        // side effects
        if (target == OrderStatus.PICK_AND_PACKING) {
            notificationService.notifyOrderPlaced(order.getClientId(), order.getId(), order.getTotalPrice());
        } else if (target == OrderStatus.DELIVERED) {
            notificationService.notifyDelivery(order.getClientId(), order.getId());
        }
    }
}
