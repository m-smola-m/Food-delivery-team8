# 🎨 АРХИТЕКТУРА ФРОНТЕНДА НА SERVLET

> Проект: Food Delivery Team 8  
> Дата: 30 ноября 2025  
> Тип: Servlet-based веб-приложение

---

## 📋 ОГЛАВЛЕНИЕ

1. [Архитектура](#архитектура)
2. [Структура проекта](#структура-проекта)
3. [Слои приложения](#слои-приложения)
4. [Основные страницы](#основные-страницы)
5. [Маршруты и URL](#маршруты-и-url)
6. [Безопасность](#безопасность)
7. [Интеграция с бэком](#интеграция-с-бэком)

---

## 🏗️ АРХИТЕКТУРА

### MVC Паттерн (Model-View-Controller)

```
┌─────────────────────────────────────────────────┐
│           BROWSER / CLIENT                       │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│         SERVLET LAYER (Controllers)             │
│  • AuthServlet                                  │
│  • ClientServlet                                │
│  • ShopServlet                                  │
│  • OrderServlet                                 │
│  • CartServlet                                  │
│  • CourierServlet                               │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│         SERVICE LAYER (бизнес-логика)           │
│  • ClientService                                │
│  • OrderService                                 │
│  • CartService                                  │
│  • ShopInfoService                              │
│  • CourierService                               │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│         REPOSITORY LAYER (данные)               │
│  • ClientRepository                             │
│  • OrderRepository                              │
│  • CartRepository                               │
│  • ShopRepository                               │
│  • CourierRepository                            │
└─────────────────────────────────────────────────┘
```

---

## 📁 СТРУКТУРА ПРОЕКТА

```
src/
├── main/
│   ├── java/com/team8/fooddelivery/
│   │   ├── servlet/                          ← ВСЕ SERVLETS
│   │   │   ├── auth/
│   │   │   │   ├── AuthServlet.java
│   │   │   │   ├── LoginServlet.java
│   │   │   │   └── RegisterServlet.java
│   │   │   ├── client/
│   │   │   │   ├── ClientHomeServlet.java
│   │   │   │   ├── ClientProfileServlet.java
│   │   │   │   └── MyOrdersServlet.java
│   │   │   ├── shop/
│   │   │   │   ├── ShopListServlet.java
│   │   │   │   ├── ShopDetailsServlet.java
│   │   │   │   └── ProductsServlet.java
│   │   │   ├── cart/
│   │   │   │   ├── CartServlet.java
│   │   │   │   ├── CartAddServlet.java
│   │   │   │   └── CheckoutServlet.java
│   │   │   ├── order/
│   │   │   │   ├── OrderCreateServlet.java
│   │   │   │   ├── OrderStatusServlet.java
│   │   │   │   └── OrderTrackingServlet.java
│   │   │   ├── courier/
│   │   │   │   ├── CourierDashboardServlet.java
│   │   │   │   ├── CourierOrdersServlet.java
│   │   │   │   └── CourierStatusServlet.java
│   │   │   └── common/
│   │   │       ├── ErrorServlet.java
│   │   │       ├── NotFoundServlet.java
│   │   │       └── FileServlet.java
│   │   │
│   │   ├── filter/                            ← ФИЛЬТРЫ
│   │   │   ├── AuthenticationFilter.java
│   │   │   ├── AuthorizationFilter.java
│   │   │   └── LoggingFilter.java
│   │   │
│   │   ├── util/
│   │   │   ├── SessionManager.java
│   │   │   ├── RequestValidator.java
│   │   │   └── ResponseBuilder.java
│   │   │
│   │   ├── service/               (СУЩЕСТВУЕТ)
│   │   ├── repository/            (СУЩЕСТВУЕТ)
│   │   └── model/                 (СУЩЕСТВУЕТ)
│   │
│   └── webapp/                                 ← ФРОНТЕНД РЕСУРСЫ
│       ├── WEB-INF/
│       │   ├── web.xml                        ← КОНФИГУРАЦИЯ
│       │   └── jsp/
│       │       ├── layout/
│       │       │   ├── header.jsp
│       │       │   ├── footer.jsp
│       │       │   └── navbar.jsp
│       │       ├── auth/
│       │       │   ├── login.jsp
│       │       │   ├── register.jsp
│       │       │   └── forgot-password.jsp
│       │       ├── client/
│       │       │   ├── home.jsp
│       │       │   ├── profile.jsp
│       │       │   ├── my-orders.jsp
│       │       │   └── addresses.jsp
│       │       ├── shop/
│       │       │   ├── shop-list.jsp
│       │       │   ├── shop-details.jsp
│       │       │   └── products.jsp
│       │       ├── cart/
│       │       │   ├── view-cart.jsp
│       │       │   └── checkout.jsp
│       │       ├── order/
│       │       │   ├── create-order.jsp
│       │       │   ├── order-confirmation.jsp
│       │       │   └── track-order.jsp
│       │       ├── courier/
│       │       │   ├── dashboard.jsp
│       │       │   ├── available-orders.jsp
│       │       │   └── my-deliveries.jsp
│       │       └── error/
│       │           ├── 404.jsp
│       │           ├── 500.jsp
│       │           └── error.jsp
│       │
│       └── resources/                         ← СТАТИЧЕСКИЕ ФАЙЛЫ
│           ├── css/
│           │   ├── style.css
│           │   ├── responsive.css
│           │   └── theme.css
│           ├── js/
│           │   ├── main.js
│           │   ├── cart.js
│           │   ├── auth.js
│           │   ├── validation.js
│           │   └── utils.js
│           ├── images/
│           │   ├── logo.png
│           │   ├── icons/
│           │   └── backgrounds/
│           └── fonts/
│               └── ...
```

---

## 🧩 СЛОИ ПРИЛОЖЕНИЯ

### 1. PRESENTATION LAYER (Servlet + JSP)

**Servlet:**
- Обрабатывают HTTP запросы
- Парсят параметры
- Вызывают Service слой
- Возвращают JSP для отображения

**JSP:**
- Отображают данные
- Генерируют HTML
- Содержат простую логику отображения

### 2. SERVICE LAYER (Бизнес-логика)

**Существующие сервисы:**
- ClientService
- OrderService
- CartService
- ShopInfoService
- CourierService

**Задачи:**
- Валидация данных
- Бизнес-логика
- Управление транзакциями

### 3. REPOSITORY LAYER (Доступ к данным)

**Существующие репозитории:**
- ClientRepository
- OrderRepository
- CartRepository
- ShopRepository
- CourierRepository

**Задачи:**
- CRUD операции
- Запросы к БД

---

## 📄 ОСНОВНЫЕ СТРАНИЦЫ И СЦЕНАРИИ

### 🔐 Аутентификация

#### LOGIN PAGE
- URL: `/login`
- Метод: GET
- Показывает форму входа

#### LOGIN HANDLER
- URL: `/auth/login`
- Метод: POST
- Параметры: email, password
- Создаёт сессию

#### REGISTER PAGE
- URL: `/register`
- Метод: GET
- Показывает форму регистрации

#### REGISTER HANDLER
- URL: `/auth/register`
- Метод: POST
- Параметры: name, email, phone, password, confirmPassword

---

### 👤 ЛИЧНЫЙ КАБИНЕТ КЛИЕНТА

#### HOME PAGE (Клиент)
- URL: `/client/home`
- Показывает магазины и рекомендации
- Компоненты:
  - Поиск магазинов
  - Список магазинов
  - Фильтры по категориям
  - Корзина (иконка)

#### MY PROFILE
- URL: `/client/profile`
- Показывает профиль клиента
- Компоненты:
  - Информация о клиенте
  - Адреса доставки
  - История заказов (краткая)

#### MY ORDERS
- URL: `/client/my-orders`
- Показывает все заказы
- Компоненты:
  - Список заказов
  - Статусы
  - Кнопки действий (отследить, повторить)

#### MY ADDRESSES
- URL: `/client/addresses`
- Управление адресами доставки
- Компоненты:
  - Список адресов
  - Кнопки: добавить, редактировать, удалить

---

### 🏪 МАГАЗИНЫ И ТОВАРЫ

#### SHOP LIST
- URL: `/shop/list`
- Показывает все магазины
- Компоненты:
  - Поиск и фильтры
  - Сортировка
  - Рейтинг и время доставки

#### SHOP DETAILS
- URL: `/shop/details?id={shopId}`
- Показывает детали магазина
- Компоненты:
  - Информация о магазине
  - Меню (категории товаров)
  - Товары в каталоге

#### PRODUCTS
- URL: `/shop/products?shopId={shopId}`
- AJAX загрузка товаров
- Возвращает JSON

---

### 🛒 КОРЗИНА

#### VIEW CART
- URL: `/cart/view`
- Показывает товары в корзине
- Компоненты:
  - Список товаров
  - Изменение количества
  - Удаление товаров
  - Итого и доставка

#### ADD TO CART (AJAX)
- URL: `/cart/add`
- Метод: POST
- JSON: {productId, quantity, shopId}
- Возвращает: JSON статус

#### CHECKOUT
- URL: `/cart/checkout`
- Показывает форму оформления
- Компоненты:
  - Адрес доставки
  - Время доставки
  - Способ оплаты

---

### 📦 ЗАКАЗЫ

#### CREATE ORDER
- URL: `/order/create`
- Метод: POST
- Параметры: cartId, addressId, deliveryTime
- Создаёт заказ и очищает корзину

#### ORDER CONFIRMATION
- URL: `/order/confirmation?orderId={orderId}`
- Показывает подтверждение

#### TRACK ORDER
- URL: `/order/track?orderId={orderId}`
- Показывает статус заказа в реальном времени
- Компоненты:
  - Статус заказа
  - Информация о курьере
  - Карта (если нужна)

---

### 🚴 КАБИНЕТ КУРЬЕРА

#### COURIER DASHBOARD
- URL: `/courier/dashboard`
- Главная страница курьера
- Компоненты:
  - Статус (online/offline)
  - Доход за день
  - Статистика

#### AVAILABLE ORDERS
- URL: `/courier/orders/available`
- Список доступных заказов
- Компоненты:
  - Карточки заказов
  - Расстояние и вознаграждение
  - Кнопка "Взять заказ"

#### MY DELIVERIES
- URL: `/courier/deliveries/my`
- Текущие доставки
- Компоненты:
  - Карточки активных доставок
  - Детали заказа
  - Кнопки действий

---

## 🌐 МАРШРУТЫ И URL

### AUTH ROUTES
```
GET  /login                 → LoginServlet (форма)
POST /auth/login            → AuthServlet (обработка)
GET  /register              → RegisterServlet (форма)
POST /auth/register         → AuthServlet (обработка)
GET  /logout                → AuthServlet (выход)
GET  /auth/profile          → ProfileServlet
```

### CLIENT ROUTES
```
GET  /client/home           → ClientHomeServlet
GET  /client/profile        → ClientProfileServlet
GET  /client/my-orders      → MyOrdersServlet
GET  /client/addresses      → AddressesServlet
POST /client/address/add    → AddressServlet
POST /client/address/update → AddressServlet
POST /client/address/delete → AddressServlet
```

### SHOP ROUTES
```
GET  /shop/list             → ShopListServlet
GET  /shop/details          → ShopDetailsServlet
GET  /shop/products         → ProductsServlet (JSON AJAX)
GET  /shop/search           → ShopSearchServlet
```

### CART ROUTES
```
GET  /cart/view             → CartViewServlet
POST /cart/add              → CartAddServlet (JSON)
POST /cart/update           → CartUpdateServlet (JSON)
POST /cart/remove           → CartRemoveServlet (JSON)
GET  /cart/checkout         → CheckoutServlet
```

### ORDER ROUTES
```
POST /order/create          → OrderCreateServlet
GET  /order/confirmation    → OrderConfirmationServlet
GET  /order/track           → OrderTrackingServlet
GET  /order/status          → OrderStatusServlet (JSON AJAX)
GET  /order/history         → OrderHistoryServlet
```

### COURIER ROUTES
```
GET  /courier/dashboard     → CourierDashboardServlet
GET  /courier/orders        → CourierOrdersServlet
POST /courier/take-order    → CourierTakeOrderServlet
GET  /courier/deliveries    → CourierDeliveriesServlet
POST /courier/status        → CourierStatusServlet
POST /courier/complete      → CourierCompleteServlet
```

### STATIC RESOURCES
```
GET  /resources/css/*       → FileServlet
GET  /resources/js/*        → FileServlet
GET  /resources/images/*    → FileServlet
GET  /resources/fonts/*     → FileServlet
```

---

## 🔒 БЕЗОПАСНОСТЬ

### Аутентификация (SessionManager)
- Сессия создаётся после входа
- Хранит: userId, userRole, userEmail
- Срок действия: 30 минут (конфигурируется)
- Удаляется при выходе

### Авторизация (Filter)
```
AuthenticationFilter:
  ├─ Проверяет наличие сессии
  ├─ Редирект на /login если нет
  └─ Пропускает публичные страницы

AuthorizationFilter:
  ├─ Проверяет роль пользователя
  ├─ Блокирует доступ к /courier/* для клиентов
  └─ Блокирует доступ к /client/* для курьеров
```

### Валидация (RequestValidator)
- Проверка параметров
- Санитизация данных
- Защита от XSS
- Защита от SQL injection

### CSRF Protection
- Токены в формах
- Проверка источника

---

## 🔌 ИНТЕГРАЦИЯ С БЭКОМ

### Как Servlet вызывает Service

```java
// 1. Получить сервис
OrderService orderService = new OrderServiceImpl();

// 2. Вызвать бизнес-логику
List<Order> orders = orderService.getClientOrders(clientId);

// 3. Положить результат в request
request.setAttribute("orders", orders);

// 4. Forward на JSP
request.getRequestDispatcher("/WEB-INF/jsp/order/list.jsp")
    .forward(request, response);
```

### JSON API для AJAX

```java
// CartAddServlet для AJAX запроса
public class CartAddServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        
        CartService cartService = new CartServiceImpl();
        
        try {
            // Парсить JSON
            BufferedReader reader = request.getReader();
            JsonObject json = JsonParser.parseString(reader).getAsJsonObject();
            
            // Вызвать сервис
            Long cartId = cartService.addItem(
                json.get("productId").getAsLong(),
                json.get("quantity").getAsInt()
            );
            
            // Вернуть JSON ответ
            response.getWriter().write(new JsonObject()
                .addProperty("success", true)
                .addProperty("cartId", cartId)
                .toString()
            );
        } catch (Exception e) {
            response.getWriter().write(new JsonObject()
                .addProperty("success", false)
                .addProperty("error", e.getMessage())
                .toString()
            );
        }
    }
}
```

---

## 📦 ЗАВИСИМОСТИ ДЛЯ ФРОНТЕНДА

**Нужно добавить в pom.xml:**

```xml
<!-- Servlet API -->
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>6.0.0</version>
    <scope>provided</scope>
</dependency>

<!-- JSP/JSTL -->
<dependency>
    <groupId>jakarta.servlet.jsp</groupId>
    <artifactId>jakarta.servlet.jsp-api</artifactId>
    <version>3.1.0</version>
    <scope>provided</scope>
</dependency>

<dependency>
    <groupId>jakarta.servlet.jsp.jstl</groupId>
    <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
    <version>3.0.0</version>
</dependency>

<!-- JSON -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>

<!-- Tomcat для локальной разработки -->
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-core</artifactId>
    <version>10.1.11</version>
    <scope>provided</scope>
</dependency>
```

---

## 🎯 СЕССИОННОЕ УПРАВЛЕНИЕ

### SessionManager

```java
public class SessionManager {
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000; // 30 минут
    
    public static void createSession(HttpSession session, User user) {
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole());
        session.setAttribute("userEmail", user.getEmail());
        session.setMaxInactiveInterval((int)(SESSION_TIMEOUT / 1000));
    }
    
    public static User getUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return null;
        
        // Получить данные пользователя
        return getUserById(userId);
    }
    
    public static void invalidateSession(HttpSession session) {
        session.invalidate();
    }
    
    public static boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("userId") != null;
    }
}
```

---

## 📝 ПРИМЕРЫ РЕАЛИЗАЦИИ

### Пример 1: AuthServlet

```java
@WebServlet("/auth/login")
public class AuthServlet extends HttpServlet {
    private ClientService clientService = new ClientServiceImpl();
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        try {
            // Валидация
            RequestValidator.validateEmail(email);
            RequestValidator.validatePassword(password);
            
            // Проверка в БД
            Optional<Client> client = clientService.authenticate(email, password);
            
            if (client.isPresent()) {
                // Создать сессию
                SessionManager.createSession(request.getSession(), client.get());
                response.sendRedirect("/client/home");
            } else {
                request.setAttribute("error", "Invalid credentials");
                request.getRequestDispatcher("/login").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/login").forward(request, response);
        }
    }
}
```

---

## 🎨 ФРОНТЕНД СТРУКТУРА (HTML/CSS/JS)

### HTML Макеты

- Использовать Bootstrap 5 для responsive дизайна
- Semantic HTML5
- Accessibility (WCAG 2.1)

### JavaScript

- Vanilla JS для простых функций
- Fetch API для AJAX запросов
- Event delegation для динамического контента

### CSS

- CSS Grid для макета
- Flexbox для компонентов
- CSS переменные для темизации

---

## 🚀 РАЗВЁРТЫВАНИЕ

### Локальная разработка
```bash
mvn clean package
# Залить WAR в Tomcat
```

### Production
```bash
# Использовать Tomcat 10.1+
# Или встроенный Tomcat в Spring Boot
```

---

**Версия:** 1.0  
**Дата:** 30 ноября 2025  
**Статус:** Готовая архитектура для реализации

