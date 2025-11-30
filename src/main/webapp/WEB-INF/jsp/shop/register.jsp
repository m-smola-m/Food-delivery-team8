<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Регистрация магазина - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="auth-page">
    <div class="auth-container">
        <div class="auth-box">
            <h1>Food Delivery</h1>
            <h2>Регистрация магазина</h2>

            <c:if test="${not empty success}">
                <div class="alert alert-success">${success}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <form method="POST" action="${pageContext.request.contextPath}/shop/register">
                <div class="form-group">
                    <label>Название магазина:</label>
                    <input type="text" name="naming" required>
                </div>

                <div class="form-group">
                    <label>Email для аутентификации:</label>
                    <input type="email" name="emailForAuth" required>
                </div>

                <div class="form-group">
                    <label>Телефон для аутентификации:</label>
                    <input type="tel" name="phoneForAuth" placeholder="89XXXXXXXXX" required>
                </div>

                <div class="form-group">
                    <label>Пароль:</label>
                    <input type="password" name="password" required>
                </div>

                <div class="form-group">
                    <label>Описание:</label>
                    <textarea name="description"></textarea>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Зарегистрировать</button>
            </form>

            <p>Уже есть аккаунт? <a href="${pageContext.request.contextPath}/shop/login">Войти</a></p>
        </div>
    </div>
</body>
</html>

