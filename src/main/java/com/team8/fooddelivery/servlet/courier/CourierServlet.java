package com.team8.fooddelivery.servlet.courier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team8.fooddelivery.model.courier.Courier;
import com.team8.fooddelivery.model.order.Order;
import com.team8.fooddelivery.service.CourierManagementService;
import com.team8.fooddelivery.service.CourierWorkService;
import com.team8.fooddelivery.service.OrderService;
import com.team8.fooddelivery.service.impl.CourierServiceImpl;
import com.team8.fooddelivery.service.impl.OrderServiceImpl;
import com.team8.fooddelivery.service.impl.CartServiceImpl;
import com.team8.fooddelivery.repository.ShopRepository;
import com.team8.fooddelivery.model.shop.Shop;
import com.team8.fooddelivery.util.SessionManager;
import com.team8.fooddelivery.util.PasswordAndTokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/courier/*")
public class CourierServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CourierServlet.class);

    private final CourierServiceImpl service = new CourierServiceImpl();
    private final CourierManagementService cms = service;
    private final CourierWorkService cws = service;
    private final OrderService orderService = new OrderServiceImpl(new CartServiceImpl());
    private final ShopRepository shopRepository = new ShopRepository();


    // ======================================================================
    // GET
    // ======================================================================

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();

        if (path == null) {
            response.sendError(404);
            return;
        }

        switch (path) {
            case "/dashboard" -> handleDashboard(request, response);
            case "/orders" -> handleOrders(request, response);
            case "/history" -> handleHistory(request, response);
            case "/earnings" -> handleEarnings(request, response);
            case "/today-history" -> handleTodayHistory(request, response);
            case "/active-order" -> handleActiveOrder(request, response);
            case "/register" -> request.getRequestDispatcher("/WEB-INF/jsp/courier/register.jsp").forward(request, response);
            case "/login" -> request.getRequestDispatcher("/WEB-INF/jsp/courier/login.jsp").forward(request, response);
            default -> response.sendError(404);
        }
    }


    // ======================================================================
    // POST
    // ======================================================================

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();

        try {
            switch (path) {
                case "/login" -> handleLogin(request, response);
                case "/register" -> handleRegister(request, response);
                case "/start-shift" -> handleStartShift(request, response);
                case "/end-shift" -> handleEndShift(request, response);
                case "/accept" -> handleAcceptOrder(request, response);
                case "/pickup" -> handlePickup(request, response);
                case "/complete" -> handleComplete(request, response);
                case "/status" -> handleStatus(request, response);
                case "/withdraw" -> handleWithdraw(request, response);
                default -> response.sendError(404);
            }
        } catch (Exception e) {
            log.error("CourierServlet error", e);
            response.sendError(500);
        }
    }


    // ======================================================================
    // REGISTRATION
    // ======================================================================

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String transport = request.getParameter("transportType");

        try {
            Long id = cms.registerNewCourier(name, phone, password, transport);

            log.info("New courier registered: {}", id);
            response.sendRedirect(request.getContextPath() + "/courier/login?registered=true");

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/courier/register.jsp").forward(request, response);
        }
    }


    // ======================================================================
    // LOGIN + JWT + HttpSession
    // ======================================================================

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        Courier courier = cms.login(phone, password);

        if (courier == null) {
            request.setAttribute("error", "Неверный телефон или пароль");
            request.getRequestDispatcher("/WEB-INF/jsp/courier/login.jsp").forward(request, response);
            return;
        }

        // Создаем сессию
        SessionManager.createSession(request.getSession(), courier);

        // Создаем JWT
        String token = PasswordAndTokenUtil.generateCourierToken(courier.getId(), courier.getPhoneNumber());
        request.getSession().setAttribute("token", token);

        log.info("Courier logged in: {}", phone);

        response.sendRedirect(request.getContextPath() + "/courier/dashboard");
    }


    // ======================================================================
    // START SHIFT
    // ======================================================================

    private void handleStartShift(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long courierId = SessionManager.getUserId(request.getSession());
        if (courierId == null) {
            response.sendRedirect(request.getContextPath() + "/courier/login");
            return;
        }

        cws.startShift(courierId);
        response.sendRedirect(request.getContextPath() + "/courier/dashboard?shift=started");
    }


    // ======================================================================
    // END SHIFT
    // ======================================================================

    private void handleEndShift(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long courierId = SessionManager.getUserId(request.getSession());
        if (courierId == null) {
            response.sendRedirect(request.getContextPath() + "/courier/login");
            return;
        }

        cws.endShift(courierId);
        response.sendRedirect(request.getContextPath() + "/courier/dashboard?shift=ended");
    }


    // ======================================================================
    // ACCEPT ORDER
    // ======================================================================

    private void handleAcceptOrder(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long courierId = SessionManager.getUserId(request.getSession());
        Long orderId = Long.parseLong(request.getParameter("orderId"));

        cws.acceptOrder(courierId, orderId);

        response.sendRedirect(request.getContextPath() + "/courier/dashboard?accepted=" + orderId);
    }


    // ======================================================================
    // PICKUP ORDER
    // ======================================================================

    private void handlePickup(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long courierId = SessionManager.getUserId(request.getSession());
        Long orderId = Long.parseLong(request.getParameter("orderId"));

        cws.pickupOrder(courierId, orderId);

        response.sendRedirect(request.getContextPath() + "/courier/dashboard?pickup=" + orderId);
    }


    // ======================================================================
    // COMPLETE ORDER
    // ======================================================================

    private void handleComplete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long courierId = SessionManager.getUserId(request.getSession());
        Long orderId = Long.parseLong(request.getParameter("orderId"));

        cws.completeOrder(courierId, orderId);

        response.sendRedirect(request.getContextPath() + "/courier/dashboard?completed=" + orderId);
    }


    // ======================================================================
    // DASHBOARD
    // ======================================================================

    private void handleDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long courierId = SessionManager.getUserId(request.getSession());
        if (courierId == null) {
            response.sendRedirect(request.getContextPath() + "/courier/login");
            return;
        }

        Courier courier = cms.getCourierById(courierId);

        request.setAttribute("courier", courier);
        request.getRequestDispatcher("/WEB-INF/jsp/courier/dashboard.jsp").forward(request, response);
    }


    // ======================================================================
    // AVAILABLE ORDERS
    // ======================================================================

    private void handleOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long courierId = SessionManager.getUserId(request.getSession());
        if (courierId == null) {
            response.sendRedirect(request.getContextPath() + "/courier/login");
            return;
        }

        List<Order> orders = orderService.getAvailableOrdersForCourier();
        
        // Получаем информацию о магазинах для каждого заказа
        Map<Long, Shop> shopsMap = new HashMap<>();
        for (Order order : orders) {
            if (order.getRestaurantId() != null && !shopsMap.containsKey(order.getRestaurantId())) {
                try {
                    shopRepository.findById(order.getRestaurantId())
                        .ifPresent(shop -> shopsMap.put(order.getRestaurantId(), shop));
                } catch (SQLException e) {
                    log.error("Ошибка при получении магазина {}", order.getRestaurantId(), e);
                }
            }
        }

        request.setAttribute("availableOrders", orders);
        request.setAttribute("shopsMap", shopsMap);
        request.getRequestDispatcher("/WEB-INF/jsp/courier/orders.jsp").forward(request, response);
    }


    // ======================================================================
    // DELIVERY HISTORY
    // ======================================================================

    private void handleHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long courierId = SessionManager.getUserId(request.getSession());
        if (courierId == null) {
            response.sendRedirect(request.getContextPath() + "/courier/login");
            return;
        }

        String dateStr = request.getParameter("date");
        LocalDate date = (dateStr == null || dateStr.isEmpty())
                ? LocalDate.now()
                : LocalDate.parse(dateStr);

        List<Order> history = cws.getOrderHistory(courierId);
        
        // Фильтруем по выбранной дате
        List<Order> filteredHistory = history.stream()
            .filter(order -> {
                if (order.getUpdatedAt() != null) {
                    LocalDate orderDate = order.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
                    return orderDate.equals(date);
                } else if (order.getCreatedAt() != null) {
                    LocalDate orderDate = order.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
                    return orderDate.equals(date);
                }
                return false;
            })
            .toList();
        
        // Получаем информацию о магазинах для каждого заказа
        Map<Long, Shop> shopsMap = new HashMap<>();
        for (Order order : filteredHistory) {
            if (order.getRestaurantId() != null && !shopsMap.containsKey(order.getRestaurantId())) {
                try {
                    shopRepository.findById(order.getRestaurantId())
                        .ifPresent(shop -> shopsMap.put(order.getRestaurantId(), shop));
                } catch (SQLException e) {
                    log.error("Ошибка при получении магазина {}", order.getRestaurantId(), e);
                }
            }
        }

        request.setAttribute("deliveryHistory", filteredHistory);
        request.setAttribute("selectedDate", date);
        request.setAttribute("shopsMap", shopsMap);
        request.getRequestDispatcher("/WEB-INF/jsp/courier/history.jsp").forward(request, response);
    }


    // ======================================================================
    // EARNINGS
    // ======================================================================

    private void handleEarnings(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long courierId = SessionManager.getUserId(request.getSession());
        if (courierId == null) {
            response.sendError(401);
            return;
        }

        List<Order> history = cws.getOrderHistory(courierId);
        double earnings = history.stream()
                .filter(o -> o.getUpdatedAt() != null && o.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDate().equals(LocalDate.now()))
                .mapToDouble(o -> 100.0) // Mock earnings per order
                .sum();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), Map.of("earnings", earnings));
    }


    // ======================================================================
    // TODAY'S DELIVERY HISTORY
    // ======================================================================

    private void handleTodayHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long courierId = SessionManager.getUserId(request.getSession());
        if (courierId == null) {
            response.sendError(401);
            return;
        }

        List<Order> history = cws.getOrderHistory(courierId);
        List<Order> todayHistory = history.stream()
                .filter(o -> o.getUpdatedAt() != null && o.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDate().equals(LocalDate.now()))
                .toList();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), todayHistory);
    }


    // ======================================================================
    // ACTIVE ORDER
    // ======================================================================

    private void handleActiveOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long courierId = SessionManager.getUserId(request.getSession());
        if (courierId == null) {
            response.sendError(401);
            return;
        }

        Order activeOrder = cws.getActiveOrderForCourier(courierId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), activeOrder);
    }


    // ======================================================================
    // STATUS UPDATE
    // ======================================================================

    private void handleStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long courierId = SessionManager.getUserId(request.getSession());
        if (courierId == null) {
            response.sendError(401);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> body = mapper.readValue(request.getInputStream(), Map.class);
        String newStatus = (String) body.get("newStatus");
        Long currentOrderId = body.get("currentOrderId") != null ? ((Number) body.get("currentOrderId")).longValue() : null;

        if ("online".equals(newStatus)) {
            cws.startShift(courierId);
        } else if ("offline".equals(newStatus)) {
            cws.endShift(courierId);
        } else if ("on_delivery".equals(newStatus) && currentOrderId != null) {
            cws.acceptOrder(courierId, currentOrderId);
        }

        response.setStatus(200);
    }


    // ======================================================================
    // WITHDRAW
    // ======================================================================

    private void handleWithdraw(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long courierId = SessionManager.getUserId(request.getSession());
        if (courierId == null) {
            response.sendRedirect(request.getContextPath() + "/courier/login");
            return;
        }

        cws.withdraw(courierId);
        response.sendRedirect(request.getContextPath() + "/courier/dashboard?withdrawn=true");
    }
}
