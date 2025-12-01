<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Список магазинов - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="container">
            <h1>Food Delivery</h1>
            <ul>
                <li><a href="${pageContext.request.contextPath}/">Главная</a></li>
                <li><a href="${pageContext.request.contextPath}/client/login">Клиент</a></li>
            </ul>
        </div>
    </nav>

    <main class="container">
        <h1>Магазины</h1>

        <div class="shops-list">
            <c:forEach var="shop" items="${shops}">
                <div class="shop-item">
                    <h3>${shop.naming}</h3>
                    <p>${shop.description}</p>
                    <a href="${pageContext.request.contextPath}/shop/details?id=${shop.shopId}" class="btn btn-secondary">
                        Подробнее
                    </a>
                </div>
            </c:forEach>
        </div>
    </main>
</body>
</html>
