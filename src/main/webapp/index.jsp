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
        <section class="hero">
            <h2>Добро пожаловать на Food Delivery</h2>
            <p>Быстрая доставка еды и продуктов</p>
            <div class="buttons">
                <a href="${pageContext.request.contextPath}/client/login" class="btn btn-primary">Заказать еду</a>
                <a href="${pageContext.request.contextPath}/shop/list" class="btn btn-secondary">Посмотреть магазины</a>
            </div>
        </section>
    </main>

    <footer>
        <p>&copy; 2025 Food Delivery. Все права защищены.</p>
    </footer>
</body>
</html>

