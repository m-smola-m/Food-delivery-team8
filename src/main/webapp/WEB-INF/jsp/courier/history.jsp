<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>История доставок - Food Delivery</title>
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
        <h1>История доставок</h1>

        <!-- Выбор даты -->
        <form method="GET" action="${pageContext.request.contextPath}/courier/history" class="date-selector">
            <label>Дата:</label>
            <input type="date" name="date" value="${selectedDate}" onchange="this.form.submit()">
        </form>

        <c:if test="${empty deliveryHistory}">
            <div class="alert alert-info">Нет доставок на выбранную дату</div>
        </c:if>

        <c:if test="${not empty deliveryHistory}">
            <div class="history-section">
                <h2>История за ${selectedDate}</h2>

                <div class="orders-list">
                    <c:forEach var="order" items="${deliveryHistory}">
                        <div class="order-card">
                            <h3>Заказ #${order.id}</h3>
                            
                            <c:set var="shop" value="${shopsMap[order.restaurantId]}"/>
                            <c:if test="${not empty shop}">
                                <p><strong>Магазин:</strong> ${shop.naming}</p>
                                <c:if test="${not empty shop.address}">
                                    <p><strong>Откуда:</strong> 
                                        ${shop.address.city}, ${shop.address.street}, д. ${shop.address.building}
                                        <c:if test="${not empty shop.address.apartment}">, кв. ${shop.address.apartment}</c:if>
                                    </p>
                                </c:if>
                            </c:if>
                            
                            <c:if test="${not empty order.deliveryAddress}">
                                <p><strong>Куда:</strong> 
                                    ${order.deliveryAddress.city}, ${order.deliveryAddress.street}, д. ${order.deliveryAddress.building}
                                    <c:if test="${not empty order.deliveryAddress.apartment}">, кв. ${order.deliveryAddress.apartment}</c:if>
                                </p>
                            </c:if>
                            
                            <c:if test="${not empty order.items}">
                                <p><strong>Состав:</strong>
                                    <c:forEach var="item" items="${order.items}" varStatus="status">
                                        ${item.productName} x${item.quantity}<c:if test="${!status.last}">, </c:if>
                                    </c:forEach>
                                </p>
                            </c:if>
                            
                            <p><strong>Сумма:</strong> ${order.totalPrice} ₽</p>
                            <p><strong>Статус:</strong> ${order.status}</p>
                            
                            <c:if test="${not empty order.updatedAt}">
                                <p><strong>Завершен:</strong> ${order.updatedAt}</p>
                            </c:if>
                            <c:if test="${empty order.updatedAt && not empty order.createdAt}">
                                <p><strong>Создан:</strong> ${order.createdAt}</p>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>

                <div class="summary">
                    <h3>Итого за день:</h3>
                    <c:set var="total" value="0"/>
                    <c:forEach var="order" items="${deliveryHistory}">
                        <c:set var="total" value="${total + order.totalPrice}"/>
                    </c:forEach>
                    <p><strong>Заказов:</strong> ${deliveryHistory.size()}</p>
                    <p><strong>Общая сумма:</strong> ${total} ₽</p>
                </div>
            </div>
        </c:if>

        <a href="${pageContext.request.contextPath}/courier/dashboard" class="btn btn-secondary">Назад</a>
    </main>
</body>
</html>

