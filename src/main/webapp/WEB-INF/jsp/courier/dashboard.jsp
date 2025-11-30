<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Личный кабинет курьера - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="container">
            <h1>Food Delivery Курьер</h1>
            <ul>
                <li><a href="${pageContext.request.contextPath}/courier/dashboard">Главная</a></li>
                <li><a href="${pageContext.request.contextPath}/courier/history">История</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout">Выход</a></li>
            </ul>
        </div>
    </nav>

    <main class="container">
        <h1>Личный кабинет курьера</h1>

        <div class="courier-info">
            <h2>${courier.name}</h2>
            <p>Статус: <span class="status-${courier.status}">${courier.status}</span></p>
            <p>Баланс: ${courier.currentBalance} ₽</p>
            <p>Текущий заказ: ${courier.currentOrderId}</p>
            <p>Последний адрес: ${courier.lastAddress}</p>
        </div>

        <div class="courier-actions">
            <c:choose>
                <c:when test="${courier.status == 'OFFLINE'}">
                    <form method="POST" action="${pageContext.request.contextPath}/courier/start-shift">
                        <input type="hidden" name="lastAddress" value="${courier.lastAddress}">
                        <button type="submit" class="btn btn-success btn-large">НАЧАТЬ СМЕНУ</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <form method="POST" action="${pageContext.request.contextPath}/courier/end-shift">
                        <button type="submit" class="btn btn-danger btn-large">ЗАВЕРШИТЬ СМЕНУ</button>
                    </form>

                    <a href="${pageContext.request.contextPath}/courier/orders" class="btn btn-primary btn-large">
                        Доступные заказы
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</body>
</html>

