<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Список магазинов - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/layout/navbar.jsp" %>

    <main class="container">
        <h1>Магазины и рестораны</h1>
        <p class="muted">Выберите магазин для просмотра меню и оформления заказа</p>

        <div class="filter-container" style="margin: 20px 0; padding: 15px; background: #f8f9fa; border-radius: 8px;">
            <label for="shop-type-filter" style="display: block; margin-bottom: 10px; font-weight: bold;">Фильтр по типу:</label>
            <select id="shop-type-filter" style="padding: 8px 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 16px; width: 100%; max-width: 300px;">
                <option value="">Все типы</option>
                <c:forEach var="type" items="${shopTypes}">
                    <option value="${type}">${type.displayName}</option>
                </c:forEach>
            </select>
        </div>

        <div class="shops-list" id="shops-list" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; margin-top: 20px;">
            <div style="grid-column: 1 / -1; text-align: center; padding: 40px; color: #666;">
                Загрузка магазинов...
            </div>
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
                                shopItem.className = 'shop-card';
                                shopItem.style.cssText = 'border: 1px solid #ddd; border-radius: 8px; padding: 20px; background: #fff; transition: box-shadow 0.2s; cursor: pointer;';
                                shopItem.onmouseover = function() { this.style.boxShadow = '0 4px 12px rgba(0,0,0,0.12)'; };
                                shopItem.onmouseout = function() { this.style.boxShadow = 'none'; };
                                shopItem.onclick = function() { window.location.href = '${pageContext.request.contextPath}/shop/details?id=' + shop.shopId; };
                                
                                function escapeHtml(text) {
                                    if (!text) return '';
                                    const div = document.createElement('div');
                                    div.textContent = text;
                                    return div.innerHTML;
                                }
                                
                                shopItem.innerHTML = `
                                    <h3 style="margin-top: 0; color: #2c3e50;">${escapeHtml(shop.naming || 'Без названия')}</h3>
                                    <p style="color: #666; margin: 10px 0;">${escapeHtml(shop.description || 'Описание отсутствует')}</p>
                                    <div style="margin-top: 15px; padding-top: 15px; border-top: 1px solid #eee;">
                                        <p style="font-size: 14px; color: #888; margin: 5px 0;">
                                            ${shop.publicEmail ? '📧 ' + escapeHtml(shop.publicEmail) : ''}
                                        </p>
                                        <p style="font-size: 14px; color: #888; margin: 5px 0;">
                                            ${shop.publicPhone ? '☎ ' + escapeHtml(shop.publicPhone) : ''}
                                        </p>
                                        ${shop.type ? '<p style="font-size: 12px; color: #999; margin-top: 10px;">Тип: ' + escapeHtml(shop.type) + '</p>' : ''}
                                    </div>
                                    <a href="${pageContext.request.contextPath}/shop/details?id=${shop.shopId}" class="btn btn-primary" style="margin-top: 15px; display: inline-block; text-decoration: none;">
                                        Подробнее →
                                    </a>
                                `;
                                shopsList.appendChild(shopItem);
                            });
                        } else {
                            shopsList.innerHTML = '<div style="grid-column: 1 / -1; text-align: center; padding: 40px; color: #666; border: 1px dashed #ddd; border-radius: 8px;">Нет доступных магазинов.</div>';
                        }
                    })
                    .catch(error => {
                        console.error('Error fetching shops:', error);
                        shopsList.innerHTML = '<div style="grid-column: 1 / -1; text-align: center; padding: 40px; color: #dc3545; border: 1px solid #dc3545; border-radius: 8px;">Ошибка загрузки списка магазинов.</div>';
                    });
            }

            // Initial load
            fetchShops();

            // Filter event
            typeFilter.addEventListener('change', function() {
                fetchShops(this.value);
            });
            
            function escapeHtml(text) {
                if (!text) return '';
                const div = document.createElement('div');
                div.textContent = text;
                return div.innerHTML;
            }
        });
    </script>
    <%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
</body>
</html>
