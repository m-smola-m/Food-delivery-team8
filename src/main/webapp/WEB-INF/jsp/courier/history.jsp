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
            <div class="alert alert-info">Нет доставок на <fmt:formatDate value="${selectedDate}" pattern="dd.MM.yyyy"/></div>
        </c:if>

        <c:if test="${not empty deliveryHistory}">
            <div class="history-section">
                <h2><fmt:formatDate value="${selectedDate}" pattern="dd MMMM yyyy"/></h2>

                <table class="delivery-table">
                    <thead>
                        <tr>
                            <th>Номер заказа</th>
                            <th>Время</th>
                            <th>Адреса (из -> в)</th>
                            <th>Сумма</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="order" items="${deliveryHistory}">
                            <tr>
                                <td>#${order.id}</td>
                                <td>
                                    <fmt:formatDate value="${order.completedAt}" pattern="HH:mm"/>
                                </td>
                                <td>
                                    ${order.shopAddress} → ${order.deliveryAddress}
                                </td>
                                <td>${order.totalPrice} ₽</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <div class="summary">
                    <h3>Итого за день:</h3>
                    <c:set var="total" value="0"/>
                    <c:forEach var="order" items="${deliveryHistory}">
                        <c:set var="total" value="${total + order.totalPrice}"/>
                    </c:forEach>
                    <p><strong>Заказов:</strong> ${deliveryHistory.size()}</p>
                    <p><strong>Сумма:</strong> ${total} ₽</p>
                </div>
            </div>
        </c:if>

        <a href="${pageContext.request.contextPath}/courier/dashboard" class="btn btn-secondary">Назад</a>
    </main>
</body>
</html>

