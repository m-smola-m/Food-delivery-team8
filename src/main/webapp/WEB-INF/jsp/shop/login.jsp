<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
</html>
</body>
    </div>
        </div>
            <p>Новый магазин? <a href="${pageContext.request.contextPath}/shop/register">Зарегистрировать</a></p>

            </form>
                <button type="submit" class="btn btn-primary btn-block">Войти</button>

                </div>
                    <input type="password" name="password" required>
                    <label>Пароль:</label>
                <div class="form-group">

                </div>
                    <input type="email" name="email" required>
                    <label>Email:</label>
                <div class="form-group">
            <form method="POST" action="${pageContext.request.contextPath}/shop/login">

            </c:if>
                <div class="alert alert-error">${error}</div>
            <c:if test="${not empty error}">

            <h2>Вход магазина</h2>
            <h1>Food Delivery</h1>
        <div class="auth-box">
    <div class="auth-container">
<body class="auth-page">
</head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <title>Вход магазин - Food Delivery</title>
    <meta charset="UTF-8">
<head>
<html>
<!DOCTYPE html>

