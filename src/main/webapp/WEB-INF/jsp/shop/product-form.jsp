<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Добавить товар - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="container">
            <h1>Food Delivery - Магазин</h1>
            <ul>
                <li><a href="${pageContext.request.contextPath}/products/list">Товары</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout">Выход</a></li>
            </ul>
        </div>
    </nav>

    <main class="container">
        <h1>${requestScope.isEdit ? 'Редактировать товар' : 'Добавить товар'}</h1>

        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <form method="POST" action="${requestScope.isEdit ? pageContext.request.contextPath.concat('/products/update') : pageContext.request.contextPath.concat('/products/add')}"
              class="product-form">

            <c:if test="${requestScope.isEdit}">
                <input type="hidden" name="productId" value="${product.productId}">
            </c:if>

            <div class="form-group">
                <label>Название:</label>
                <input type="text" name="name" value="${product.name}" required>
            </div>

            <div class="form-group">
                <label>Описание:</label>
                <textarea name="description">${product.description}</textarea>
            </div>

            <div class="form-group">
                <label>Цена:</label>
                <input type="number" name="price" value="${product.price}" step="0.01" required>
            </div>

            <div class="form-group">
                <label>Категория:</label>
                <select name="category" required>
                    <option value="">-- Выберите --</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat}" ${product.category == cat ? 'selected' : ''}>${cat}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label>Время готовки (мин):</label>
                <input type="number" name="cookingTime" value="${product.cookingTimeMinutes}" min="0">
            </div>

            <div class="form-group">
                <label>
                    <input type="checkbox" name="isAvailable" ${product.available ? 'checked' : ''}>
                    Доступен
                </label>
            </div>

            <button type="submit" class="btn btn-primary">
                ${requestScope.isEdit ? 'Обновить' : 'Добавить'}
            </button>
            <a href="${pageContext.request.contextPath}/products/list" class="btn btn-secondary">Отмена</a>
        </form>
    </main>
</body>
</html>

