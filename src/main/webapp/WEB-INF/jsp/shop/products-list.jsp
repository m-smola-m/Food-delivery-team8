<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
</html>
</body>
    </main>
        </table>
            </tbody>
                </c:forEach>
                    </tr>
                        </td>
                            </form>
                                        onclick="return confirm('Вы уверены?')">Удалить</button>
                                <button type="submit" class="btn btn-danger btn-small"
                                <input type="hidden" name="productId" value="${product.productId}">
                            <form method="POST" action="${pageContext.request.contextPath}/products/delete" style="display: inline;">
                               class="btn btn-secondary btn-small">Редактировать</a>
                            <a href="${pageContext.request.contextPath}/products/edit-form?id=${product.productId}"
                        <td>
                        </td>
                            </form>
                                </button>
                                    ${product.available ? 'Доступен' : 'Недоступен'}
                                <button type="submit" class="btn ${product.available ? 'btn-success' : 'btn-danger'} btn-small">
                                <input type="hidden" name="available" value="${product.available}">
                                <input type="hidden" name="productId" value="${product.productId}">
                            <form method="POST" action="${pageContext.request.contextPath}/products/toggle-availability" style="display: inline;">
                        <td>
                        <td>${product.cookingTimeMinutes} мин</td>
                        <td>${product.category}</td>
                        <td>${product.price} ₽</td>
                        <td>${product.name}</td>
                    <tr>
                <c:forEach var="product" items="${products}">
            <tbody>
            </thead>
                </tr>
                    <th>Действия</th>
                    <th>Доступен</th>
                    <th>Время готовки</th>
                    <th>Категория</th>
                    <th>Цена</th>
                    <th>Название</th>
                <tr>
            <thead>
        <table class="products-table">

        </a>
            + Добавить товар
        <a href="${pageContext.request.contextPath}/products/add-form" class="btn btn-primary">

        </c:if>
            <div class="alert alert-success">Товар удалён!</div>
        <c:if test="${not empty param.deleted}">
        </c:if>
            <div class="alert alert-success">Товар обновлён!</div>
        <c:if test="${not empty param.updated}">
        </c:if>
            <div class="alert alert-success">Товар добавлен!</div>
        <c:if test="${not empty param.added}">

        <h1>Управление товарами</h1>
    <main class="container">

    </nav>
        </div>
            </ul>
                <li><a href="${pageContext.request.contextPath}/auth/logout">Выход</a></li>
                <li><a href="${pageContext.request.contextPath}/products/list">Товары</a></li>
                <li><a href="${pageContext.request.contextPath}/shop/dashboard">Dashboard</a></li>
            <ul>
            <h1>Food Delivery - Магазин</h1>
        <div class="container">
    <nav class="navbar">
<body>
</head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <title>Товары - Food Delivery</title>
    <meta charset="UTF-8">
<head>
<html>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

