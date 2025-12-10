package com.team8.fooddelivery.util;

import com.team8.fooddelivery.model.client.Client;
import com.team8.fooddelivery.model.courier.Courier;
import com.team8.fooddelivery.model.shop.Shop;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {
    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    private static final long SESSION_TIMEOUT = 30 * 60;

    public static void createSession(HttpSession session, Client client) {
        session.setAttribute("userId", client.getId());
        session.setAttribute("userRole", "CLIENT");
        session.setAttribute("userName", client.getName());
        session.setAttribute("userEmail", client.getEmail());
        session.setAttribute("clientStatus", client.getStatus().name()); // <-- Добавлено
        session.setMaxInactiveInterval((int) SESSION_TIMEOUT);
        log.debug("Created client session for user: {}", client.getId());
    }

    public static void createSession(HttpSession session, Courier courier) {
        session.setAttribute("userId", courier.getId());
        session.setAttribute("userRole", "COURIER");
        session.setAttribute("userName", courier.getName());
        session.setMaxInactiveInterval((int) SESSION_TIMEOUT);
        log.debug("Created courier session for user: {}", courier.getId());
    }

    public static void createSession(HttpSession session, Shop shop) {
        session.setAttribute("userId", shop.getShopId());
        session.setAttribute("shopId", shop.getShopId()); // Для обратной совместимости
        session.setAttribute("userRole", "SHOP");
        session.setAttribute("userName", shop.getNaming());
        session.setMaxInactiveInterval((int) SESSION_TIMEOUT);
        log.debug("Created shop session for user: {}", shop.getShopId());
    }

    public static void invalidateSession(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        session.invalidate();
        log.debug("Invalidated session for user: {}", userId);
    }

    public static boolean isAuthenticated(HttpSession session) {
        return session != null && session.getAttribute("userId") != null;
    }

    public static String getUserRole(HttpSession session) {
        return (String) session.getAttribute("userRole");
    }

    public static Long getUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }
}
