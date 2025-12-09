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
                        <h3>Заказ #${order.id != null ? order.id : 'N/A'}</h3>
                        <p><strong>ID клиента:</strong> ${order.customerId != null ? order.customerId : 'Не указан'}</p>
                        <c:if test="${order.deliveryAddress != null}">
                            <p><strong>Адрес доставки:</strong> 
                                ${order.deliveryAddress.city != null ? order.deliveryAddress.city : ''}
                                ${order.deliveryAddress.street != null ? ', ' : ''}${order.deliveryAddress.street != null ? order.deliveryAddress.street : ''}
                                ${order.deliveryAddress.building != null ? ', ' : ''}${order.deliveryAddress.building != null ? order.deliveryAddress.building : ''}
                            </p>
                        </c:if>
                        <c:if test="${order.items != null and not empty order.items}">
                            <p><strong>Состав заказа:</strong></p>
                            <ul>
                                <c:forEach var="item" items="${order.items}">
                                    <li>${item.productName != null ? item.productName : 'Товар'} × ${item.quantity != null ? item.quantity : 0} — ${item.price != null ? item.price : 0} ₽</li>
                                </c:forEach>
                            </ul>
                        </c:if>
                        <p><strong>Сумма:</strong> ${order.totalPrice != null ? order.totalPrice : '0'} ₽</p>
                        <p><strong>Статус:</strong> ${order.status != null ? order.status.name() : 'Неизвестно'}</p>
                        <p><strong>Способ оплаты:</strong> ${order.paymentMethod != null ? order.paymentMethod.name() : 'Не указан'}</p>
                        <p><strong>Статус оплаты:</strong> ${order.paymentStatus != null ? order.paymentStatus.name() : 'Неизвестно'}</p>
                        <c:if test="${order.createdAt != null}">
                            <p><strong>Дата создания:</strong> ${order.createdAt}</p>
                        </c:if>

                        <form method="POST" action="${pageContext.request.contextPath}/orders/update-status" class="inline-form">
                            <input type="hidden" name="orderId" value="${order.id}">
                            <select name="status" required>
                                <option value="PREPARING" ${order.status != null && order.status.name() == 'PREPARING' ? 'selected' : ''}>PREPARING</option>
                                <option value="READY_FOR_PICKUP" ${order.status != null && order.status.name() == 'READY_FOR_PICKUP' ? 'selected' : ''}>READY_FOR_PICKUP</option>
                                <option value="PICKED_UP" ${order.status != null && order.status.name() == 'PICKED_UP' ? 'selected' : ''}>PICKED_UP</option>
                                <option value="DELIVERING" ${order.status != null && order.status.name() == 'DELIVERING' ? 'selected' : ''}>DELIVERING</option>
                                <option value="DELIVERED" ${order.status != null && order.status.name() == 'DELIVERED' ? 'selected' : ''}>DELIVERED</option>
                                <option value="COMPLETED" ${order.status != null && order.status.name() == 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                                <option value="CANCELLED" ${order.status != null && order.status.name() == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                            </select>
                            <button type="submit" class="btn btn-primary btn-small">Сохранить</button>
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
