<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Личный кабинет - Food Delivery</title>
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
        <h1>Главная страница</h1>
        <p>Добро пожаловать, ${sessionScope.userName}!</p>

        <div class="section">
            <h2>Найти магазины</h2>
            <form method="GET" action="${pageContext.request.contextPath}/shop/list">
                <input type="text" name="search" placeholder="Найти магазин...">
                <button type="submit" class="btn btn-primary">Поиск</button>
            </form>
        </div>
    </main>
</body>
</html>

