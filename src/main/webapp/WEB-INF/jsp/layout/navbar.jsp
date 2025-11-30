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
            <li>
                <a href="${pageContext.request.contextPath}/client/profile">Профиль</a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/auth/logout">Выход</a>
            </li>
        </ul>
    </div>
</nav>

