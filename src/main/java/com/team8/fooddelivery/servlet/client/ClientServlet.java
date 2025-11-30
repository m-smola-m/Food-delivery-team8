package com.team8.fooddelivery.servlet.client;

import com.team8.fooddelivery.model.Address;
import com.team8.fooddelivery.model.AuthResponse;
import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.service.ClientService;
import com.team8.fooddelivery.service.impl.ClientServiceImpl;
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

@WebServlet("/client/*")
public class ClientServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ClientServlet.class);

    private final ClientService clientService = new ClientServiceImpl(new CartServiceImpl());


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
            case "/home" -> showHome(request, response);
            default -> response.sendError(404);
        }
    }


    // ====================================================================
    // POST
    // ====================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

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
                .latitude(parseDouble(request.getParameter("latitude")))
                .longitude(parseDouble(request.getParameter("longitude")))
                .addressNote(request.getParameter("addressNote"))
                .district(request.getParameter("district"))
                .build();
    }

    private Integer parseInt(String v) {
        try { return v == null ? null : Integer.parseInt(v); }
        catch (Exception e) { return null; }
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
            AuthResponse auth = clientService.login(login, password);
            Client client = clientService.getById(auth.getClientId());

            SessionManager.createSession(request.getSession(), client);

            // сохраняем JWT
            request.getSession().setAttribute("token", auth.getAuthToken());

            response.sendRedirect(request.getContextPath() + "/client/home");

        } catch (Exception e) {
            log.warn("Login failed: {}", e.getMessage());
            request.setAttribute("error", e.getMessage());
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

        Client client = clientService.getById(userId);
        if (client == null) {
            response.sendError(404);
            return;
        }

        request.setAttribute("client", client);
        request.getRequestDispatcher("/WEB-INF/jsp/client/profile.jsp").forward(request, response);
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
                    request.getParameter("name"),
                    request.getParameter("email"),
                    extractAddress(request)
            );

            response.sendRedirect(request.getContextPath() + "/client/profile?updated=true");

        } catch (Exception e) {
            log.error("Update failed", e);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/client/profile.jsp").forward(request, response);
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
        }

        SessionManager.invalidateSession(request.getSession());
        response.sendRedirect(request.getContextPath() + "/client/login?deactivated=true");
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
}
