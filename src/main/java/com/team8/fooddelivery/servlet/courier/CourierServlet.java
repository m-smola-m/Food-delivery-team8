package com.team8.fooddelivery.servlet.courier;

import com.team8.fooddelivery.model.courier.Courier;
import com.team8.fooddelivery.model.order.Order;
import com.team8.fooddelivery.service.CourierManagementService;
import com.team8.fooddelivery.service.CourierWorkService;
import com.team8.fooddelivery.service.OrderService;
import com.team8.fooddelivery.service.impl.CourierServiceImpl;
import com.team8.fooddelivery.service.impl.OrderServiceImpl;
import com.team8.fooddelivery.service.impl.CartServiceImpl;
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
import java.time.LocalDate;
import java.util.List;

@WebServlet("/courier/*")
public class CourierServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(CourierServlet.class);

    private final CourierServiceImpl service = new CourierServiceImpl();
    private final CourierManagementService cms = service;
    private final CourierWorkService cws = service;
    private final OrderService orderService = new OrderServiceImpl(new CartServiceImpl());


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

        request.setAttribute("availableOrders", orders);
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

        request.setAttribute("deliveryHistory", history);
        request.setAttribute("selectedDate", date);
        request.getRequestDispatcher("/WEB-INF/jsp/courier/history.jsp").forward(request, response);
    }
}
