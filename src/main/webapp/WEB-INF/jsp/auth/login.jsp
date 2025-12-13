<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Вход в систему - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="auth-page">
    <div class="auth-container">
        <div class="auth-box role-selector">
            <h1>Food Delivery</h1>
            <h2>Выберите свою роль</h2>

            <div class="role-grid">
                <div class="role-card">
                    <div class="role-icon">👤</div>
                    <h3>Клиент</h3>
                    <p>Оформляйте заказы, управляйте профилем и корзиной.</p>
                    <a class="btn btn-primary btn-block" href="${pageContext.request.contextPath}/login?role=CLIENT">Войти как клиент</a>
                </div>
                <div class="role-card">
                    <div class="role-icon">🚚</div>
                    <h3>Курьер</h3>
                    <p>Управляйте сменой, принимайте заказы, связывайтесь с клиентами.</p>
                    <a class="btn btn-primary btn-block" href="${pageContext.request.contextPath}/login?role=COURIER">Войти как курьер</a>
                </div>
                <div class="role-card">
                    <div class="role-icon">🏬</div>
                    <h3>Магазин</h3>
                    <p>Следите за заказами, обновляйте ассортимент и статус магазина.</p>
                    <a class="btn btn-primary btn-block" href="${pageContext.request.contextPath}/login?role=SHOP">Войти как магазин</a>
                </div>
            </div>
            <p class="auth-link">
                Нет аккаунта? <a href="${pageContext.request.contextPath}/register?role=CLIENT">Зарегистрироваться как клиент</a>
            </p>
        </div>
    </div>
</body>
</html>
