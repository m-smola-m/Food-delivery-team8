<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Заказы магазина - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/jsp/layout/navbar.jsp" %>

<main class="container">
    <div class="section-header">
        <div>
            <h1>Заказы ресторана</h1>
            <p class="muted">Меняйте статусы PREPARING, READY, PICKED_UP, REJECTED и связывайте курьеров.</p>
        </div>
        <form method="GET" class="inline-form" action="${pageContext.request.contextPath}/shop/orders">
            <label class="sr-only" for="date">Дата</label>
            <input type="date" id="date" name="date" value="${param.date}">
            <button type="submit" class="btn btn-secondary btn-small">Обновить</button>
        </form>
    </div>

    <div class="orders-list">
        <c:choose>
            <c:when test="${orders != null and not empty orders}">
                <c:forEach var="order" items="${orders}">
                    <div class="order-card">
                        <h3>Заказ #${order.orderId != null ? order.orderId : 'N/A'}</h3>
                        <p><strong>Клиент:</strong> ${order.clientName != null ? order.clientName : 'Не указан'} (${order.clientPhone != null ? order.clientPhone : 'Не указан'})</p>
                        <p><strong>Адрес:</strong> ${order.toStreet != null ? order.toStreet : ''}, ${order.toHouse != null ? order.toHouse : ''}</p>
                        <p><strong>Состав:</strong> ${order.summary != null ? order.summary : 'Не указан'}</p>
                        <p><strong>Сумма:</strong> ${order.totalPrice != null ? order.totalPrice : '0'} ₽</p>
                        <p><strong>Текущий статус:</strong> ${order.status != null ? order.status : 'Неизвестно'}</p>

                        <form method="POST" action="${pageContext.request.contextPath}/orders/update-status" class="inline-form">
                            <input type="hidden" name="orderId" value="${order.orderId}">
                            <select name="status" required>
                                <option value="PREPARING">PREPARING</option>
                                <option value="READY">READY</option>
                                <option value="PICKED_UP">PICKED_UP</option>
                                <option value="REJECTED">REJECTED</option>
                            </select>
                            <button type="submit" class="btn btn-primary btn-small">Сохранить</button>
                        </form>

                        <form method="POST" action="${pageContext.request.contextPath}/orders/cancel" class="inline-form">
                            <input type="hidden" name="orderId" value="${order.orderId}">
                            <button type="submit" class="btn btn-danger btn-small">Отменить (нет ингредиентов)</button>
                        </form>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div style="text-align: center; padding: 40px; color: #666; border: 1px dashed #ddd; border-radius: 8px;">
                    <p>Заказов пока нет.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>
<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
</body>
</html>
