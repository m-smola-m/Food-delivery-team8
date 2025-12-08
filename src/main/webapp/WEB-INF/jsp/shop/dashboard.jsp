<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Кабинет магазина - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/jsp/layout/navbar.jsp" %>

<main class="container">
    <h1>Панель управления магазином</h1>
    
    <c:if test="${not empty param.updated}">
        <div class="alert alert-success">Данные успешно обновлены!</div>
    </c:if>
    
    <div class="grid-2">
    <!-- Первая колонка: Основная информация -->
    <section class="card">
        <div class="section-header">
            <div>
                <h2>Статус магазина</h2>
                <p class="muted">Текущий статус: ${shop.status}</p>
            </div>
            <span class="status-badge status-${shop.status}">${shop.status}</span>
        </div>
        <c:if test="${shop.status == 'ACTIVE' || shop.status == 'APPROVED'}">
            <form method="POST" action="${pageContext.request.contextPath}/shop/update-status" class="stacked-form">
                <label>Изменить статус</label>
                <select name="status" required>
                    <option value="ACTIVE" ${shop.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                    <option value="APPROVED" ${shop.status == 'APPROVED' ? 'selected' : ''}>APPROVED</option>
                    <option value="SUSPENDED" ${shop.status == 'SUSPENDED' ? 'selected' : ''}>SUSPENDED</option>
                    <option value="CLOSED" ${shop.status == 'CLOSED' ? 'selected' : ''}>CLOSED</option>
                </select>
                <button type="submit" class="btn btn-primary">Сохранить</button>
            </form>
        </c:if>
        <c:if test="${shop.status != 'ACTIVE' && shop.status != 'APPROVED'}">
            <p class="muted">Для изменения статуса обратитесь к администратору.</p>
        </c:if>
    </section>

    <section class="card">
        <h2>Публичная информация</h2>
        <form method="POST" action="${pageContext.request.contextPath}/shop/update-public" class="stacked-form">
            <label>Витринное название</label>
            <input type="text" name="naming" value="${shop.naming}" required>

            <label>Описание</label>
            <textarea name="description" rows="3">${shop.description}</textarea>

            <label>Тип/категория</label>
            <select name="type" required>
                <c:forEach var="shopType" items="${shopTypes}">
                    <option value="${shopType}" ${shop.type == shopType ? 'selected' : ''}>${shopType.displayName}</option>
                </c:forEach>
            </select>

            <label>Публичный телефон</label>
            <input type="tel" name="publicPhone" value="${shop.publicPhone}" required>

            <label>Публичная почта</label>
            <input type="email" name="publicEmail" value="${shop.publicEmail}" required>

            <button type="submit" class="btn btn-primary">Обновить данные</button>
        </form>
    </section>

    <section class="card">
        <h2>Безопасность аккаунта</h2>
        <form method="POST" action="${pageContext.request.contextPath}/shop/update-contacts" class="stacked-form">
            <p class="muted">Смена emailForAuth / phoneForAuth потребует подтверждения кода.</p>
            <label>Новый email для входа</label>
            <input type="email" name="emailForAuth" placeholder="example@gmail.com" value="${shop.emailForAuth}">
            <label>Новый телефон для входа</label>
            <input type="tel" name="phoneForAuth" placeholder="89XXXXXXXXX" value="${shop.phoneForAuth}">
            <label>Пароль для подтверждения</label>
            <input type="password" name="password" required>
            <label>Код из почты/телефона</label>
            <input type="text" name="otp" placeholder="6 цифр">
            <button type="submit" class="btn btn-secondary">Сохранить контакты</button>
        </form>

        <form method="POST" action="${pageContext.request.contextPath}/shop/update-password" class="stacked-form" style="margin-top: 20px;">
            <label>Старый пароль</label>
            <input type="password" name="oldPassword" required>
            <label>Новый пароль</label>
            <input type="password" name="newPassword" minlength="8" placeholder="Спецзнак, цифра, заглавная" required>
            <label>Подтверждение кода</label>
            <input type="text" name="code" placeholder="Из email/телефона" required>
            <button type="submit" class="btn btn-primary">Сменить пароль</button>
        </form>
    </section>

    <!-- Вторая колонка: Адрес -->
    <section class="card wide" style="grid-column: 1 / -1;">
        <h2>Адрес магазина</h2>
        <form method="POST" action="${pageContext.request.contextPath}/shop/update-address" class="form-grid">
            <div class="form-group">
                <label>Страна</label>
                <input type="text" name="country" value="${shop.address != null ? shop.address.country : ''}" required>
            </div>
            <div class="form-group">
                <label>Город</label>
                <input type="text" name="city" value="${shop.address != null ? shop.address.city : ''}" required>
            </div>
            <div class="form-group">
                <label>Улица</label>
                <input type="text" name="street" value="${shop.address != null ? shop.address.street : ''}" required>
            </div>
            <div class="form-group">
                <label>Дом</label>
                <input type="text" name="building" value="${shop.address != null ? shop.address.building : ''}" required>
            </div>
            <div class="form-group">
                <label>Квартира/офис</label>
                <input type="text" name="apartment" value="${shop.address != null ? shop.address.apartment : ''}">
            </div>
            <div class="form-group">
                <label>Подъезд</label>
                <input type="text" name="entrance" value="${shop.address != null ? shop.address.entrance : ''}">
            </div>
            <div class="form-group">
                <label>Этаж</label>
                <input type="number" name="floor" value="${shop.address != null ? shop.address.floor : ''}">
            </div>
            <div class="form-group">
                <label>Район</label>
                <input type="text" name="district" value="${shop.address != null ? shop.address.district : ''}">
            </div>
            <div class="form-group">
                <label>Координаты (широта)</label>
                <input type="number" step="0.000001" name="latitude" value="${shop.address != null ? shop.address.latitude : ''}">
            </div>
            <div class="form-group">
                <label>Координаты (долгота)</label>
                <input type="number" step="0.000001" name="longitude" value="${shop.address != null ? shop.address.longitude : ''}">
            </div>
            <div class="form-group full">
                <label>Комментарий к адресу</label>
                <textarea name="addressNote" rows="2">${shop.address != null ? shop.address.addressNote : ''}</textarea>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Сохранить адрес</button>
            </div>
        </form>
    </section>
    </div>
</main>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
</body>
</html>
