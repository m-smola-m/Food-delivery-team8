package com.team8.fooddelivery.servlet.auth;

import com.team8.fooddelivery.model.AuthResponse;
import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.model.courier.Courier;
import com.team8.fooddelivery.model.shop.Shop;
import com.team8.fooddelivery.repository.ClientRepository;
import com.team8.fooddelivery.service.impl.ClientServiceImpl;
import com.team8.fooddelivery.service.impl.CourierServiceImpl;
import com.team8.fooddelivery.service.impl.ShopInfoServiceImpl;
import com.team8.fooddelivery.service.impl.CartServiceImpl;
import com.team8.fooddelivery.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/register", "/auth/*"})
public class AuthServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(AuthServlet.class);

    private final ClientServiceImpl clientService = new ClientServiceImpl(new ClientRepository(), new CartServiceImpl());
    private final CourierServiceImpl courierService = new CourierServiceImpl();
    private final ShopInfoServiceImpl shopInfoService = new ShopInfoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String role = req.getParameter("role");

        if (path.equals("/register")) {
            req.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp").forward(req, resp);
            return;
        }

        if (role == null) {
            req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
            return;
        }

        switch (role) {
            case "CLIENT" -> req.getRequestDispatcher("/WEB-INF/jsp/client/login.jsp").forward(req, resp);
            case "COURIER" -> req.getRequestDispatcher("/WEB-INF/jsp/courier/login.jsp").forward(req, resp);
            case "SHOP" -> req.getRequestDispatcher("/WEB-INF/jsp/shop/login.jsp").forward(req, resp);
            default -> req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        if ("/register".equals(servletPath)) {
            handleClientRegistration(req, resp);
            return;
        }

        String role = req.getParameter("role");
        if (role == null && req.getParameter("login") != null) {
            role = "CLIENT";
        }
        if (role == null) {
            req.setAttribute("error", "Выберите роль для входа");
            req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
            return;
        }

        switch (role) {
            case "CLIENT" -> handleClientLogin(req, resp);
            case "COURIER" -> handleCourierLogin(req, resp);
            case "SHOP" -> handleShopLogin(req, resp);
            default -> {
                req.setAttribute("error", "Неизвестная роль");
                req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
            }
        }
    }

    private void handleClientRegistration(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Client client = clientService.register(
                    req.getParameter("phone"),
                    req.getParameter("password"),
                    req.getParameter("name"),
                    req.getParameter("email"),
                    null
            );
            SessionManager.createSession(req.getSession(), client);
            resp.sendRedirect(req.getContextPath() + "/client/home?registered=true");
        } catch (Exception e) {
            log.error("Client registration failed", e);
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp").forward(req, resp);
        }
    }

    private void handleClientLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            AuthResponse auth = clientService.login(req.getParameter("login"), req.getParameter("password"));
            Client client = clientService.getById(auth.getClientId());
            SessionManager.createSession(req.getSession(), client);
            req.getSession().setAttribute("token", auth.getAuthToken());
            resp.sendRedirect(req.getContextPath() + "/client/home");
        } catch (Exception e) {
            log.warn("Client login failed", e);
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/client/login.jsp").forward(req, resp);
        }
    }

    private void handleCourierLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Courier courier = courierService.login(req.getParameter("phone"), req.getParameter("password"));
            SessionManager.createSession(req.getSession(), courier);
            resp.sendRedirect(req.getContextPath() + "/courier/dashboard");
        } catch (Exception e) {
            log.warn("Courier login failed", e);
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/courier/login.jsp").forward(req, resp);
        }
    }

    private void handleShopLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Shop shop = shopInfoService.login(req.getParameter("login"), req.getParameter("password"));
            SessionManager.createSession(req.getSession(), shop);
            resp.sendRedirect(req.getContextPath() + "/shop/dashboard");
        } catch (Exception e) {
            log.warn("Shop login failed", e);
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/shop/login.jsp").forward(req, resp);
        }
    }
}
