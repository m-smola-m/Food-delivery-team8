package com.team8.fooddelivery.filter;

import com.team8.fooddelivery.util.SessionManager;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

@WebFilter({"/client/*", "/courier/*", "/shop/*"})
public class AuthorizationFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);
    private static final Set<String> ROLELESS_PATHS = Set.of(
        "/client/login",
        "/client/register",
        "/courier/login",
        "/shop/login",
        "/shop/register",
        "/shop/list",
        "/shop/details",
        "/shop/list-api"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Убираем contextPath из URI для проверки
        String path = requestURI;
        if (contextPath != null && !contextPath.isEmpty() && requestURI.startsWith(contextPath)) {
            path = requestURI.substring(contextPath.length());
        }

        // Проверяем публичные пути
        if (ROLELESS_PATHS.stream().anyMatch(path::endsWith) || ROLELESS_PATHS.stream().anyMatch(path::equals)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session == null || !SessionManager.isAuthenticated(session)) {
            log.warn("Unauthorized access attempt (no session) to {}", requestURI);
            // Определяем, куда редиректить в зависимости от пути
            if (path.contains("/shop/") || path.startsWith("/shop/")) {
                httpResponse.sendRedirect(contextPath + "/shop/login");
            } else if (path.contains("/courier/") || path.startsWith("/courier/")) {
                httpResponse.sendRedirect(contextPath + "/courier/login");
            } else {
                httpResponse.sendRedirect(contextPath + "/client/login");
            }
            return;
        }

        String userRole = SessionManager.getUserRole(session);
        Long userId = SessionManager.getUserId(session);
        log.debug("AuthorizationFilter session userId={}, role={} for URI {} (path: {})", userId, userRole, requestURI, path);

        if (userRole == null) {
            log.warn("Session lost role information for user {}, forcing logout", userId);
            SessionManager.invalidateSession(session);
            // Определяем, куда редиректить в зависимости от пути
            if (path.contains("/shop/") || path.startsWith("/shop/")) {
                httpResponse.sendRedirect(contextPath + "/shop/login");
            } else if (path.contains("/courier/") || path.startsWith("/courier/")) {
                httpResponse.sendRedirect(contextPath + "/courier/login");
            } else {
                httpResponse.sendRedirect(contextPath + "/client/login");
            }
            return;
        }

        // Проверяем доступ к ресурсам клиента
        if ((path.contains("/client/") || path.startsWith("/client/")) && !"CLIENT".equals(userRole)) {
            log.warn("Forbidden: role {} trying to access client resource: {}", userRole, requestURI);
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Проверяем доступ к ресурсам курьера
        if ((path.contains("/courier/") || path.startsWith("/courier/")) && !"COURIER".equals(userRole)) {
            log.warn("Forbidden: role {} trying to access courier resource: {}", userRole, requestURI);
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Проверяем доступ к ресурсам магазина
        if ((path.contains("/shop/") || path.startsWith("/shop/")) && !"SHOP".equals(userRole)) {
            log.warn("Forbidden: role {} trying to access shop resource: {}", userRole, requestURI);
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        log.debug("AuthorizationFilter allowed user {} ({}) to access {}", userId, userRole, requestURI);
        chain.doFilter(request, response);
    }
}
