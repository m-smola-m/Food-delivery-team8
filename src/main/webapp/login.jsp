<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Вход - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="auth-page">
<nav class="navbar">
        <div class="container">
            <a href="${pageContext.request.contextPath}/" class="btn-back" aria-label="Назад на главную">← Назад</a>
        </div>
        <div class="container">
            <h1><a href="${pageContext.request.contextPath}/">Food Delivery</a></h1>
        </div>
    </nav>
<div class="auth-container">
    <div class="auth-box">
        <h2>Выберите роль для входа</h2>
        <div class="role-selection">
            <a href="${pageContext.request.contextPath}/login?role=CLIENT" class="btn btn-role">Клиент</a>
            <a href="${pageContext.request.contextPath}/login?role=SHOP" class="btn btn-role">Магазин</a>
            <a href="${pageContext.request.contextPath}/courier_login.jsp" class="btn btn-role">Курьер</a>
        </div>
    </div>
</div>
</body>
</html>