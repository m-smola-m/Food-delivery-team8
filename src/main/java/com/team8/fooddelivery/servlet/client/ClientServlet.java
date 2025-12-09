package com.team8.fooddelivery.servlet.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team8.fooddelivery.util.OrderHistoryResponse;
import com.team8.fooddelivery.util.NotificationDto;
import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.AuthResponse;
import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.model.client.ClientStatus;
import com.team8.fooddelivery.model.notification.Notification;
import com.team8.fooddelivery.model.order.Order;
import com.team8.fooddelivery.repository.ClientRepository;
import com.team8.fooddelivery.service.ClientService;
import com.team8.fooddelivery.service.NotificationService;
import com.team8.fooddelivery.service.OrderService;
import com.team8.fooddelivery.service.impl.CartServiceImpl;
import com.team8.fooddelivery.service.impl.ClientServiceImpl;
import com.team8.fooddelivery.service.impl.OrderServiceImpl;
import com.team8.fooddelivery.util.LoginPageDataProvider;
import com.team8.fooddelivery.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/client/*")
public class ClientServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ClientServlet.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.systemDefault());

    private final CartServiceImpl cartService = new CartServiceImpl();
    private final ClientService clientService = new ClientServiceImpl(new ClientRepository(), cartService);
    private final OrderService orderService = new OrderServiceImpl(cartService);
    private final NotificationService notificationService = NotificationService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // ====================================================================
    // GET
    // ====================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String path = request.getPathInfo();
        if (path == null) {
            response.sendError(404);
            return;
        }

        switch (path) {
            case "/profile" -> showProfile(request, response);
            case "/login" -> showLogin(request, response);
            case "/register" -> showRegister(request, response);
            case "/forgot_password" -> showForgotPassword(request, response);
            case "/home" -> showHome(request, response);
            case "/orders" -> response.sendRedirect(request.getContextPath() + "/client/home?tab=orders");
            case "/orders-api" -> sendOrderHistory(request, response);
            case "/notifications-api" -> sendNotifications(request, response);
            case "/profile-api" -> sendProfileData(request, response); // <-- Новый эндпоинт
            default -> response.sendError(404);
        }
    }

    private void sendProfileData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long userId = SessionManager.getUserId(request.getSession());
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Client client = clientService.getById(userId);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), client);
    }


    // ====================================================================
    // LOGIN & REGISTER PAGES
    // ====================================================================
    private void showLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        LoginPageDataProvider.attachClientDemoData(request, clientService);
        request.getRequestDispatcher("/WEB-INF/jsp/client/login.jsp").forward(request, response);
    }

    private void showRegister(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.getRequestDispatcher("/WEB-INF/jsp/client/register.jsp").forward(request, response);
    }

    private void showForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.getRequestDispatcher("/WEB-INF/jsp/client/forgot_password.jsp").forward(request, response);
    }


    // ====================================================================
    // POST
    // ====================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("doPost called with action: {}", request.getParameter("action"));
        String action = request.getParameter("action");

        String path = request.getPathInfo();
        if (path == null) {
            response.sendError(404);
            return;
        }

        try {
            switch (path) {
                case "/register" -> register(request, response);
                case "/login" -> login(request, response);
                case "/update-profile" -> updateProfile(request, response);
                case "/deactivate" -> deactivate(request, response);
                case "/notifications/readAll" -> markNotificationsRead(request, response);
                case "/orders/repeat" -> repeatOrder(request, response);
                default -> response.sendError(404);
            }

        } catch (Exception e) {
            log.error("Error in ClientServlet", e);
            response.sendError(500);
        }
    }



    // ====================================================================
    // ADDRESS BUILDER (обрабатывает ВСЕ поля адреса)
    // ====================================================================
    private Address extractAddress(HttpServletRequest request) {
        return Address.builder()
                .country(request.getParameter("country"))
                .city(request.getParameter("city"))
                .street(request.getParameter("street"))
                .building(request.getParameter("building"))
                .apartment(request.getParameter("apartment"))
                .entrance(request.getParameter("entrance"))
                .floor(parseInt(request.getParameter("floor")))
                .addressNote(request.getParameter("addressNote"))
                .district(request.getParameter("district"))
                .build();
    }

    private Integer parseInt(String v) {
        if (v == null) return null;
        String s = v.trim();
        if (s.isEmpty()) return null;
        try { return Integer.parseInt(s); }
        catch (Exception e) {
            // Возвращаем маркер неверного целочисленного значения — это позволит валидации поймать ошибку
            return Integer.MIN_VALUE;
        }
    }

    private Double parseDouble(String v) {
        try { return v == null ? null : Double.parseDouble(v); }
        catch (Exception e) { return null; }
    }



    // ====================================================================
    // REGISTER
    // ====================================================================
    private void register(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            Client client = clientService.register(
                    request.getParameter("phone"),
                    request.getParameter("password"),
                    request.getParameter("name"),
                    request.getParameter("email"),
                    extractAddress(request)
            );

            // сохраняем авторизованного клиента
            SessionManager.createSession(request.getSession(), client);

            response.sendRedirect(request.getContextPath() + "/client/home?registered=true");

        } catch (com.team8.fooddelivery.util.ValidationException ve) {
            log.warn("Registration validation failed: {}", ve.getFieldErrors());
            request.setAttribute("fieldErrors", ve.getFieldErrors());
            // keep submitted values
            request.setAttribute("formData", request.getParameterMap());
            request.getRequestDispatcher("/WEB-INF/jsp/client/register.jsp").forward(request, response);
        } catch (Exception e) {
            log.error("Registration failed", e);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/client/register.jsp").forward(request, response);
        }
    }



    // ====================================================================
    // LOGIN
    // ====================================================================
    private void login(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String login = request.getParameter("login");
        String password = request.getParameter("password");

        try {
            // Сначала найдем клиента, чтобы проверить его статус
            Client client = clientService.getByPhone(login);
            if (client == null) {
                client = clientService.getByEmail(login);
            }

            // Если клиент существует и он неактивен, выводим ошибку
            if (client != null && !client.isActive()) {
                request.setAttribute("error", "Ваш аккаунт деактивирован. Обратитесь в поддержку.");
                request.getRequestDispatcher("/WEB-INF/jsp/client/login.jsp").forward(request, response);
                return;
            }

            // Если клиент активен или не найден, продолжаем стандартную процедуру входа
            AuthResponse auth = clientService.login(login, password);
            Client loggedInClient = clientService.getById(auth.getClientId());

            SessionManager.createSession(request.getSession(), loggedInClient);

            // сохраняем JWT
            request.getSession().setAttribute("token", auth.getAuthToken());

            response.sendRedirect(request.getContextPath() + "/client/home");

        } catch (Exception e) {
            log.warn("Login failed: {}", e.getMessage());
            request.setAttribute("error", "Неверный логин или пароль");
            request.getRequestDispatcher("/WEB-INF/jsp/client/login.jsp").forward(request, response);
        }
    }



    // ====================================================================
    // PROFILE VIEW
    // ====================================================================
    private void showProfile(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        Long userId = SessionManager.getUserId(request.getSession());
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/client/login");
            return;
        }

        // Redirect to home with profile tab instead of separate profile page
        response.sendRedirect(request.getContextPath() + "/client/home?tab=profile");
    }



    // ====================================================================
    // UPDATE PROFILE
    // ====================================================================
    private void updateProfile(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        Long userId = SessionManager.getUserId(request.getSession());
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/client/login");
            return;
        }

        try {
            clientService.update(
                    userId,
                    null, // name is immutable from UI — ignore any client-provided value
                    request.getParameter("email"),
                    extractAddress(request)
            );

            // Если это AJAX-запрос — вернуть JSON успеха
            boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
                    || "application/json".equals(request.getHeader("Accept"))
                    || "1".equals(request.getParameter("ajax"));

            if (isAjax) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                java.util.Map<String, Object> out = new java.util.HashMap<>();
                out.put("success", true);
                out.put("message", "Профиль успешно обновлён");
                objectMapper.writeValue(response.getWriter(), out);
                return;
            }

            // Redirect to home profile tab with success flag
            response.sendRedirect(request.getContextPath() + "/client/home?tab=profile&updated=true");

        } catch (com.team8.fooddelivery.util.ValidationException ve) {
            log.warn("Update profile validation failed: {}", ve.getFieldErrors());
            // AJAX -> вернуть JSON с ошибками
            boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
                    || "application/json".equals(request.getHeader("Accept"))
                    || "1".equals(request.getParameter("ajax"));
            if (isAjax) {
                response.setStatus(400);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                java.util.Map<String, Object> out = new java.util.HashMap<>();
                out.put("success", false);
                out.put("fieldErrors", ve.getFieldErrors());
                objectMapper.writeValue(response.getWriter(), out);
                return;
            }

            // forward back to home with errors and keep values
            request.setAttribute("fieldErrors", ve.getFieldErrors());
            request.setAttribute("formData", request.getParameterMap());
            request.getRequestDispatcher("/WEB-INF/jsp/client/home.jsp").forward(request, response);
        } catch (Exception e) {
            log.error("Update failed", e);
            boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
                    || "application/json".equals(request.getHeader("Accept"))
                    || "1".equals(request.getParameter("ajax"));
            if (isAjax) {
                response.setStatus(500);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                java.util.Map<String, Object> out = new java.util.HashMap<>();
                out.put("success", false);
                out.put("message", e.getMessage() == null ? "Ошибка обновления" : e.getMessage());
                objectMapper.writeValue(response.getWriter(), out);
                return;
            }
            // Redirect back to home profile tab with error message (url-encoded)
            String msg = e.getMessage() == null ? "Ошибка обновления" : e.getMessage();
            response.sendRedirect(request.getContextPath() + "/client/home?tab=profile&error=" + java.net.URLEncoder.encode(msg, java.nio.charset.StandardCharsets.UTF_8));
        }
    }



    // ====================================================================
    // DEACTIVATE
    // ====================================================================
    private void deactivate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long userId = SessionManager.getUserId(request.getSession());

        if (userId != null) {
            clientService.deactivate(userId);
            // Обновляем статус в сессии
            request.getSession().setAttribute("clientStatus", ClientStatus.INACTIVE.name());
        }

        // Не инвалидируем сессию, чтобы пользователь видел сообщение
        // SessionManager.invalidateSession(request.getSession());
        response.sendRedirect(request.getContextPath() + "/client/home?deactivated=true");
    }



    // ====================================================================
    // HOME
    // ====================================================================
    private void showHome(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        Long userId = SessionManager.getUserId(request.getSession());

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/client/login");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/jsp/client/home.jsp").forward(request, response);
    }


    // ====================================================================
    // ORDER & NOTIFICATION HISTORY
    // ====================================================================
    private void sendOrderHistory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long userId = SessionManager.getUserId(request.getSession());
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        List<Order> orders = orderService.getOrdersByClient(userId);
        List<OrderHistoryResponse> responses = orders.stream()
                .map(order -> OrderHistoryResponse.builder()
                        .id(order.getId())
                        .status(order.getStatus() != null ? order.getStatus().name() : "")
                        .total(order.getTotalPrice())
                        .createdAt(order.getCreatedAt() != null ? FORMATTER.format(order.getCreatedAt()) : "")
                        .items(order.getItems().stream()
                                .map(item -> OrderHistoryResponse.Item.builder()
                                        .name(item.getProductName())
                                        .quantity(item.getQuantity())
                                        .price(item.getPrice())
                                        .build())
                                .toList())
                        .build())
                .toList();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), responses);
    }

    private void sendNotifications(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long userId = SessionManager.getUserId(request.getSession());
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        List<Notification> notifications = notificationService.getNotifications(userId);
        List<NotificationDto> dtos = notifications.stream()
                .map(n -> NotificationDto.builder()
                        .id(n.getId())
                        .clientId(n.getClientId())
                        .type(n.getType())
                        .message(n.getMessage())
                        .timestamp(n.getTimestamp() != null ? FORMATTER.format(n.getTimestamp().atZone(ZoneId.systemDefault())) : "")
                        .isRead(n.isRead())
                        .build())
                .toList();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), dtos);
    }

    private void markNotificationsRead(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long userId = SessionManager.getUserId(request.getSession());
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        try {
            notificationService.markAllAsRead(userId);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"status\":\"ok\"}");
        } catch (Exception e) {
            log.error("Failed to mark notifications as read for client {}", userId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"status\":\"error\", \"message\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    private void repeatOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long userId = SessionManager.getUserId(request.getSession());
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.trim().isEmpty() || "undefined".equalsIgnoreCase(orderIdStr)) {
            log.error("Не удалось повторить заказ: orderId is " + orderIdStr);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Order ID is missing or invalid.");
            return;
        }
        try {
            Long orderId = Long.parseLong(orderIdStr);
            orderService.repeatOrder(userId, orderId);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"ok\"}");
        } catch (NumberFormatException e) {
            log.error("Не удалось повторить заказ: неверный формат orderId", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + escape("Invalid Order ID format.") + "\"}");
        } catch (Exception e) {
            log.error("Не удалось повторить заказ", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + escape(e.getMessage()) + "\"}");
        }
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
