<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Личный кабинет - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <style>
        .tabs { display: flex; gap: 10px; border-bottom: 2px solid #ddd; margin-bottom: 20px; }
        .tab-button { padding: 10px 20px; background: none; border: none; cursor: pointer; font-size: 16px; border-bottom: 3px solid transparent; }
        .tab-button.active { border-bottom-color: #007bff; color: #007bff; font-weight: bold; }
        .tab-content { display: none; }
        .tab-content.active { display: block; }
        .shops-grid, .products-grid, .categories-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; margin-top: 20px; }
        .shop-card, .product-card, .category-card { border: 1px solid #ddd; border-radius: 8px; padding: 15px; transition: box-shadow 0.2s; background: #fff; }
        .shop-card:hover, .product-card:hover, .category-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.12); }
        .product-price { font-size: 18px; font-weight: bold; color: #28a745; margin: 10px 0; }
        .product-actions { display: flex; gap: 10px; margin-top: auto; }
        .btn-small { padding: 6px 10px; font-size: 13px; }
        .back-link { margin-top: 10px; display: inline-flex; align-items: center; color: #007bff; cursor: pointer; }
        .back-link span { margin-left: 6px; }
        .cart-items { margin-top: 20px; }
        .cart-item { display: flex; justify-content: space-between; align-items: center; padding: 15px; border: 1px solid #ddd; border-radius: 8px; margin-bottom: 10px; }
        .cart-summary { background: #f8f9fa; padding: 20px; border-radius: 8px; margin-top: 20px; }
        .summary-row { display: flex; justify-content: space-between; margin: 10px 0; font-size: 16px; }
        .summary-total { font-size: 20px; font-weight: bold; color: #23a340; border-top: 2px solid #ddd; padding-top: 10px; margin-top: 10px; }
        .empty-state { padding: 40px; text-align: center; color: #777; border: 1px dashed #ccc; border-radius: 8px; }
        .orders-list, .notifications-list { margin-top: 20px; display: flex; flex-direction: column; gap: 12px; }
        .order-card, .notification-card { border: 1px solid #ddd; border-radius: 8px; padding: 15px; background: #fff; }
        .order-items { margin-top: 10px; font-size: 14px; color: #555; }
        .notification-card.unread { border-color: #007bff; box-shadow: 0 2px 8px rgba(0,123,255,0.15); }
    </style>
</head>
<body>
<nav class="navbar">
    <div class="container">
        <h1><a href="${pageContext.request.contextPath}/">Food Delivery</a></h1>
        <div class="nav-user">
            <span>Добро пожаловать, ${sessionScope.userName}!</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary">Выход</a>
        </div>
    </div>
</nav>

<main class="container">
    <h1>Личный кабинет</h1>

    <div class="tabs">
        <button class="tab-button active" data-tab="restaurants" onclick="switchTab(event, 'restaurants')">🏪 Рестораны</button>
        <button class="tab-button" data-tab="cart" onclick="switchTab(event, 'cart')">🛒 Корзина</button>
        <button class="tab-button" data-tab="profile" onclick="switchTab(event, 'profile')">👤 Профиль</button>
        <button class="tab-button" data-tab="orders" onclick="switchTab(event, 'orders')">🧾 История заказов</button>
        <button class="tab-button" data-tab="notifications" onclick="switchTab(event, 'notifications')">🔔 Уведомления</button>
    </div>

    <div id="restaurants" class="tab-content active">
        <div id="shopListSection">
            <h2>Доступные рестораны</h2>
            <div class="shops-grid" id="shopsContainer">
                <div class="empty-state">Загрузка ресторанов...</div>
            </div>
        </div>

        <div id="shopDetailsSection" style="display:none;">
            <div class="back-link" onclick="showShopList()">⬅ <span>К списку ресторанов</span></div>
            <h2 id="selectedShopName"></h2>
            <p id="selectedShopInfo"></p>
            <div>
                <h3>Категории</h3>
                <div class="categories-grid" id="categoriesContainer"></div>
            </div>
            <div>
                <h3>Продукты</h3>
                <div class="products-grid" id="productsContainer">
                    <div class="empty-state">Выберите категорию, чтобы увидеть продукты.</div>
                </div>
            </div>
        </div>
    </div>

    <div id="cart" class="tab-content">
        <h2>Ваша корзина</h2>
        <div class="cart-items" id="cartContainer">
            <div class="empty-state">Корзина пуста</div>
        </div>
        <div class="cart-summary" id="cartSummary" style="display:none;">
            <div class="summary-row"><span>Количество товаров:</span><span id="cartCount">0</span></div>
            <div class="summary-row"><span>Сумма:</span><span id="cartTotal">0 ₽</span></div>
            <div class="summary-total"><div class="summary-row"><span>Итого:</span><span id="cartGrandTotal">0 ₽</span></div></div>
            <label for="paymentMethod">Способ оплаты:</label>
            <select id="paymentMethod" style="width:100%; margin:10px 0; padding:8px;">
                <option value="CASH">Оплата при получении</option>
                <option value="CARD">Карта онлайн</option>
            </select>
            <button class="btn btn-success" style="width:100%; margin-top:10px;" onclick="checkout()">Оформить заказ</button>
        </div>
    </div>

    <div id="profile" class="tab-content">
        <h2>Мой профиль</h2>
        <form method="POST" action="${pageContext.request.contextPath}/client/update-profile" class="profile-form">
            <div class="form-group">
                <label for="name">Имя:</label>
                <input type="text" id="name" name="name" value="${sessionScope.userName}" required>
            </div>

            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" value="${sessionScope.userEmail}" required>
            </div>

            <div class="form-group">
                <label for="phone">Телефон:</label>
                <input type="tel" id="phone" name="phone" placeholder="89XXXXXXXXX" required>
            </div>

            <div class="form-group">
                <label for="country">Страна:</label>
                <input type="text" id="country" name="country" placeholder="Россия">
            </div>

            <div class="form-group">
                <label for="city">Город:</label>
                <input type="text" id="city" name="city" placeholder="Москва">
            </div>

            <div class="form-group">
                <label for="street">Улица:</label>
                <input type="text" id="street" name="street" placeholder="Главная улица">
            </div>

            <div class="form-group">
                <label for="building">Здание:</label>
                <input type="text" id="building" name="building" placeholder="1">
            </div>

            <div class="form-group">
                <label for="apartment">Квартира:</label>
                <input type="text" id="apartment" name="apartment" placeholder="101">
            </div>

            <button type="submit" class="btn btn-primary">Сохранить</button>
        </form>
        <div style="margin-top:30px;">
            <h3>Опасная зона</h3>
            <button onclick="if(confirm('Вы уверены?')) location.href='${pageContext.request.contextPath}/client/deactivate'" class="btn btn-danger">Деактивировать аккаунт</button>
        </div>
    </div>

    <div id="orders" class="tab-content">
        <h2>История заказов</h2>
        <div id="ordersContainer" class="orders-list">
            <div class="empty-state">Загрузка заказов...</div>
        </div>
    </div>

    <div id="notifications" class="tab-content">
        <h2>Уведомления</h2>
        <div style="text-align:right; margin-bottom:10px;">
            <button class="btn btn-secondary" onclick="markNotificationsRead()">Отметить прочитанными</button>
        </div>
        <div id="notificationsContainer" class="notifications-list">
            <div class="empty-state">Загрузка уведомлений...</div>
        </div>
    </div>
</main>

<script>
    let currentShopId = null;

    function switchTab(evt, tabName) {
        document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
        document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
        document.getElementById(tabName).classList.add('active');
        evt.target.classList.add('active');
        if (tabName === 'restaurants') loadShops();
        if (tabName === 'cart') loadCart();
        if (tabName === 'orders') loadOrders();
        if (tabName === 'notifications') loadNotifications();
    }

    function showShopList() {
        document.getElementById('shopDetailsSection').style.display = 'none';
        document.getElementById('shopListSection').style.display = 'block';
        currentShopId = null;
    }

    function escapeHtml(str) {
        if (!str) return '';
        return str
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function renderShopCard(shop) {
        const card = document.createElement('div');
        card.className = 'shop-card';
        card.addEventListener('click', () => openShop(shop.shopId, shop.naming || ''));
        card.innerHTML = '' +
            '<h3>' + escapeHtml(shop.naming || '') + '</h3>' +
            '<p>' + escapeHtml(shop.description || '') + '</p>' +
            '<p style="color:#666;font-size:13px;">📧 ' + escapeHtml(shop.publicEmail || '—') + ' | ☎ ' + escapeHtml(shop.publicPhone || '—') + '</p>' +
            '<p style="font-size:12px;color:#999;">Тип: ' + escapeHtml(shop.type || '—') + '</p>';
        return card;
    }

    function renderProductCard(product) {
        const card = document.createElement('div');
        card.className = 'product-card';
        card.innerHTML = '' +
            '<h4>' + escapeHtml(product.name || '') + '</h4>' +
            '<p>' + escapeHtml(product.description || '') + '</p>' +
            '<p style="color:#666;font-size:12px;">Вес: ' + (product.weight || 0) + ' г</p>' +
            '<div class="product-price">' + (product.price || 0) + ' ₽</div>' +
            '<div class="product-actions">' +
            '  <button class="btn btn-success btn-small">+ Добавить</button>' +
            '</div>';
        card.querySelector('button').addEventListener('click', () => addToCart(product.productId, product.name || ''));
        return card;
    }

    function renderCartItem(item) {
        const wrapper = document.createElement('div');
        wrapper.className = 'cart-item';
        wrapper.innerHTML = '' +
            '<div>' +
            '  <strong>' + escapeHtml(item.name || '') + '</strong>' +
            '  <p>' + item.price + ' ₽ × ' + item.quantity + ' = ' + (item.price * item.quantity) + ' ₽</p>' +
            '</div>' +
            '<div class="cart-item-quantity">' +
            '  <button class="btn btn-small">−</button>' +
            '  <span>' + item.quantity + '</span>' +
            '  <button class="btn btn-small">+</button>' +
            '  <button class="btn btn-danger btn-small">✕</button>' +
            '</div>';
        const buttons = wrapper.querySelectorAll('button');
        buttons[0].addEventListener('click', () => updateQuantity(item.cartItemId, item.quantity - 1));
        buttons[1].addEventListener('click', () => updateQuantity(item.cartItemId, item.quantity + 1));
        buttons[2].addEventListener('click', () => removeFromCart(item.cartItemId));
        return wrapper;
    }

    function loadShops() {
        fetch('${pageContext.request.contextPath}/shop/list-api')
            .then(r => r.json())
            .then(shops => {
                const container = document.getElementById('shopsContainer');
                container.innerHTML = '';
                if (!shops.length) {
                    container.innerHTML = '<div class="empty-state">Нет доступных ресторанов</div>';
                    return;
                }
                shops.forEach(shop => container.appendChild(renderShopCard(shop)));
            })
            .catch(() => {
                document.getElementById('shopsContainer').innerHTML = '<div class="empty-state">Ошибка загрузки ресторанов</div>';
            });
    }

    function openShop(shopId, shopName) {
        currentShopId = shopId;
        document.getElementById('shopListSection').style.display = 'none';
        document.getElementById('shopDetailsSection').style.display = 'block';
        document.getElementById('selectedShopName').innerText = shopName;
        document.getElementById('selectedShopInfo').innerText = 'Выберите категорию, чтобы увидеть продукты.';
        loadCategories(shopId);
        document.getElementById('productsContainer').innerHTML = '<div class="empty-state">Загрузка категорий...</div>';
    }

    function loadCategories(shopId) {
        fetch('${pageContext.request.contextPath}/products/categories?shopId=' + shopId)
            .then(r => r.json())
            .then(categories => {
                const container = document.getElementById('categoriesContainer');
                container.innerHTML = '';
                if (!categories.length) {
                    container.innerHTML = '<div class="empty-state">Категории не найдены</div>';
                    document.getElementById('productsContainer').innerHTML = '<div class="empty-state">Нет продуктов</div>';
                    return;
                }
                categories.forEach(cat => {
                    const card = document.createElement('div');
                    card.className = 'category-card';
                    card.innerHTML = '<strong>' + translateCategory(cat) + '</strong>';
                    card.addEventListener('click', () => loadProducts(shopId, cat));
                    container.appendChild(card);
                });
                loadProducts(shopId, categories[0]);
            })
            .catch(() => {
                document.getElementById('categoriesContainer').innerHTML = '<div class="empty-state">Ошибка загрузки категорий</div>';
            });
    }

    function loadProducts(shopId, category) {
        document.getElementById('productsContainer').innerHTML = '<div class="empty-state">Загрузка продуктов...</div>';
        fetch('${pageContext.request.contextPath}/products/by-shop?shopId=' + shopId + '&category=' + encodeURIComponent(category))
            .then(r => r.json())
            .then(products => {
                const container = document.getElementById('productsContainer');
                container.innerHTML = '';
                if (!products.length) {
                    container.innerHTML = '<div class="empty-state">Нет продуктов в этой категории</div>';
                    return;
                }
                products.forEach(product => container.appendChild(renderProductCard(product)));
            })
            .catch(() => {
                document.getElementById('productsContainer').innerHTML = '<div class="empty-state">Ошибка загрузки продуктов</div>';
            });
    }

    function addToCart(productId, productName) {
        fetch('${pageContext.request.contextPath}/cart/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'productId=' + productId + '&quantity=1'
        })
            .then(r => r.json())
            .then(() => {
                alert('✅ ' + productName + ' добавлен в корзину');
                loadCart();
            })
            .catch(() => alert('Не удалось добавить товар в корзину'));
    }

    function loadCart() {
        fetch('${pageContext.request.contextPath}/cart/items-api')
            .then(r => r.json())
            .then(data => {
                const items = data.items || [];
                const container = document.getElementById('cartContainer');
                container.innerHTML = '';
                if (!items.length) {
                    container.innerHTML = '<div class="empty-state">Корзина пуста</div>';
                    document.getElementById('cartSummary').style.display = 'none';
                    return;
                }
                let total = data.total || 0;
                let count = 0;
                items.forEach(item => {
                    count += item.quantity;
                    container.appendChild(renderCartItem(item));
                });
                document.getElementById('cartSummary').style.display = 'block';
                document.getElementById('cartCount').innerText = count;
                document.getElementById('cartTotal').innerText = total + ' ₽';
                document.getElementById('cartGrandTotal').innerText = total + ' ₽';
            })
            .catch(() => {
                document.getElementById('cartContainer').innerHTML = '<div class="empty-state">Ошибка загрузки корзины</div>';
            });
    }

    function updateQuantity(cartItemId, newQuantity) {
        if (newQuantity < 1) {
            removeFromCart(cartItemId);
            return;
        }
        fetch('${pageContext.request.contextPath}/cart/update', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'cartItemId=' + cartItemId + '&quantity=' + newQuantity
        }).then(loadCart);
    }

    function removeFromCart(cartItemId) {
        fetch('${pageContext.request.contextPath}/cart/remove', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'cartItemId=' + cartItemId
        }).then(loadCart);
    }

    function translateCategory(cat) {
        const map = { BAKERY: 'Хлебобулочные', MAIN_DISH: 'Основные блюда', DESSERT: 'Десерты', DRINK: 'Напитки', OTHER: 'Другое' };
        return map[cat] || cat;
    }

    function checkout() {
        const method = document.getElementById('paymentMethod').value;
        fetch('${pageContext.request.contextPath}/cart/checkout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'paymentMethod=' + encodeURIComponent(method)
        })
            .then(r => r.json())
            .then(data => {
                if (data.error) {
                    alert('Ошибка: ' + data.error);
                    return;
                }
                alert('Заказ #' + data.orderId + ' оформлен. Статус: ' + data.status);
                loadCart();
            })
            .catch(() => alert('Не удалось оформить заказ'));
    }

    function loadOrders() {
        fetch('${pageContext.request.contextPath}/client/orders-api')
            .then(r => r.json())
            .then(orders => {
                const container = document.getElementById('ordersContainer');
                if (!orders.length) {
                    container.innerHTML = '<div class="empty-state">Заказов пока нет</div>';
                    return;
                }
                container.innerHTML = orders.map(order => {
                    const itemsHtml = order.items.map(item => `${item.name} × ${item.quantity} — ${item.price} ₽`).join('<br>');
                    return `
                        <div class="order-card">
                            <div style="display:flex; justify-content:space-between;">
                                <strong>Заказ #${order.id}</strong>
                                <span>${order.createdAt || ''}</span>
                            </div>
                            <p>Статус: <strong>${order.status}</strong></p>
                            <p>Сумма: <strong>${order.total} ₽</strong></p>
                            <div class="order-items">${itemsHtml}</div>
                            <button class="btn btn-primary btn-small" onclick="repeatOrder(${order.id})">Повторить</button>
                        </div>`;
                }).join('');
            })
            .catch(() => document.getElementById('ordersContainer').innerHTML = '<div class="empty-state">Ошибка загрузки заказов</div>');
    }

    function loadNotifications() {
        fetch('${pageContext.request.contextPath}/client/notifications-api')
            .then(r => r.json())
            .then(list => {
                const container = document.getElementById('notificationsContainer');
                if (!list.length) {
                    container.innerHTML = '<div class="empty-state">Уведомлений нет</div>';
                    return;
                }
                container.innerHTML = list.map(n => `
                    <div class="notification-card ${n.isRead ? '' : 'unread'}">
                        <div style="display:flex; justify-content:space-between;">
                            <strong>${n.type}</strong>
                            <span>${n.createdAt}</span>
                        </div>
                        <p>${n.message}</p>
                    </div>`).join('');
            })
            .catch(() => document.getElementById('notificationsContainer').innerHTML = '<div class="empty-state">Ошибка загрузки уведомлений</div>');
    }

    function markNotificationsRead() {
        fetch('${pageContext.request.contextPath}/client/notifications/read', {
            method: 'POST'
        }).then(() => loadNotifications());
    }

    function repeatOrder(orderId) {
        fetch('${pageContext.request.contextPath}/client/orders/repeat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'orderId=' + orderId
        })
            .then(r => r.json())
            .then(data => {
                if (data.error) {
                    alert('Ошибка: ' + data.error);
                    return;
                }
                alert('Товары заказа #' + orderId + ' добавлены в корзину');
                loadCart();
                const cartTabBtn = document.querySelector('.tab-button[data-tab="cart"]');
                if (cartTabBtn) {
                    cartTabBtn.click();
                }
            })
            .catch(() => alert('Не удалось повторить заказ'));
    }

    window.addEventListener('load', () => {
        loadShops();
        loadOrders();
        loadNotifications();
    });
    </script>
</body>
</html>
