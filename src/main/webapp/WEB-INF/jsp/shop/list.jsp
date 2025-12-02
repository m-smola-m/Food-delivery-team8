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

        <div class="filter-container">
            <label for="shop-type-filter">Фильтр по типу:</label>
            <select id="shop-type-filter">
                <option value="">Все</option>
                <c:forEach var="type" items="${shopTypes}">
                    <option value="${type}">${type.displayName}</option>
                </c:forEach>
            </select>
        </div>

        <div class="shops-list" id="shops-list">
            <!-- Shops will be loaded here dynamically -->
        </div>
    </main>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const shopsList = document.getElementById('shops-list');
            const typeFilter = document.getElementById('shop-type-filter');

            function fetchShops(type = '') {
                let url = '${pageContext.request.contextPath}/shop/list-api';
                if (type) {
                    url += '?type=' + type;
                }

                fetch(url)
                    .then(response => response.json())
                    .then(data => {
                        shopsList.innerHTML = ''; // Clear existing list
                        if (data.length > 0) {
                            data.forEach(shop => {
                                const shopItem = document.createElement('div');
                                shopItem.className = 'shop-item';
                                shopItem.innerHTML = `
                                    <h3>${shop.naming}</h3>
                                    <p>${shop.description}</p>
                                    <a href="${pageContext.request.contextPath}/shop/details?id=${shop.shopId}" class="btn btn-secondary">
                                        Подробнее
                                    </a>
                                `;
                                shopsList.appendChild(shopItem);
                            });
                        } else {
                            shopsList.innerHTML = '<p>Нет доступных ресторанов.</p>';
                        }
                    })
                    .catch(error => {
                        console.error('Error fetching shops:', error);
                        shopsList.innerHTML = '<p>Ошибка загрузки списка ресторанов.</p>';
                    });
            }

            // Initial load
            fetchShops();

            // Filter event
            typeFilter.addEventListener('change', function() {
                fetchShops(this.value);
            });
        });
    </script>
</body>
</html>
