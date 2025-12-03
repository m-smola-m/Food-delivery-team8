<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>История заказов - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="container">
    <header>
        <h1>История заказов</h1>
        <p>Курьер: <span id="courierName">${sessionScope.courier.name}</span></p>
        <a href="${pageContext.request.contextPath}/courier/dashboard" class="btn btn-secondary">Назад к панели</a>
    </header>

    <div class="date-selector">
        <label for="date">Выберите дату:</label>
        <input type="date" id="date" name="date" value="${selectedDate}">
        <button id="filterBtn" class="btn btn-primary">Фильтровать</button>
    </div>

    <div class="history-list">
        <c:if test="${not empty deliveryHistory}">
            <c:forEach var="order" items="${deliveryHistory}">
                <div class="order-card">
                    <h3>Заказ #${order.id}</h3>
                    <p><strong>Адрес доставки:</strong> ${order.deliveryAddress.street} ${order.deliveryAddress.building}</p>
                    <p><strong>Сумма:</strong> ${order.totalPrice} руб.</p>
                    <p><strong>Статус:</strong> ${order.status}</p>
                    <p><strong>Время обновления:</strong> ${order.updatedAt}</p>
                </div>
            </c:forEach>
        </c:if>
        <c:if test="${empty deliveryHistory}">
            <p>Нет заказов за выбранную дату.</p>
        </c:if>
    </div>
</div>

<script>
document.getElementById('filterBtn').addEventListener('click', function() {
    const date = document.getElementById('date').value;
    window.location.href = '${pageContext.request.contextPath}/courier/history?date=' + date;
});
</script>
</body>
</html>
