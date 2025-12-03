package com.team8.fooddelivery.servlet;

import com.team8.fooddelivery.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(LogoutServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Logout request received");

        // Завершаем сессию пользователя
        SessionManager.invalidateSession(req.getSession());
        log.info("Session invalidated");

        // Показываем красивую страницу подтверждения выхода
        log.info("Forwarding to logout.jsp");
        req.getRequestDispatcher("/WEB-INF/jsp/logout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}

