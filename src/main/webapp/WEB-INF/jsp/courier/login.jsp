<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Вход курьера - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="auth-page">
    <div class="auth-container">
        <div class="auth-box">
            <h1>Food Delivery</h1>
            <h2>Вход курьера</h2>
            <p class="role-hint">Телефон и пароль выдает менеджер. После входа вы сможете начать смену.</p>
            <p class="role-hint">Тестовый курьер: телефон +79991000001 / пароль pwd01</p>

            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <form method="POST" action="${pageContext.request.contextPath}/login">
                <input type="hidden" name="role" value="COURIER"/>
                <div class="form-group">
                    <label>Телефон:</label>
                    <input type="tel" name="phone" placeholder="89XXXXXXXXX" required>
                </div>

                <div class="form-group">
                    <label>Пароль:</label>
                    <input type="password" name="password" required>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Войти</button>
            </form>
        </div>
    </div>
</body>
</html>
