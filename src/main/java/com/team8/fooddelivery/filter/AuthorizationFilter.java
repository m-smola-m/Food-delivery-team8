package com.team8.fooddelivery.filter;

import com.team8.fooddelivery.util.SessionManager;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebFilter({"/client/*", "/courier/*"})
public class AuthorizationFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String userRole = SessionManager.getUserRole(httpRequest.getSession());
        String requestURI = httpRequest.getRequestURI();

        if (requestURI.contains("/client/") && !"CLIENT".equals(userRole)) {
            log.warn("Unauthorized role access: {} trying to access {}", userRole, requestURI);
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (requestURI.contains("/courier/") && !"COURIER".equals(userRole)) {
            log.warn("Unauthorized role access: {} trying to access {}", userRole, requestURI);
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }
}

