<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Доступные заказы - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="container">
            <h1>Food Delivery Курьер</h1>
            <ul>
                <li><a href="${pageContext.request.contextPath}/courier/dashboard">Главная</a></li>
                <li><a href="${pageContext.request.contextPath}/courier/orders">Заказы</a></li>
                <li><a href="${pageContext.request.contextPath}/courier/history">История</a></li>
            </ul>
        </div>
    </nav>

    <main class="container">
        <h1>Доступные заказы</h1>
        <p>Выберите заказ, который готовы забрать и доставить.</p>

        <c:if test="${empty availableOrders}">
            <div class="alert alert-info">Нет доступных заказов</div>
        </c:if>

        <c:if test="${not empty availableOrders}">
            <div class="orders-list">
                <c:forEach var="order" items="${availableOrders}">
                    <div class="order-card">
                        <h3>Заказ #${order.id}</h3>
                        
                        <c:set var="shop" value="${shopsMap[order.restaurantId]}"/>
                        <c:if test="${not empty shop}">
                            <p><strong>Магазин:</strong> ${shop.naming}</p>
                            <c:if test="${not empty shop.address}">
                                <p><strong>Откуда (адрес магазина):</strong> 
                                    ${shop.address.city}, ${shop.address.street}, д. ${shop.address.building}
                                    <c:if test="${not empty shop.address.apartment}">, кв. ${shop.address.apartment}</c:if>
                                </p>
                            </c:if>
                        </c:if>
                        
                        <c:if test="${not empty order.deliveryAddress}">
                            <p><strong>Куда (адрес доставки):</strong> 
                                ${order.deliveryAddress.city}, ${order.deliveryAddress.street}, д. ${order.deliveryAddress.building}
                                <c:if test="${not empty order.deliveryAddress.apartment}">, кв. ${order.deliveryAddress.apartment}</c:if>
                                <c:if test="${not empty order.deliveryAddress.addressNote}"> (${order.deliveryAddress.addressNote})</c:if>
                            </p>
                        </c:if>
                        
                        <c:if test="${not empty order.items}">
                            <p><strong>Состав заказа:</strong></p>
                            <ul>
                                <c:forEach var="item" items="${order.items}">
                                    <li>${item.productName} x${item.quantity} - ${item.price} ₽</li>
                                </c:forEach>
                            </ul>
                        </c:if>
                        
                        <p><strong>Сумма:</strong> ${order.totalPrice} ₽</p>
                        <p><strong>Статус:</strong> ${order.status}</p>

                        <form method="POST" action="${pageContext.request.contextPath}/courier/accept">
                            <input type="hidden" name="orderId" value="${order.id}">
                            <button type="submit" class="btn btn-success btn-large">ПРИНЯТЬ ЗАКАЗ</button>
                        </form>
                    </div>
                </c:forEach>
            </div>
        </c:if>

        <a href="${pageContext.request.contextPath}/courier/dashboard" class="btn btn-secondary">Назад</a>
    </main>
</body>
</html>
