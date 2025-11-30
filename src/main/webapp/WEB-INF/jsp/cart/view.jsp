<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Корзина - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/layout/navbar.jsp" %>

    <main class="container">
        <h1>Ваша корзина</h1>

        <div class="cart-container">
            <div class="cart-items">
                <c:if test="${empty cartItems}">
                    <p class="empty-cart">Корзина пуста</p>
                    <a href="${pageContext.request.contextPath}/client/home" class="btn btn-primary">
                        Продолжить покупки
                    </a>
                </c:if>

                <c:if test="${not empty cartItems}">
                    <table class="cart-table">
                        <thead>
                            <tr>
                                <th>Товар</th>
                                <th>Цена</th>
                                <th>Количество</th>
                                <th>Итого</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${cartItems}">
                                <tr class="cart-item" data-item-id="${item.id}">
                                    <td>${item.productName}</td>
                                    <td>${item.price} ₽</td>
                                    <td>
                                        <input type="number" class="quantity" value="${item.quantity}" min="1">
                                    </td>
                                    <td>${item.price * item.quantity} ₽</td>
                                    <td>
                                        <button class="btn-remove" onclick="removeFromCart(${item.id})">Удалить</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <div class="cart-summary">
                        <p>Итого: <strong>${total} ₽</strong></p>
                        <a href="${pageContext.request.contextPath}/cart/checkout" class="btn btn-primary btn-block">
                            Оформить заказ
                        </a>
                    </div>
                </c:if>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>

    <script src="${pageContext.request.contextPath}/resources/js/cart.js"></script>
</body>
</html>

