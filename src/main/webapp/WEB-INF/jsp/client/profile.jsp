<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Профиль - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="container">
            <h1>Food Delivery</h1>
            <ul>
                <li><a href="${pageContext.request.contextPath}/client/home">Главная</a></li>
                <li><a href="${pageContext.request.contextPath}/client/profile">Профиль</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout">Выход</a></li>
            </ul>
        </div>
    </nav>

    <main class="container">
        <h1>Мой профиль</h1>

        <c:if test="${not empty param.updated}">
            <div class="alert alert-success">Профиль обновлён!</div>
        </c:if>

        <div class="profile-section">
            <form method="POST" action="${pageContext.request.contextPath}/client/update-profile">
                <div class="form-group">
                    <label>Имя:</label>
                    <input type="text" name="name" value="${client.name}">
                </div>

                <div class="form-group">
                    <label>Email:</label>
                    <input type="email" name="email" value="${client.email}">
                </div>

                <div class="form-group">
                    <label>Телефон:</label>
                    <input type="tel" name="phone" value="${client.phone}">
                </div>

                <button type="submit" class="btn btn-primary">Сохранить</button>
            </form>

            <form method="POST" action="${pageContext.request.contextPath}/client/deactivate" style="margin-top: 20px;">
                <button type="submit" class="btn btn-danger" onclick="return confirm('Вы уверены?')">
                    Деактивировать аккаунт
                </button>
            </form>
        </div>
    </main>
</body>
</html>

