package com.team8.fooddelivery.servlet.cart;

import com.team8.fooddelivery.model.product.CartItem;
import com.team8.fooddelivery.model.order.Order;
import com.team8.fooddelivery.model.client.PaymentMethodForOrder;
import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.repository.ClientRepository;
import com.team8.fooddelivery.service.CartService;
import com.team8.fooddelivery.service.OrderService;
import com.team8.fooddelivery.service.impl.CartServiceImpl;
import com.team8.fooddelivery.service.impl.OrderServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/cart/*")
public class CartServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(CartServlet.class);
    private final CartServiceImpl cartService = new CartServiceImpl();
    private final OrderService orderService = new OrderServiceImpl(cartService);
    private final ClientRepository clientRepository = new ClientRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if ("/items-api".equals(pathInfo)) {
            handleItemsApi(request, response);
        } else {
            response.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        try {
            if ("/add".equals(pathInfo)) {
                handleAddToCart(request, response);
            } else if ("/remove".equals(pathInfo)) {
                handleRemoveFromCart(request, response);
            } else if ("/update".equals(pathInfo)) {
                handleUpdateQuantity(request, response);
            } else if ("/checkout".equals(pathInfo)) {
                handleCheckout(request, response);
            } else if ("/clear".equals(pathInfo)) { // <-- Новый эндпоинт
                handleClearCart(request, response);
            } else {
                response.sendError(404);
            }
        } catch (Exception e) {
            log.error("Error", e);
            sendError(response, e.getMessage());
        }
    }

    private void handleClearCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long clientId = getClientId(request, response);
        if (clientId == null) {
            return;
        }
        cartService.clearCart(clientId);
        sendSuccess(response);
    }

    private void handleItemsApi(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long clientId = getClientId(request, response);
        if (clientId == null) {
            return;
        }

        List<CartItem> items = cartService.getCartItemsForClient(clientId);
        double total = items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();

        StringBuilder json = new StringBuilder("{");
        json.append("\"items\":[");
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            json.append("{")
                .append("\"cartItemId\":").append(item.getId()).append(",")
                .append("\"productId\":").append(item.getProductId()).append(",")
                .append("\"name\":\"").append(escape(item.getProductName())).append("\",")
                .append("\"price\":").append(item.getPrice()).append(",")
                .append("\"quantity\":").append(item.getQuantity()).append("}");
            if (i < items.size() - 1) {
                json.append(",");
            }
        }
        json.append("],");
        json.append("\"total\":").append(total);
        json.append("}");

        sendJson(response, json.toString());
    }

    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long clientId = getClientId(request, response);
        if (clientId == null) {
            return;
        }

        try {
            Long productId = Long.parseLong(request.getParameter("productId"));
            int quantity = Integer.parseInt(request.getParameter("quantity") != null ?
                    request.getParameter("quantity") : "1");

            cartService.addToCart(clientId, productId, quantity);
            sendSuccess(response);
        } catch (IllegalStateException e) {
            log.warn("Cannot add item to cart: {}", e.getMessage());
            sendError(response, e.getMessage());
        } catch (Exception e) {
            log.error("Error adding item to cart", e);
            sendError(response, "Не удалось добавить товар в корзину");
        }
    }

    private void handleRemoveFromCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long clientId = getClientId(request, response);
        if (clientId == null) {
            return;
        }

        Long cartItemId = Long.parseLong(request.getParameter("cartItemId"));
        cartService.removeFromCart(clientId, cartItemId);
        sendSuccess(response);
    }

    private void handleUpdateQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long clientId = getClientId(request, response);
        if (clientId == null) {
            return;
        }

        Long cartItemId = Long.parseLong(request.getParameter("cartItemId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        cartService.updateQuantity(clientId, cartItemId, quantity);
        sendSuccess(response);
    }

    private void handleCheckout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long clientId = getClientId(request, response);
        if (clientId == null) {
            return;
        }

        String paymentParam = request.getParameter("paymentMethod");
        PaymentMethodForOrder paymentMethod;
        try {
            paymentMethod = paymentParam == null ? PaymentMethodForOrder.CASH : PaymentMethodForOrder.valueOf(paymentParam);
        } catch (IllegalArgumentException ex) {
            paymentMethod = PaymentMethodForOrder.CASH;
        }

        // Адрес теперь тоже может приходить из формы
        Address address = extractAddress(request);
        if (isAddressEmpty(address)) {
            address = loadClientAddress(clientId);
        }

        try {
            Order order = orderService.placeOrder(clientId, address, paymentMethod);
            sendJson(response, orderToJson(order));
        } catch (RuntimeException e) {
            log.error("Checkout failed for client {}", clientId, e);
            sendError(response, e.getMessage());
        }
    }

    private Address extractAddress(HttpServletRequest request) {
        return Address.builder()
                .country(request.getParameter("country"))
                .city(request.getParameter("city"))
                .street(request.getParameter("street"))
                .building(request.getParameter("building"))
                .apartment(request.getParameter("apartment"))
                .entrance(request.getParameter("entrance"))
                .floor(request.getParameter("floor") != null ? Integer.parseInt(request.getParameter("floor")) : null)
                .addressNote(request.getParameter("addressNote"))
                .build();
    }

    private boolean isAddressEmpty(Address address) {
        return address == null ||
                (address.getCountry() == null || address.getCountry().isEmpty()) &&
                (address.getCity() == null || address.getCity().isEmpty()) &&
                (address.getStreet() == null || address.getStreet().isEmpty()) &&
                (address.getBuilding() == null || address.getBuilding().isEmpty());
    }

    private Address loadClientAddress(Long clientId) {
        try {
            return clientRepository.findById(clientId)
                    .map(Client::getAddress)
                    .orElse(null);
        } catch (Exception e) {
            log.warn("Cannot load address for client {}", clientId, e);
            return null;
        }
    }

    private String orderToJson(Order order) {
        return "{" +
                "\"orderId\":" + order.getId() + "," +
                "\"status\":\"" + (order.getStatus() != null ? order.getStatus().name() : "") + "\"," +
                "\"total\":" + (order.getTotalPrice() != null ? order.getTotalPrice() : 0) +
                "}";
    }

    private Long getClientId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long clientId = (Long) request.getSession().getAttribute("userId");
        if (clientId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            sendError(response, "Пользователь не авторизован");
        }
        return clientId;
    }

    private void sendJson(HttpServletResponse response, String payload) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(payload);
    }

    private void sendSuccess(HttpServletResponse response) throws IOException {
        sendJson(response, "{\"status\":\"ok\"}");
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"error\":\"" + escape(message) + "\"}");
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
