<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<nav class="navbar">
    <div class="container">
        <div class="navbar-brand">
            <a href="${pageContext.request.contextPath}/client/home">Food Delivery</a>
        </div>

        <ul class="navbar-menu">
            <li>
                <a href="${pageContext.request.contextPath}/client/home">Магазины</a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/client/orders">Заказы</a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/cart/view">Корзина</a>
            </li>
            <li class="notification-bell-container">
                            <a href="#" id="notification-bell">
                                <i class="fa fa-bell"></i>
                                <span id="notification-count"></span>
                            </a>
                            <div id="notification-dropdown" class="notification-dropdown">
                                <ul id="notification-list"></ul>
                                <button id="mark-read-btn">Отметить все как прочитанные</button>
                            </div>
                        </li>
            <li>
                <a href="${pageContext.request.contextPath}/client/profile">Профиль</a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/auth/logout">Выход</a>
            </li>
        </ul>
    </div>
</nav>
<script src="${pageContext.request.contextPath}/resources/js/notifications.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

