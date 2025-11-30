package com.team8.fooddelivery.servlet.shop;

import com.team8.fooddelivery.model.shop.Shop;
import com.team8.fooddelivery.service.ShopInfoService;
import com.team8.fooddelivery.service.impl.ShopInfoServiceImpl;
import com.team8.fooddelivery.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/shop/*")
public class ShopServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ShopServlet.class);
    private final ShopInfoService shopService = new ShopInfoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if ("/list".equals(pathInfo)) {
            handleShopList(request, response);
        } else if ("/details".equals(pathInfo)) {
            handleShopDetails(request, response);
        } else if ("/dashboard".equals(pathInfo)) {
            handleDashboard(request, response);
        } else {
            response.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        try {
            if ("/register".equals(pathInfo)) {
                handleRegister(request, response);
            } else if ("/login".equals(pathInfo)) {
                handleLogin(request, response);
            } else if ("/update-status".equals(pathInfo)) {
                handleUpdateStatus(request, response);
            } else {
                response.sendError(404);
            }
        } catch (Exception e) {
            log.error("Error", e);
            response.sendError(500);
        }
    }

    private void handleShopList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Shop> shops = shopService.getAllShops();
            request.setAttribute("shops", shops);
            request.getRequestDispatcher("/WEB-INF/jsp/shop/list.jsp").forward(request, response);
        } catch (Exception e) {
            log.error("Error loading shop list", e);
            response.sendError(500);
        }
    }

    private void handleShopDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Long shopId = Long.parseLong(request.getParameter("id"));
            Optional<Shop> shop = shopService.getShopById(shopId);

            if (shop.isPresent()) {
                request.setAttribute("shop", shop.get());
                request.getRequestDispatcher("/WEB-INF/jsp/shop/details.jsp").forward(request, response);
            } else {
                response.sendError(404);
            }
        } catch (Exception e) {
            log.error("Error loading shop details", e);
            response.sendError(500);
        }
    }

    private void handleDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long shopId = (Long) request.getSession().getAttribute("shopId");
        if (shopId == null) {
            response.sendRedirect(request.getContextPath() + "/shop/login");
            return;
        }

        try {
            Optional<Shop> shop = shopService.getShopById(shopId);
            if (shop.isPresent()) {
                request.setAttribute("shop", shop.get());
                request.getRequestDispatcher("/WEB-INF/jsp/shop/dashboard.jsp").forward(request, response);
            } else {
                response.sendError(404);
            }
        } catch (Exception e) {
            log.error("Error loading dashboard", e);
            response.sendError(500);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String naming = request.getParameter("naming");
        String email = request.getParameter("emailForAuth");
        String phone = request.getParameter("phoneForAuth");
        String password = request.getParameter("password");

        try {
            if (naming == null || naming.isEmpty() || email == null || email.isEmpty()) {
                request.setAttribute("error", "Обязательные поля не заполнены");
                request.getRequestDispatcher("/WEB-INF/jsp/shop/register.jsp").forward(request, response);
                return;
            }

            // TODO: Реализовать регистрацию магазина
            log.info("Shop registration requested: {}", email);

            request.setAttribute("success", "Регистрация успешна. Ожидайте проверки администратора.");
            request.getRequestDispatcher("/WEB-INF/jsp/shop/register.jsp").forward(request, response);
        } catch (Exception e) {
            log.error("Registration error", e);
            request.setAttribute("error", "Ошибка при регистрации");
            request.getRequestDispatcher("/WEB-INF/jsp/shop/register.jsp").forward(request, response);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                request.setAttribute("error", "Email и пароль обязательны");
                request.getRequestDispatcher("/WEB-INF/jsp/shop/login.jsp").forward(request, response);
                return;
            }

            // TODO: Реализовать аутентификацию магазина
            log.info("Shop login attempted: {}", email);
            response.sendRedirect(request.getContextPath() + "/shop/dashboard");
        } catch (Exception e) {
            log.error("Login error", e);
            request.setAttribute("error", "Ошибка при входе");
            request.getRequestDispatcher("/WEB-INF/jsp/shop/login.jsp").forward(request, response);
        }
    }

    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long shopId = (Long) request.getSession().getAttribute("shopId");
        if (shopId == null) {
            response.sendRedirect(request.getContextPath() + "/shop/login");
            return;
        }

        try {
            String newStatus = request.getParameter("status");
            // TODO: Реализовать обновление статуса
            log.info("Shop status update: {} -> {}", shopId, newStatus);
            response.sendRedirect(request.getContextPath() + "/shop/dashboard?updated=true");
        } catch (Exception e) {
            log.error("Error updating status", e);
            response.sendError(500);
        }
    }
}

