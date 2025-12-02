package com.team8.fooddelivery.filter;

import com.team8.fooddelivery.util.SessionManager;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

public class AuthenticationFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "",
            "/",
            "/index.jsp",
            "/login",
            "/register",
            "/auth",
            "/client/login",
            "/client/register",
            "/shop/login",
            "/shop/register",
            "/shop/list",
            "/courier/login"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = extractPath(httpRequest);
        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session == null || !SessionManager.isAuthenticated(session)) {
            log.debug("Unauthenticated request to {}, redirecting to /login", path);
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    private String extractPath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && requestURI.startsWith(contextPath)) {
            return requestURI.substring(contextPath.length());
        }
        return requestURI;
    }

    private boolean isPublic(String path) {
        if (path == null || PUBLIC_PATHS.contains(path)) {
            return true;
        }
        return path.startsWith("/resources/") || path.startsWith("/auth/");
    }
}

