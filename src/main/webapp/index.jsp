<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="container">
            <h1><a href="${pageContext.request.contextPath}/">Food Delivery</a></h1>
            <ul>
                <li><a href="${pageContext.request.contextPath}/client/login">Клиент</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/login">Магазин</a></li>
                <li><a href="${pageContext.request.contextPath}/courier/login">Курьер</a></li>
            </ul>
        </div>
    </nav>

    <main class="container">
        <section class="hero" style="padding:80px 20px; text-align:center;">
            <h2>Добро пожаловать на Food Delivery</h2>
            <p>Быстрая доставка еды и продуктов</p>
            <div class="buttons" style="justify-content:center; margin-top:30px;">
                <a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-primary" style="padding:16px 28px; font-size:18px;">Войти</a>
            </div>
        </section>
    </main>

    <footer>
        <p>&copy; 2025 Food Delivery. Все права защищены.</p>
    </footer>
</body>
</html>
