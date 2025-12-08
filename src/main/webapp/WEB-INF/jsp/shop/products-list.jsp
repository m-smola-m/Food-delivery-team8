<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Товары - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/layout/navbar.jsp" %>

    <main class="container">
        <div class="section-header">
            <div>
                <h1>Управление товарами</h1>
                <p class="muted">Добавляйте блюда, меняйте цены, доступность и карточки без JavaScript.</p>
            </div>
            <a href="${pageContext.request.contextPath}/products/add-form" class="btn btn-primary">+ Добавить товар</a>
        </div>

        <c:if test="${not empty param.added}">
            <div class="alert alert-success">Товар добавлен!</div>
        </c:if>
        <c:if test="${not empty param.updated}">
            <div class="alert alert-success">Товар обновлён!</div>
        </c:if>
        <c:if test="${not empty param.deleted}">
            <div class="alert alert-success">Товар удалён!</div>
        </c:if>

        <table class="products-table">
            <thead>
                <tr>
                    <th>Название</th>
                    <th>Цена</th>
                    <th>Категория</th>
                    <th>Вес</th>
                    <th>Время готовки</th>
                    <th>Доступность</th>
                    <th>Действия</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="product" items="${products}">
                    <tr>
                        <td>
                            <div class="product-name">${product.name}</div>
                            <div class="muted small">${product.description}</div>
                        </td>
                        <td>${product.price} ₽</td>
                        <td>${product.category}</td>
                        <td>${product.weight} г</td>
                        <td>${product.cookingTimeMinutes} мин</td>
                        <td>
                            <form method="POST" action="${pageContext.request.contextPath}/products/toggle-availability" class="inline-form">
                                <input type="hidden" name="productId" value="${product.productId}">
                                <input type="hidden" name="available" value="${product.available}">
                                <button type="submit" class="btn ${product.available ? 'btn-success' : 'btn-danger'} btn-small">
                                    ${product.available ? 'Доступен' : 'Недоступен'}
                                </button>
                            </form>
                        </td>
                        <td class="actions">
                            <a href="${pageContext.request.contextPath}/products/edit-form?id=${product.productId}" class="btn btn-secondary btn-small">Редактировать</a>
                            <form method="POST" action="${pageContext.request.contextPath}/products/delete" class="inline-form">
                                <input type="hidden" name="productId" value="${product.productId}">
                                <button type="submit" class="btn btn-danger btn-small">Удалить</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </main>
    <%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
</body>
</html>
