package com.team8.fooddelivery.servlet.cart;

import com.team8.fooddelivery.model.product.Cart;
import com.team8.fooddelivery.model.product.CartItem;
import com.team8.fooddelivery.service.CartService;
import com.team8.fooddelivery.service.impl.CartServiceImpl;
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

@WebServlet("/cart/*")
public class CartServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(CartServlet.class);
    private final CartService cartService = new CartServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if ("/view".equals(pathInfo)) {
            handleViewCart(request, response);
        } else {
            response.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        try {
            if ("/add".equals(pathInfo)) {
                handleAddToCart(request, response);
            } else if ("/remove".equals(pathInfo)) {
                handleRemoveFromCart(request, response);
            } else if ("/update".equals(pathInfo)) {
                handleUpdateQuantity(request, response);
            } else if ("/add-comment".equals(pathInfo)) {
                handleAddComment(request, response);
            } else {
                response.sendError(404);
            }
        } catch (Exception e) {
            log.error("Error", e);
            response.sendError(500);
        }
    }

    /**
     * Показать содержимое корзины
     */
    private void handleViewCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long clientId = (Long) request.getSession().getAttribute("userId");
        if (clientId == null) {
            response.sendRedirect(request.getContextPath() + "/client/login");
            return;
        }

        try {
            // TODO: Реализовать получение корзины клиента
            // Cart cart = cartService.getCartByClientId(clientId);
            // List<CartItem> items = cartService.getCartItems(cart.getCartId());

            request.setAttribute("cart", null);
            request.setAttribute("cartItems", List.of());
            request.setAttribute("total", 0.0);

            request.getRequestDispatcher("/WEB-INF/jsp/cart/view.jsp").forward(request, response);
        } catch (Exception e) {
            log.error("Error loading cart", e);
            response.sendError(500);
        }
    }

    /**
     * Добавить товар в корзину
     */
    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long clientId = (Long) request.getSession().getAttribute("userId");
        if (clientId == null) {
            response.sendRedirect(request.getContextPath() + "/client/login");
            return;
        }

        try {
            Long productId = Long.parseLong(request.getParameter("productId"));
            int quantity = Integer.parseInt(request.getParameter("quantity") != null ?
                    request.getParameter("quantity") : "1");

            // TODO: Реализовать добавление товара в корзину
            // Cart cart = cartService.getCartByClientId(clientId);
            // cartService.addItem(cart.getCartId(), productId, quantity);

            log.info("Product {} added to cart for client {}", productId, clientId);
            response.sendRedirect(request.getContextPath() + "/cart/view?added=true");
        } catch (Exception e) {
            log.error("Error adding to cart", e);
            request.setAttribute("error", "Ошибка при добавлении товара");
            request.getRequestDispatcher("/WEB-INF/jsp/cart/view.jsp").forward(request, response);
        }
    }

    /**
     * Удалить товар из корзины
     */
    private void handleRemoveFromCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long clientId = (Long) request.getSession().getAttribute("userId");
        if (clientId == null) {
            response.sendRedirect(request.getContextPath() + "/client/login");
            return;
        }

        try {
            Long cartItemId = Long.parseLong(request.getParameter("cartItemId"));

            // TODO: Реализовать удаление товара
            // cartService.removeItem(cartItemId);

            log.info("Item {} removed from cart", cartItemId);
            response.sendRedirect(request.getContextPath() + "/cart/view?removed=true");
        } catch (Exception e) {
            log.error("Error removing from cart", e);
            response.sendRedirect(request.getContextPath() + "/cart/view?error=true");
        }
    }

    /**
     * Обновить количество товара
     */
    private void handleUpdateQuantity(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long clientId = (Long) request.getSession().getAttribute("userId");
        if (clientId == null) {
            response.sendRedirect(request.getContextPath() + "/client/login");
            return;
        }

        try {
            Long cartItemId = Long.parseLong(request.getParameter("cartItemId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            if (quantity <= 0) {
                handleRemoveFromCart(request, response);
                return;
            }

            // TODO: Реализовать обновление количества
            // cartService.updateItem(cartItemId, quantity);

            log.info("Item {} quantity updated to {}", cartItemId, quantity);
            response.sendRedirect(request.getContextPath() + "/cart/view?updated=true");
        } catch (Exception e) {
            log.error("Error updating quantity", e);
            response.sendRedirect(request.getContextPath() + "/cart/view?error=true");
        }
    }

    /**
     * Добавить комментарий к товару в корзине
     */
    private void handleAddComment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long clientId = (Long) request.getSession().getAttribute("userId");
        if (clientId == null) {
            response.sendRedirect(request.getContextPath() + "/client/login");
            return;
        }

        try {
            Long cartItemId = Long.parseLong(request.getParameter("cartItemId"));
            String comment = request.getParameter("comment");

            // TODO: Реализовать добавление комментария
            // CartItem item = cartService.getCartItem(cartItemId);
            // item.setComment(comment);
            // cartService.updateItem(cartItemId, item);

            log.info("Comment added to item {}", cartItemId);
            response.sendRedirect(request.getContextPath() + "/cart/view?comment=added");
        } catch (Exception e) {
            log.error("Error adding comment", e);
            response.sendRedirect(request.getContextPath() + "/cart/view?error=true");
        }
    }
}

