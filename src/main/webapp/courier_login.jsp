<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Вход для курьера - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="auth-page">
<nav class="navbar">
        <div class="container">
            <a href="${pageContext.request.contextPath}/" class="btn-back" aria-label="Назад на главную">← Назад</a>
        </div>
        <!-- Локальный логотип удалён: оставлена только кнопка назад в navbar -->
    </nav>
<div class="auth-container">
    <div class="auth-box">
        <h2>Вход для курьера</h2>

        <c:if test="${not empty error}">
            <div class="alert alert-error">
                ${error}
            </div>
        </c:if>

        <form method="POST" action="${pageContext.request.contextPath}/auth/login">
            <input type="hidden" name="role" value="COURIER">
            <div class="form-group">
                <label for="phoneNumber">Номер телефона:</label>
                <input type="text" id="phoneNumber" name="phoneNumber" required>
            </div>

            <div class="form-group">
                <label for="password">Пароль:</label>
                <input type="password" id="password" name="password" required>
            </div>

            <button type="submit" class="btn btn-primary btn-block">
                Войти
            </button>
        </form>

        <p class="auth-link">
            <a href="${pageContext.request.contextPath}/login">Назад</a>
        </p>
    </div>
</div>
</body>
</html>
