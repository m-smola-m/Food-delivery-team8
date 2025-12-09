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
        .cart-shop-group { border: 1px solid #e6e9ee; border-radius: 8px; padding: 12px; margin-bottom: 12px; background: #fff; }
        .cart-shop-header { display:flex; justify-content:space-between; align-items:center; padding-bottom:8px; border-bottom:1px dashed #eee; margin-bottom:8px; }
        .cart-shop-title { font-weight:700; color:#2b6cb0; }
        .cart-shop-subtotal { font-weight:600; color:#23a340; }
        /* Toast notifications (bottom-right) */
        #toast-container { position: fixed; right: 20px; bottom: 20px; display:flex; flex-direction: column-reverse; gap:10px; z-index: 3000; pointer-events:none; }
        .toast { pointer-events:auto; min-width:220px; max-width:360px; background:#fff; color:#111; padding:10px 14px; border-radius:8px; box-shadow:0 6px 20px rgba(0,0,0,0.12); display:flex; gap:8px; align-items:center; border-left:4px solid transparent; transform:translateY(10px); opacity:0; animation: toast-in 220ms forwards; }
        .toast-success { border-left-color:#23a340; }
        .toast-error { border-left-color:#ff4d4d; }
        .toast .toast-message { flex:1; font-size:14px; }
        .toast .toast-close { background:transparent; border:none; color:#666; cursor:pointer; font-size:14px; padding:6px; }
        @keyframes toast-in { from { transform:translateY(8px); opacity:0; } to { transform:translateY(0); opacity:1; } }
        @keyframes toast-out { from { transform:translateY(0); opacity:1; } to { transform:translateY(6px); opacity:0; } }
        .empty-state { padding: 40px; text-align: center; color: #777; border: 1px dashed #ccc; border-radius: 8px; }
        .orders-list, .notifications-list { margin-top: 20px; display: flex; flex-direction: column; gap: 12px; }
        .order-card, .notification-card { border: 1px solid #ddd; border-radius: 8px; padding: 15px; background: #fff; transition: all 0.3s ease; position: relative; }
        .notification-card.unread { border-left: 4px solid #007bff; background: #f8f9ff; box-shadow: 0 2px 8px rgba(0,123,255,0.1); }
        .notification-card.unread strong { color: #007bff; }
        .notification-card.unread::before { content: ''; position: absolute; top: 15px; right: 15px; width: 10px; height: 10px; background: #dc3545; border-radius: 50%; border: 2px solid white; box-shadow: 0 0 0 1px #dc3545; }
        @keyframes markAsRead {
            0% { transform: scale(1); opacity: 1; }
            50% { transform: scale(1.2); opacity: 0.8; }
            100% { transform: scale(0); opacity: 0; }
        }
        .modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.6); display: flex; justify-content: center; align-items: center; z-index: 1000; }
        .modal-content { max-width:720px; width:95%; padding:20px 22px; position:relative; background: white; border-radius: 8px; overflow-y: auto; }
        .modal-close { position:absolute; top:10px; right:12px; background:transparent; border:none; font-size:18px; cursor:pointer; color:#666; }
        .muted { color:#666; font-size:14px; margin-top:6px; }
        .modal-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
        .inactive-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(255, 255, 255, 0.8); z-index: 999; display: flex; justify-content: center; align-items: center; text-align: center; }
        .profile-alert { padding: 10px 15px; border-radius: 8px; margin-bottom: 20px; display: flex; align-items: center; }
        .field-error { color: #a70000; font-size: 13px; margin-top:6px; }
        /* Profile form improvements */
        .profile-form { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; background: #fff; padding: 16px; border-radius: 8px; box-shadow: 0 6px 18px rgba(0,0,0,0.06); }
        .profile-form .form-group { display:flex; flex-direction:column; }
        .profile-form label { font-weight:600; margin-bottom:6px; color:#333; }
        .profile-form input, .profile-form textarea { padding:10px 12px; border:1px solid #e0e6ed; border-radius:6px; font-size:14px; }
        .profile-form .full { grid-column: 1 / -1; }
        .profile-actions { grid-column: 1 / -1; display:flex; gap:10px; justify-content:flex-end; margin-top:8px; }
        .success-banner { display:none; background:#f0fff4; border:1px solid #2ecc71; color:#1f7a3a; padding:10px 12px; border-radius:6px; margin-bottom:12px; }
        .error-banner { display:none; background:#fff0f0; border:1px solid #ff4d4d; color:#a70000; padding:10px 12px; border-radius:6px; margin-bottom:12px; }
        @media(max-width:800px){ .profile-form { grid-template-columns: 1fr; } }
    </style>
</head>
<body>
<c:if test="${sessionScope.clientStatus == 'INACTIVE'}">
    <div class="inactive-overlay">
        <div>
            <h2>Аккаунт деактивирован</h2>
            <p>Вы не можете совершать покупки. Обратитесь в поддержку для восстановления.</p>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-primary">Выйти</a>
        </div>
    </div>
</c:if>

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
            <button class="btn btn-danger" style="width:100%; margin-top:10px;" onclick="clearCart()">Очистить корзину</button>
            <button class="btn btn-success" style="width:100%; margin-top:10px;" onclick="showCheckoutModal()">Оформить заказ</button>
        </div>
    </div>

    <div id="profile" class="tab-content">
        <h2>Мой профиль</h2>

        <div id="profileErrorBanner" class="error-banner"></div>
        <div id="profileSuccessBanner" class="success-banner"></div>

        <!-- header with immutable name and phone -->
        <div class="profile-header" style="background:#fff; padding:16px; border-radius:8px; box-shadow:0 2px 8px rgba(0,0,0,0.05); margin-bottom:12px; display:flex; justify-content:space-between; align-items:center;">
            <div>
                <h3 id="profileName" style="margin:0; font-size:20px; color:#222;">${sessionScope.userName}</h3>
                <p id="profilePhone" style="margin:6px 0 0 0; color:#666;">${sessionScope.userPhone}</p>
            </div>
            <div style="text-align:right; color:#666; font-size:13px;">ID: ${sessionScope.userId}</div>
        </div>

        <form id="profileForm" class="profile-form">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" value="${formData.email != null ? formData.email[0] : ''}" required>
                <div class="field-error" id="err-email">${fieldErrors['email']}</div>
            </div>
            <div class="form-group">
                <label for="country">Страна</label>
                <input type="text" id="country" name="country" placeholder="Россия" value="${formData.country != null ? formData.country[0] : ''}">
                <div class="field-error" id="err-country">${fieldErrors['country']}</div>
            </div>
            <div class="form-group">
                <label for="city">Город</label>
                <input type="text" id="city" name="city" placeholder="Москва" value="${formData.city != null ? formData.city[0] : ''}">
                <div class="field-error" id="err-city">${fieldErrors['city']}</div>
            </div>
            <div class="form-group">
                <label for="street">Улица</label>
                <input type="text" id="street" name="street" placeholder="Главная улица" value="${formData.street != null ? formData.street[0] : ''}">
                <div class="field-error" id="err-street">${fieldErrors['street']}</div>
            </div>
            <div class="form-group">
                <label for="building">Здание</label>
                <input type="text" id="building" name="building" placeholder="1" value="${formData.building != null ? formData.building[0] : ''}">
                <div class="field-error" id="err-building">${fieldErrors['building']}</div>
            </div>
            <div class="form-group">
                <label for="apartment">Квартира</label>
                <input type="text" id="apartment" name="apartment" placeholder="101" value="${formData.apartment != null ? formData.apartment[0] : ''}">
                <div class="field-error" id="err-apartment">${fieldErrors['apartment']}</div>
            </div>
            <div class="form-group">
                <label for="floor">Этаж</label>
                <input type="text" id="floor" name="floor" placeholder="3" value="${formData.floor != null ? formData.floor[0] : ''}">
                <div class="field-error" id="err-floor">${fieldErrors['floor']}</div>
            </div>
            <div class="form-group full">
                <label for="addressNote">Комментарий к адресу</label>
                <textarea id="addressNote" name="addressNote" rows="2">${formData.addressNote != null ? formData.addressNote[0] : ''}</textarea>
                <div class="field-error" id="err-addressNote">${fieldErrors['addressNote']}</div>
            </div>
            <div class="profile-actions">
                <button type="button" class="btn btn-secondary" onclick="loadProfileData();">Отменить</button>
                <button type="submit" class="btn btn-primary">Сохранить</button>
            </div>
        </form>
        <div style="margin-top:30px;">
            <h3>Опасная зона</h3>
            <form id="deactivateForm" method="POST" action="${pageContext.request.contextPath}/client/deactivate" style="display: inline;">
                <button type="submit" class="btn btn-danger" onclick="return confirm('Вы уверены, что хотите деактивировать аккаунт?');">Деактивировать аккаунт</button>
            </form>
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

<!-- Улучшенный checkout modal -->
<div id="checkoutModal" class="modal-overlay" style="display:none;">
    <div class="modal-content" role="dialog" aria-modal="true" aria-labelledby="checkoutTitle">
        <button class="modal-close" onclick="hideCheckoutModal()" aria-label="Закрыть">✕</button>
        <h2 id="checkoutTitle">Оформление заказа</h2>
        <p class="muted">Проверьте корзину и заполните адрес доставки. Вы сможете выбрать способ оплаты ниже.</p>
        <div id="modalError" class="error-banner" style="display:none;margin-bottom:8px;"></div>
        <div id="modalOrderSummary" style="margin:10px 0 16px;"></div>
        <form id="checkoutForm">
            <div class="checkout-grid" style="display:grid; grid-template-columns: 1fr 1fr; gap:10px;">
                <div class="form-group">
                    <label>Страна</label>
                    <input type="text" name="country" required>
                </div>
                <div class="form-group">
                    <label>Город</label>
                    <input type="text" name="city" required>
                </div>
                <div class="form-group">
                    <label>Улица</label>
                    <input type="text" name="street" required>
                </div>
                <div class="form-group">
                    <label>Здание</label>
                    <input type="text" name="building" required>
                </div>
                <div class="form-group">
                    <label>Квартира</label>
                    <input type="text" name="apartment">
                </div>
                <div class="form-group">
                    <label>Подъезд</label>
                    <input type="text" name="entrance">
                </div>
                <div class="form-group">
                    <label>Этаж</label>
                    <input type="text" name="floor">
                </div>
                <div class="form-group">
                    <label>Комментарий</label>
                    <input type="text" name="addressNote">
                </div>
            </div>

            <div style="margin-top:12px;font-weight:600;">Оплата</div>
            <select name="paymentMethod" style="width:100%; margin:10px 0; padding:8px;">
                <option value="CASH">Оплата при получении</option>
                <option value="CARD">Карта онлайн</option>
            </select>
        </form>
        <div class="modal-actions" style="margin-top:12px; display:flex; gap:8px; justify-content:flex-end;">
            <button class="btn btn-secondary" onclick="hideCheckoutModal()">Отмена</button>
            <button class="btn btn-success" onclick="confirmCheckout()">Подтвердить и заказать</button>
        </div>
    </div>
</div>

<style>
    .modal-content { max-width:720px; width:95%; padding:20px 22px; position:relative; }
    .modal-close { position:absolute; top:10px; right:12px; background:transparent; border:none; font-size:18px; cursor:pointer; color:#666; }
    .muted { color:#666; font-size:14px; margin-top:6px; }
    @media(max-width:720px){ .checkout-grid { grid-template-columns: 1fr; } }
</style>

<script>
    let currentShopId = null;
    const isInactive = "${sessionScope.clientStatus}" === "INACTIVE";
    const isLoggedIn = ${sessionScope.userId != null ? 'true' : 'false'};

    function switchTab(evt, tabName) {
        document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
        document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
        document.getElementById(tabName).classList.add('active');
        evt.target.classList.add('active');
        if (tabName === 'restaurants') loadShops();
        if (tabName === 'cart') loadCart();
        if (tabName === 'orders') loadOrders();
        if (tabName === 'notifications') loadNotifications();
        if (tabName === 'profile') loadProfileData();
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

    // Emoji mapper for product names — placed before any usage
    function getProductEmoji(name) {
        if (!name) return '🍽️';
        const lower = name.toLowerCase();
        if (/(пицц|pizza)/.test(lower)) return '🍕';
        if (/(бургер|гамбургер|burger)/.test(lower)) return '🍔';
        if (/(сэндвич|sandwich|бутерброд)/.test(lower)) return '🥪';
        if (/(паста|спагетти|macaroni|penne|pasta)/.test(lower)) return '🍝';
        if (/(суши|ролл|роллы|sushi|roll)/.test(lower)) return '🍣';
        if (/(рамен|лапш|ramen|noodle|udon|лапша)/.test(lower)) return '🍜';
        if (/(салат|salad)/.test(lower)) return '🥗';
        if (/(суп|soup|борщ|borsch)/.test(lower)) return '🍲';
        if (/(рыб|лосос|salmon|fish|треск|треска)/.test(lower)) return '🐟';
        if (/(куриц|chicken|цыпленок|куриный)/.test(lower)) return '🍗';
        if (/(стейк|говядин|beef|steak|мясо)/.test(lower)) return '🥩';
        if (/(свин|pork)/.test(lower)) return '🍖';
        if (/(кревет|shrimp|prawn|морепродукт)/.test(lower)) return '🍤';
        if (/(омар|краб|lobster|crab)/.test(lower)) return '🦞';
        if (/(тако|taco)/.test(lower)) return '🌮';
        if (/(шаурм|shawarma|буррито|burrito|wrap|лаваш)/.test(lower)) return '🌯';
        if (/(фри|картофел|fries|potato)/.test(lower)) return '🍟';
        if (/(блин|панкейк|pancake)/.test(lower)) return '🥞';
        if (/(хлеб|булоч|bagel|булочка|bake)/.test(lower)) return '🍞';
        if (/(сыр|cheese)/.test(lower)) return '🧀';
        if (/(яйц|egg)/.test(lower)) return '🥚';
        if (/(торт|пирог|cake|cookie|печеньк|пирожное|dessert)/.test(lower)) return '🍰';
        if (/(морожен|ice ?cream|gelato)/.test(lower)) return '🍦';
        if (/(кофе|coffee)/.test(lower)) return '☕';
        if (/(чай|tea)/.test(lower)) return '🍵';
        if (/(сок|juice|smoothie|напиток|молочный коктейль|milkshake)/.test(lower)) return '🥤';
        if (/(коктейль|cocktail|mojito|martini)/.test(lower)) return '🍹';
        if (/(пиво|beer)/.test(lower)) return '🍺';
        if (/(вино|wine)/.test(lower)) return '🍷';
        if (/(фрукт|яблок|яблоко|banana|банан|orange|апельсин|груша|pear|манго|mango)/.test(lower)) return '🍎';
        if (/(овощ|томат|огурец|carrot|vegetable|veggie|свекл|баклажан)/.test(lower)) return '🥕';
        if (/(орех|nuts|nut)/.test(lower)) return '🥜';
        if (/(печень|cookie|cupcake|muffin|сладко|шоколад)/.test(lower)) return '🍪';
        if (/(хотдог|hotdog)/.test(lower)) return '🌭';
        // default food plate
        return '🍽️';
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
        card.style.cursor = 'pointer';
        card.addEventListener('click', (e) => {
            if (!e.target.closest('button')) {
                window.location.href = '${pageContext.request.contextPath}/product/details/' + product.productId;
            }
        });
        const emoji = getProductEmoji(product.name || '');
        card.innerHTML = '' +
            '<div style="font-size: 48px; text-align: center; margin-bottom: 10px;">' + emoji + '</div>' +
            '<h4>' + escapeHtml(product.name || '') + '</h4>' +
            '<p>' + escapeHtml(product.description || '') + '</p>' +
            '<p style="color:#666;font-size:12px;">Вес: ' + (product.weight || 0) + ' г</p>' +
            '<div class="product-price">' + (product.price || 0) + ' ₽</div>' +
            '<div class="product-actions">' +
            '  <button class="btn btn-success btn-small" ' + (isInactive ? 'disabled' : '') + '>+ Добавить</button>' +
            '</div>';
        card.querySelector('button').addEventListener('click', () => addToCart(product.productId, product.name || ''));
        return card;
    }

    function renderCartItem(item) {
        const wrapper = document.createElement('div');
        wrapper.className = 'cart-item';
        const emoji = getProductEmoji(item.name || '');
        wrapper.innerHTML = '' +
            '<div style="display: flex; align-items: center; gap: 10px;">' +
            '  <span style="font-size: 24px;">' + emoji + '</span>' +
            '  <div>' +
            '    <strong>' + escapeHtml(item.name || '') + '</strong>' +
            '    <p>' + item.price + ' ₽ × ' + item.quantity + ' = ' + (item.price * item.quantity) + ' ₽</p>' +
            '  </div>' +
            '</div>' +
            '<div class="cart-item-quantity">' +
            '  <button class="btn btn-small" ' + (isInactive ? 'disabled' : '') + '>−</button>' +
            '  <span>' + item.quantity + '</span>' +
            '  <button class="btn btn-small" ' + (isInactive ? 'disabled' : '') + '>+</button>' +
            '  <button class="btn btn-danger btn-small" ' + (isInactive ? 'disabled' : '') + '>×</button>' +
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
        if (!shopId) {
            document.getElementById('categoriesContainer').innerHTML = '<div class="empty-state">Ошибка: неизвестный ресторан</div>';
            document.getElementById('productsContainer').innerHTML = '<div class="empty-state">Не удалось загрузить продукты: неизвестный ресторан</div>';
            return;
        }
        currentShopId = shopId;
        document.getElementById('shopListSection').style.display = 'none';
        document.getElementById('shopDetailsSection').style.display = 'block';
        document.getElementById('selectedShopName').innerText = shopName;
        document.getElementById('selectedShopInfo').innerText = 'Выберите категорию, чтобы увидеть продукты.';
        loadCategories(shopId);
        document.getElementById('productsContainer').innerHTML = '<div class="empty-state">Загрузка категорий...</div>';
    }

    function loadCategories(shopId) {
        const container = document.getElementById('categoriesContainer');
        const productsContainer = document.getElementById('productsContainer');
        container.innerHTML = '<div class="empty-state">Загрузка категорий...</div>';
        fetch('${pageContext.request.contextPath}/products/categories?shopId=' + shopId)
            .then(r => {
                if (r.status === 401) { window.location = '${pageContext.request.contextPath}/client/login'; return Promise.reject(new Error('Не авторизован')); }
                if (!r.ok) return r.text().then(t => { throw new Error(t || 'Сервер вернул ошибку при загрузке категорий'); });
                return r.json();
            })
            .then(categories => {
                container.innerHTML = '';
                if (!categories || !categories.length) {
                    container.innerHTML = '<div class="empty-state">Категории не найдены</div>';
                    productsContainer.innerHTML = '<div class="empty-state">Нет продуктов</div>';
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
            .catch(err => {
                console.error('Failed to load categories:', err);
                container.innerHTML = '<div class="empty-state">Ошибка загрузки категорий: ' + (err.message || '') + '</div>';
            });
    }

    function loadProducts(shopId, category) {
        const container = document.getElementById('productsContainer');
        container.innerHTML = '<div class="empty-state">Загрузка продуктов...</div>';
        fetch('${pageContext.request.contextPath}/products/by-shop?shopId=' + shopId + '&category=' + encodeURIComponent(category))
            .then(r => {
                if (r.status === 401) { window.location = '${pageContext.request.contextPath}/client/login'; return Promise.reject(new Error('Не авторизован')); }
                if (!r.ok) return r.text().then(t => { throw new Error(t || 'Сервер вернул ошибку при загрузке продуктов'); });
                return r.json();
            })
            .then(products => {
                container.innerHTML = '';
                if (!products || !products.length) {
                    container.innerHTML = '<div class="empty-state">Нет продуктов в этой категории</div>';
                    return;
                }
                products.forEach(product => container.appendChild(renderProductCard(product)));
            })
            .catch(err => {
                console.error('Failed to load products:', err);
                container.innerHTML = '<div class="empty-state">Ошибка загрузки продуктов: ' + (err.message || '') + '</div>';
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
                // alert('✅ ' + productName + ' добавлен в корзину');
                try { showToast('✅ ' + productName + ' добавлен в корзину'); } catch (e) { alert('✅ ' + productName + ' добавлен в корзину'); }
                loadCart();
            })
            .catch(() => alert('Не удалось добавить товар в корзину'));
    }

    function clearCart() {
        if (confirm('Вы уверены, что хотите очистить корзину?')) {
            fetch('${pageContext.request.contextPath}/cart/clear', { method: 'POST' })
                .then(loadCart);
        }
    }

    function loadCart() {
        if (!isLoggedIn) {
            document.getElementById('cartContainer').innerHTML = '<div class="empty-state">Пожалуйста, войдите, чтобы видеть корзину</div>';
            document.getElementById('cartSummary').style.display = 'none';
            return;
        }
        const container = document.getElementById('cartContainer');
        const summaryEl = document.getElementById('cartSummary');
        container.innerHTML = '<div class="empty-state">Загрузка корзины...</div>';
        fetch('${pageContext.request.contextPath}/cart/items-api')
            .then(r => {
                if (r.status === 401) { window.location = '${pageContext.request.contextPath}/client/login'; return Promise.reject(new Error('Не авторизован')); }
                if (!r.ok) return r.text().then(t => { throw new Error(t || 'Сервер вернул ошибку при загрузке корзины'); });
                return r.json();
            })
            .then(data => {
                const items = data.items || [];
                container.innerHTML = '';
                if (!items.length) {
                    container.innerHTML = '<div class="empty-state">Корзина пуста</div>';
                    summaryEl.style.display = 'none';
                    return;
                }
                let total = data.total || 0;
                let count = 0;
                // Group items by shop
                const groups = {};
                items.forEach(item => {
                    const shopId = item.shopId || 'unknown';
                    if (!groups[shopId]) {
                        groups[shopId] = { items: [], total: 0 };
                    }
                    groups[shopId].items.push(item);
                    groups[shopId].total += item.price * item.quantity;
                });

                // Render groups
                Object.keys(groups).forEach(shopId => {
                    const group = groups[shopId];
                    const shopName = group.items[0].shopName || 'Неизвестный магазин';
                    const shopWrapper = document.createElement('div');
                    shopWrapper.className = 'cart-shop-group';
                    shopWrapper.innerHTML = '' +
                        '<div class="cart-shop-header">' +
                        '  <div class="cart-shop-title">' + escapeHtml(shopName) + '</div>' +
                        '  <div class="cart-shop-subtotal">' + group.total + ' ₽</div>' +
                        '</div>';
                    group.items.forEach(item => {
                        shopWrapper.appendChild(renderCartItem(item));
                    });
                    container.appendChild(shopWrapper);
                });

                summaryEl.style.display = 'block';
                document.getElementById('cartCount').innerText = count;
                document.getElementById('cartTotal').innerText = total + ' ₽';
                document.getElementById('cartGrandTotal').innerText = total + ' ₽';
            })
            .catch(err => {
                console.error('Failed to load cart:', err);
                container.innerHTML = '<div class="empty-state">Ошибка загрузки корзины: ' + (err.message || '') + '</div>';
                summaryEl.style.display = 'none';
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

    function showCheckoutModal() {
        fetch('${pageContext.request.contextPath}/client/profile-api') // Предполагается новый API для данных клиента
            .then(r => r.json())
            .then(client => {
                const form = document.getElementById('checkoutForm');
                const address = client.address || {};
                form.country.value = address.country || '';
                form.city.value = address.city || '';
                form.street.value = address.street || '';
                form.building.value = address.building || '';
                form.apartment.value = address.apartment || '';
                form.entrance.value = address.entrance || '';
                form.floor.value = address.floor || '';
                form.addressNote.value = address.addressNote || '';
            });

        const summary = document.getElementById('modalOrderSummary');
        summary.innerHTML = document.getElementById('cartContainer').innerHTML;
        summary.querySelectorAll('button').forEach(b => b.remove()); // Убираем кнопки управления из саммари

        document.getElementById('checkoutModal').style.display = 'flex';
    }

    function hideCheckoutModal() {
        document.getElementById('checkoutModal').style.display = 'none';
    }

    function confirmCheckout() {
        const form = document.getElementById('checkoutForm');
        const formData = new FormData(form);
        const body = new URLSearchParams(formData).toString();

        fetch('${pageContext.request.contextPath}/cart/checkout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: body
        })
        .then(r => {
            if (r.status === 401) { window.location = '${pageContext.request.contextPath}/client/login'; return Promise.reject(new Error('Не авторизован')); }
            if (!r.ok) return r.text().then(t => { throw new Error(t || 'Сервер вернул ошибку при оформлении заказа'); });
            return r.json();
        })
        .then(data => {
            if (data.error) {
                const modalErr = document.getElementById('modalError');
                if (modalErr) { modalErr.style.display='block'; modalErr.innerText = data.error; }
                return;
            }
            alert('Заказ #' + data.orderId + ' оформлен. Статус: ' + data.status);
            hideCheckoutModal();
            loadCart();
            loadOrders();
        })
        .catch(err => {
            const modalErr = document.getElementById('modalError');
            if (modalErr) { modalErr.style.display='block'; modalErr.innerText = 'Ошибка: ' + (err.message || 'Не удалось оформить заказ'); }
            console.error('Checkout failed:', err);
        });
    }

    function loadOrders() {
        fetch('${pageContext.request.contextPath}/client/orders-api')
            .then(r => r.json())
            .then(orders => {
                console.log('Orders received:', orders);
                const container = document.getElementById('ordersContainer');
                if (!orders || !orders.length) {
                    container.innerHTML = '<div class="empty-state">Заказов пока нет</div>';
                    return;
                }
                container.innerHTML = orders.map(order => {
                    const orderId = order.id || 'N/A';
                    const status = order.status || 'Неизвестно';
                    const total = order.total || 0;
                    const createdAt = order.createdAt || '';
                    const items = order.items || [];
                    const itemsHtml = items.map(item => {
                        const name = escapeHtml(item.name || 'Неизвестный товар');
                        const qty = item.quantity || 0;
                        const price = item.price || 0;
                        return name + ' × ' + qty + ' — ' + price + ' ₽';
                    }).join('<br>');

                    const disabledAttr = (orderId === 'N/A' || isInactive) ? 'disabled' : '';

                    return '<div class="order-card">' +
                        '<div style="display:flex; justify-content:space-between;">' +
                        '<strong>Заказ #' + orderId + '</strong>' +
                        '<span>' + escapeHtml(createdAt) + '</span>' +
                        '</div>' +
                        '<p>Статус: <strong>' + escapeHtml(status) + '</strong></p>' +
                        '<p>Сумма: <strong>' + total + ' ₽</strong></p>' +
                        '<div class="order-items">' + (itemsHtml || 'Нет товаров') + '</div>' +
                        '<button class="btn btn-primary btn-small" onclick="repeatOrder(' + orderId + ')" ' + disabledAttr + '>Повторить</button>' +
                        '</div>';
                }).join('');
            })
            .catch((err) => {
                console.error('Failed to load orders:', err);
                document.getElementById('ordersContainer').innerHTML = '<div class="empty-state">Ошибка загрузки заказов</div>';
            });
    }

    function loadNotifications() {
        fetch('${pageContext.request.contextPath}/client/notifications-api')
            .then(r => r.json())
            .then(list => {
                console.log('Notifications received:', list);
                const container = document.getElementById('notificationsContainer');
                if (!list || !list.length) {
                    container.innerHTML = '<div class="empty-state">Уведомлений нет</div>';
                    return;
                }

                container.innerHTML = list.map(n => {
                    const type = n.type || 'Уведомление';
                    const message = n.message || '';
                    const createdAt = n.createdAt || n.timestamp || '';
                    const readFlag = n.isRead;
                    const isRead = (typeof readFlag === 'boolean' ? readFlag : n.read) ? '' : 'unread';
                    console.log('Notification:', type, 'isRead:', n.isRead, 'class:', isRead);

                    return '<div class="notification-card ' + isRead + '" data-is-read="' + n.isRead + '">' +
                        '<div style="display:flex; justify-content:space-between;">' +
                        '<strong>' + escapeHtml(type) + '</strong>' +
                        '<span>' + escapeHtml(createdAt) + '</span>' +
                        '</div>' +
                        '<p>' + escapeHtml(message) + '</p>' +
                        '</div>';
                }).join('');
            })
            .catch((err) => {
                console.error('Failed to load notifications:', err);
                document.getElementById('notificationsContainer').innerHTML = '<div class="empty-state">Ошибка загрузки уведомлений</div>';
            });
    }

    function markNotificationsRead() {
        fetch('${pageContext.request.contextPath}/client/notifications/readAll', {
            method: 'POST'
        })
            .then(r => {
                if (!r.ok) {
                    alert('Не удалось отметить уведомления как прочитанные');
                    return;
                }

                const btn = document.querySelector('button[onclick="markNotificationsRead()"]');
                if (btn) {
                    const originalText = btn.textContent;
                    btn.textContent = '✓ Отмечено!';
                    btn.disabled = true;
                    setTimeout(() => {
                        btn.textContent = originalText;
                        btn.disabled = false;
                    }, 2000);
                }

                document.querySelectorAll('.notification-card.unread').forEach(card => {
                    card.classList.remove('unread');
                    card.style.animation = 'markAsRead 0.5s ease-out';
                });

                setTimeout(loadNotifications, 500);
            })
            .catch(() => alert('Ошибка сети при попытке отметить уведомления как прочитанные'));
    }

    window.addEventListener('load', () => {
        loadShops();
        loadOrders();
        loadNotifications();
        // Если сервер forward'ил форму с formData (например при ошибках валидации),
        // не перезаписываем поля, а отображаем их как есть. Флаг вычисляется на сервере.
        const shouldFetchProfile = ${empty formData ? 'true' : 'false'};
         // activate tab from query param if present
         const params = new URLSearchParams(window.location.search);
         const tab = params.get('tab');
         if (tab) {
             const btn = document.querySelector('.tab-button[data-tab="' + tab + '"]');
             if (btn) {
                 btn.click();
             }
         }

        // handle profile tab alerts from server (error/updated)
        const profileAlert = document.getElementById('profileAlert');
        const profileSuccess = document.getElementById('profileSuccess');
        const errorMsg = params.get('error');
        const updated = params.get('updated') === 'true' || params.get('updated') === '1';
        function hideProfileSuccess() {
            if (profileSuccess) { profileSuccess.style.display = 'none'; profileSuccess.innerHTML = ''; }
        }
        function showProfileSuccess(msg) {
            if (!profileSuccess) return;
            profileSuccess.style.display = 'block';
            profileSuccess.style.background = '#f0fff4';
            profileSuccess.style.border = '1px solid #2ecc71';
            profileSuccess.style.color = '#1f7a3a';
            profileSuccess.innerHTML = '<strong>Готово:</strong> ' + msg;
            // auto-hide
            setTimeout(() => { try { profileSuccess.style.display = 'none'; } catch(e){} }, 4500);
        }

        if (tab === 'profile' && errorMsg) {
            profileAlert.style.display = 'block';
            profileAlert.style.background = '#fff0f0';
            profileAlert.style.border = '1px solid #ff4d4d';
            profileAlert.style.color = '#a70000';
            profileAlert.innerHTML = '<strong>Ошибка:</strong> ' + decodeURIComponent(errorMsg) + '<br><small>Пример правильного формата: Email — example@mail.com; Этаж — целое число; Номер здания — до 10 символов.</small>';
            // hide success if error present
            hideProfileSuccess();
        } else if (tab === 'profile' && (params.get('updated') === 'true' || params.get('updated') === '1')) {
            try { showToast('Профиль успешно обновлён'); } catch (e) { /* ignore */ }
            try { showProfileSuccess('Профиль успешно обновлён'); } catch (e) { /* ignore */ }
        }

        if (isInactive) {
            document.querySelectorAll('button, input, select, textarea').forEach(el => {
                if (!el.closest('.inactive-overlay')) el.disabled = true;
            });
        }

        // load profile data into the header and form only when server did not provide formData
        if (shouldFetchProfile === true || shouldFetchProfile === 'true') {
            loadProfileData();
        }
    });

    function loadProfileData() {
        fetch('${pageContext.request.contextPath}/client/profile-api')
            .then(r => r.json())
            .then(client => {
                // fill header (immutable fields)
                const nameEl = document.getElementById('profileName');
                const phoneEl = document.getElementById('profilePhone');
                if (nameEl) nameEl.innerText = client.name || '';
                if (phoneEl) phoneEl.innerText = client.phone || '';

                // fill editable fields
                const form = document.querySelector('#profile .profile-form');
                if (form) {
                    // prefer explicit elements by name/id to avoid relying on form.elements collection
                    const address = client.address || {};
                    const elEmail = document.getElementById('email'); if (elEmail) elEmail.value = client.email || '';
                    const elCountry = document.getElementById('country'); if (elCountry) elCountry.value = address.country || '';
                    const elCity = document.getElementById('city'); if (elCity) elCity.value = address.city || '';
                    const elStreet = document.getElementById('street'); if (elStreet) elStreet.value = address.street || '';
                    const elBuilding = document.getElementById('building'); if (elBuilding) elBuilding.value = address.building || '';
                    const elApartment = document.getElementById('apartment'); if (elApartment) elApartment.value = address.apartment || '';
                    const elFloor = document.getElementById('floor'); if (elFloor) elFloor.value = (address.floor !== null && address.floor !== undefined) ? address.floor : '';
                    const elNote = document.getElementById('addressNote'); if (elNote) elNote.value = address.addressNote || '';
                }
            });
    }

    // profile form AJAX submit handler
    (function(){
        const form = document.getElementById('profileForm');
        const errorBanner = document.getElementById('profileErrorBanner');
        const successBanner = document.getElementById('profileSuccessBanner');
        // helper to clear field errors
        function clearFieldErrors(){
            ['email','country','city','street','building','apartment','floor','addressNote'].forEach(f => {
                const el = document.getElementById('err-' + f);
                if (el) el.innerText = '';
            });
            if (errorBanner) { errorBanner.style.display='none'; errorBanner.innerHTML=''; }
        }
        if (!form) return;
        form.addEventListener('submit', function(e){
            e.preventDefault();
            clearFieldErrors();
            successBanner.style.display='none';
            const data = new URLSearchParams(new FormData(form));
            data.append('ajax','1');
            fetch('${pageContext.request.contextPath}/client/update-profile', {
                method: 'POST',
                headers: { 'X-Requested-With': 'XMLHttpRequest', 'Content-Type': 'application/x-www-form-urlencoded' },
                body: data.toString()
            }).then(async r => {
                if (r.ok) {
                    const resp = await r.json();
                    if (resp.success) {
                        successBanner.style.display='block';
                        successBanner.innerText = resp.message || 'Профиль успешно обновлён';
                        // update header info
                        loadProfileData();
                    }
                } else if (r.status === 400) {
                    const resp = await r.json();
                    const errs = resp.fieldErrors || {};
                    Object.keys(errs).forEach(k => {
                        const el = document.getElementById('err-' + k);
                        if (el) el.innerText = errs[k];
                    });
                    errorBanner.style.display='block';
                    errorBanner.innerHTML = '<strong>Ошибка валидации:</strong> проверьте помеченные поля.';
                } else {
                    const resp = await r.json().catch(()=>({message:'Ошибка сервера'}));
                    errorBanner.style.display='block';
                    errorBanner.innerHTML = '<strong>Ошибка:</strong> ' + (resp.message || 'Не удалось обновить профиль');
                }
            }).catch(err => {
                errorBanner.style.display='block';
                errorBanner.innerHTML = '<strong>Ошибка сети:</strong> ' + (err.message || '');
            });
        });
    })();

    /* Toast notifications */
    function showToast(message, isError = false) {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = 'toast' + (isError ? ' toast-error' : ' toast-success');
        toast.innerHTML = '' +
            '<div class="toast-message">' + message + '</div>' +
            '<button class="toast-close" onclick="this.parentElement.style.display=\'none\'">✕</button>';
        container.appendChild(toast);
        setTimeout(() => {
            toast.style.animation = 'toast-out 200ms forwards';
            setTimeout(() => { toast.remove(); }, 200); // remove after animation
        }, 3000);
    }
</script>

<!-- Toast container (bottom-right) -->
<div id="toast-container"></div>
</body>
</html>
