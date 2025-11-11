package com.team8.fooddelivery.model;

import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Data
public class Order {
    private Long id;
    private OrderStatus status;
    private Long customerId;
    private Long restaurantId;
    private String deliveryAddress;
    private Long courierId; 
    private Double totalPrice;
    private List<String> items;

    public static final Map<Long, Order> TEST_ORDERS = new HashMap<>();

    static {
        Order o1 = new Order();
        o1.setId(101L);
        o1.setStatus(OrderStatus.READY_FOR_PICKUP); 
        o1.setCustomerId(1L);
        o1.setRestaurantId(1L);
        o1.setDeliveryAddress("ул. Пушкина, д. 10");
        o1.setTotalPrice(1500.0);
        o1.setItems(List.of("Пицца Пепперони", "Кола"));
        
        Order o2 = new Order();
        o2.setId(102L);
        o2.setStatus(OrderStatus.PENDING);
        o2.setCustomerId(2L);
        o2.setRestaurantId(2L);
        o2.setDeliveryAddress("пр. Ленина, д. 5");
        o2.setTotalPrice(900.0);
        o2.setItems(List.of("Вок с курицей"));

        TEST_ORDERS.put(o1.getId(), o1);
        TEST_ORDERS.put(o2.getId(), o2);
    }
}
