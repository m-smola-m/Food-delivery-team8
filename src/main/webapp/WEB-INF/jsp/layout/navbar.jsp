<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="navbar">
    <div class="container">
        <div class="navbar-brand">
            <c:choose>
                <c:when test="${sessionScope.userRole == 'SHOP'}">
                    <a href="${pageContext.request.contextPath}/shop/dashboard">Food Delivery</a>
                </c:when>
                <c:when test="${sessionScope.userRole == 'COURIER'}">
                    <a href="${pageContext.request.contextPath}/courier/dashboard">Food Delivery</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/client/home">Food Delivery</a>
                </c:otherwise>
            </c:choose>
        </div>

        <ul class="navbar-menu">
            <c:choose>
                <c:when test="${sessionScope.userRole == 'SHOP'}">
                    <li>
                        <a href="${pageContext.request.contextPath}/shop/dashboard">Панель</a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/products/list">Товары</a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/shop/orders">Заказы</a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/auth/logout">Выход</a>
                    </li>
                </c:when>
                <c:when test="${sessionScope.userRole == 'COURIER'}">
                    <li>
                        <a href="${pageContext.request.contextPath}/courier/dashboard">Панель</a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/courier/orders">Заказы</a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/auth/logout">Выход</a>
                    </li>
                </c:when>
                <c:otherwise>
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
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
</nav>
<c:if test="${sessionScope.userRole == 'CLIENT'}">
    <script src="${pageContext.request.contextPath}/resources/js/notifications.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</c:if>

<div class="toast-container" id="globalToastContainer" aria-live="polite" aria-atomic="true"></div>
<script>
    // Глобальная функция для показа toast уведомления
    function showToast(message, timeout = 3000) {
        try {
            const container = document.getElementById('globalToastContainer');
            if (!container) return;
            const t = document.createElement('div');
            t.className = 'toast';
            t.textContent = message;
            container.appendChild(t);
            // force reflow to allow transition
            void t.offsetWidth;
            t.classList.add('show');
            setTimeout(() => {
                t.classList.remove('show');
                setTimeout(() => t.remove(), 250);
            }, timeout);
        } catch (e) {
            console.error('Toast error', e);
        }
    }
</script>
<script src="${pageContext.request.contextPath}/resources/js/notifications.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
