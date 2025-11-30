<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
</html>
</body>
    </div>
        </div>
            <p>Уже есть аккаунт? <a href="${pageContext.request.contextPath}/client/login">Войти</a></p>

            </form>
                <button type="submit" class="btn btn-primary btn-block">Зарегистрироваться</button>

                </div>
                    <input type="password" name="password" required>
                    <label>Пароль:</label>
                <div class="form-group">

                </div>
                    <input type="text" name="building">
                    <label>Здание:</label>
                <div class="form-group">

                </div>
                    <input type="text" name="street">
                    <label>Улица:</label>
                <div class="form-group">

                </div>
                    <input type="text" name="city">
                    <label>Город:</label>
                <div class="form-group">

                </div>
                    <input type="tel" name="phone" placeholder="89XXXXXXXXX" required>
                    <label>Телефон:</label>
                <div class="form-group">

                </div>
                    <input type="email" name="email" required>
                    <label>Email:</label>
                <div class="form-group">

                </div>
                    <input type="text" name="name" required>
                    <label>Имя:</label>
                <div class="form-group">
            <form method="POST" action="${pageContext.request.contextPath}/client/register">

            </c:if>
                <div class="alert alert-error">${error}</div>
            <c:if test="${not empty error}">

            <h2>Регистрация</h2>
            <h1>Food Delivery</h1>
        <div class="auth-box">
    <div class="auth-container">
<body class="auth-page">
</head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <title>Регистрация - Food Delivery</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="UTF-8">
<head>
<html>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

