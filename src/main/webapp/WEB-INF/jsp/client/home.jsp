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
        .tabs {
            display: flex;
            gap: 10px;
            border-bottom: 2px solid #ddd;
            margin-bottom: 20px;
        }
        .tab-button {
            padding: 10px 20px;
            background: none;
            border: none;
            cursor: pointer;
            font-size: 16px;
            border-bottom: 3px solid transparent;
            transition: all 0.3s;
        }
        .tab-button.active {
            border-bottom-color: #007bff;
            color: #007bff;
            font-weight: bold;
        }
        .tab-content {
            display: none;
        }
        .tab-content.active {
            display: block;
        }
        .shops-grid, .products-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        .shop-card, .product-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            cursor: pointer;
            transition: box-shadow 0.3s;
        }
        .shop-card:hover, .product-card:hover {
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        .product-card {
            display: flex;
            flex-direction: column;
        }
        .product-price {
            font-size: 18px;
            font-weight: bold;
            color: #28a745;
            margin: 10px 0;
        }
        .product-actions {
            display: flex;
            gap: 10px;
            margin-top: auto;
        }
        .btn-small {
            padding: 5px 10px;
            font-size: 14px;
        }
        .cart-items {
            margin-top: 20px;
        }
        .cart-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 8px;
            margin-bottom: 10px;
        }
        .cart-item-info {
            flex: 1;
        }
        .cart-item-quantity {
            display: flex;
            gap: 5px;
            align-items: center;
        }
        .cart-summary {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-top: 20px;
        }
        .summary-row {
            display: flex;
            justify-content: space-between;
            margin: 10px 0;
            font-size: 16px;
        }
        .summary-total {
            font-size: 20px;
            font-weight: bold;
            color: #28a745;
            border-top: 2px solid #ddd;
            padding-top: 10px;
            margin-top: 10px;
        }
        .profile-form {
            max-width: 500px;
            margin-top: 20px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        .form-group input, .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }
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
            <button class="tab-button active" onclick="switchTab('restaurants')">🏪 Рестораны</button>
            <button class="tab-button" onclick="switchTab('cart')">🛒 Корзина</button>
            <button class="tab-button" onclick="switchTab('profile')">👤 Профиль</button>
        </div>

        <!-- Вкладка Рестораны -->
        <div id="restaurants" class="tab-content active">
            <h2>Доступные рестораны</h2>

            <div class="category-filter" style="margin-bottom: 20px;">
                <label>Выберите категорию:</label>
                <select id="categoryFilter" onchange="filterByCategory()" style="padding: 10px; margin-left: 10px;">
                    <option value="">Все категории</option>
                    <option value="BAKERY">Хлебобулочные</option>
                    <option value="MAIN_DISH">Основные блюда</option>
                    <option value="DESSERT">Десерты</option>
                    <option value="DRINK">Напитки</option>
                    <option value="OTHER">Другое</option>
                </select>
            </div>

            <div class="shops-grid" id="shopsContainer">
                <p>Загрузка ресторанов...</p>
            </div>
        </div>

        <!-- Вкладка Корзина -->
        <div id="cart" class="tab-content">
            <h2>Ваша корзина</h2>
            <div class="cart-items" id="cartContainer">
                <p>Корзина пуста</p>
            </div>
            <div class="cart-summary" id="cartSummary" style="display:none;">
                <div class="summary-row">
                    <span>Количество товаров:</span>
                    <span id="cartCount">0</span>
                </div>
                <div class="summary-row">
                    <span>Сумма:</span>
                    <span id="cartTotal">0 ₽</span>
                </div>
                <div class="summary-total">
                    <div class="summary-row">
                        <span>Итого:</span>
                        <span id="cartGrandTotal">0 ₽</span>
                    </div>
                </div>
                <button class="btn btn-success" style="width: 100%; margin-top: 20px;">Оформить заказ</button>
            </div>
        </div>

        <!-- Вкладка Профиль -->
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

            <div style="margin-top: 30px;">
                <h3>Опасная зона</h3>
                <button onclick="if(confirm('Вы уверены?')) location.href='${pageContext.request.contextPath}/client/deactivate'" class="btn btn-danger">
                    Деактивировать аккаунт
                </button>
            </div>
        </div>
    </main>

    <script>
        function switchTab(tabName) {
            // Скрыть все вкладки
            document.querySelectorAll('.tab-content').forEach(tab => {
                tab.classList.remove('active');
            });

            // Деактивировать все кнопки
            document.querySelectorAll('.tab-button').forEach(btn => {
                btn.classList.remove('active');
            });

            // Показать выбранную вкладку
            document.getElementById(tabName).classList.add('active');
            event.target.classList.add('active');

            // Загрузить данные если нужно
            if (tabName === 'restaurants') {
                loadShops();
            } else if (tabName === 'cart') {
                loadCart();
            }
        }

        function loadShops() {
            fetch('${pageContext.request.contextPath}/shop/list-api')
                .then(response => response.json())
                .then(shops => {
                    let html = '';
                    if (shops.length === 0) {
                        html = '<p>Нет доступных ресторанов</p>';
                    } else {
                        shops.forEach(shop => {
                            html += `
                                <div class="shop-card" onclick="viewShopProducts(${shop.shopId})">
                                    <h3>${shop.naming}</h3>
                                    <p>${shop.description}</p>
                                    <p style="color: #666; font-size: 14px;">📧 ${shop.publicEmail}</p>
                                </div>
                            `;
                        });
                    }
                    document.getElementById('shopsContainer').innerHTML = html;
                })
                .catch(error => {
                    console.error('Ошибка загрузки ресторанов:', error);
                    document.getElementById('shopsContainer').innerHTML = '<p>Ошибка загрузки ресторанов</p>';
                });
        }

        function viewShopProducts(shopId) {
            fetch(`${pageContext.request.contextPath}/products/by-shop?shopId=` + shopId)
                .then(response => response.json())
                .then(products => {
                    let html = '<h3>Продукты</h3><div class="products-grid">';
                    if (products.length === 0) {
                        html += '<p>Нет продуктов</p>';
                    } else {
                        products.forEach(product => {
                            html += `
                                <div class="product-card">
                                    <h4>${product.name}</h4>
                                    <p>${product.description}</p>
                                    <p style="color: #666; font-size: 12px;">Вес: ${product.weight}g</p>
                                    <div class="product-price">${product.price} ₽</div>
                                    <div class="product-actions">
                                        <button onclick="addToCart(${product.productId}, '${product.name}', ${product.price})"
                                                class="btn btn-success btn-small">+ Добавить</button>
                                    </div>
                                </div>
                            `;
                        });
                    }
                    html += '</div>';
                    document.getElementById('shopsContainer').innerHTML = html;
                })
                .catch(error => {
                    console.error('Ошибка загрузки продуктов:', error);
                });
        }

        function addToCart(productId, productName, price) {
            fetch('${pageContext.request.contextPath}/cart/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `productId=` + productId + `&quantity=1`
            })
            .then(response => response.json())
            .then(data => {
                alert('✅ ' + productName + ' добавлен в корзину');
                loadCart();
            })
            .catch(error => console.error('Ошибка:', error));
        }

        function loadCart() {
            fetch('${pageContext.request.contextPath}/cart/items-api')
                .then(response => response.json())
                .then(items => {
                    let html = '';
                    let total = 0;
                    let count = 0;

                    if (items.length === 0) {
                        html = '<p>Корзина пуста</p>';
                        document.getElementById('cartSummary').style.display = 'none';
                    } else {
                        items.forEach(item => {
                            let itemTotal = item.price * item.quantity;
                            total += itemTotal;
                            count += item.quantity;
                            html += `
                                <div class="cart-item">
                                    <div class="cart-item-info">
                                        <h4>${item.name}</h4>
                                        <p>${item.price} ₽ × ${item.quantity} = ${itemTotal} ₽</p>
                                    </div>
                                    <div class="cart-item-quantity">
                                        <button onclick="updateQuantity(${item.cartItemId}, ${item.quantity - 1})" class="btn btn-small">−</button>
                                        <span>${item.quantity}</span>
                                        <button onclick="updateQuantity(${item.cartItemId}, ${item.quantity + 1})" class="btn btn-small">+</button>
                                        <button onclick="removeFromCart(${item.cartItemId})" class="btn btn-danger btn-small">✕ Удалить</button>
                                    </div>
                                </div>
                            `;
                        });
                        document.getElementById('cartSummary').style.display = 'block';
                        document.getElementById('cartCount').textContent = count;
                        document.getElementById('cartTotal').textContent = total + ' ₽';
                        document.getElementById('cartGrandTotal').textContent = total + ' ₽';
                    }
                    document.getElementById('cartContainer').innerHTML = html;
                })
                .catch(error => {
                    console.error('Ошибка загрузки корзины:', error);
                    document.getElementById('cartContainer').innerHTML = '<p>Ошибка загрузки корзины</p>';
                });
        }

        function updateQuantity(cartItemId, newQuantity) {
            if (newQuantity < 1) {
                removeFromCart(cartItemId);
                return;
            }

            fetch('${pageContext.request.contextPath}/cart/update', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `cartItemId=` + cartItemId + `&quantity=` + newQuantity
            })
            .then(() => loadCart())
            .catch(error => console.error('Ошибка:', error));
        }

        function removeFromCart(cartItemId) {
            fetch('${pageContext.request.contextPath}/cart/remove?cartItemId=' + cartItemId, {
                method: 'POST'
            })
            .then(() => {
                alert('✅ Товар удален из корзины');
                loadCart();
            })
            .catch(error => console.error('Ошибка:', error));
        }

        function filterByCategory() {
            const category = document.getElementById('categoryFilter').value;
            if (category) {
                fetch(`${pageContext.request.contextPath}/products/by-category?category=` + category)
                    .then(response => response.json())
                    .then(products => {
                        let html = '<div class="products-grid">';
                        products.forEach(product => {
                            html += `
                                <div class="product-card">
                                    <h4>${product.name}</h4>
                                    <p>${product.description}</p>
                                    <div class="product-price">${product.price} ₽</div>
                                    <button onclick="addToCart(${product.productId}, '${product.name}', ${product.price})"
                                            class="btn btn-success">+ Добавить</button>
                                </div>
                            `;
                        });
                        html += '</div>';
                        document.getElementById('shopsContainer').innerHTML = html;
                    })
                    .catch(error => console.error('Ошибка:', error));
            } else {
                loadShops();
            }
        }

        // Загрузить рестораны при загрузке страницы
        window.addEventListener('load', loadShops);
    </script>
</body>
</html>
