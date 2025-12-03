<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${shop.naming} - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/layout/navbar.jsp" %>

    <main class="container">
        <div class="shop-header">
            <h1>${shop.naming}</h1>
            <p class="description">${shop.description}</p>
            <p class="rating">★ ${shop.rating}</p>
        </div>

        <div class="categories-filter">
            <form method="GET" action="${pageContext.request.contextPath}/shop/details">
                <input type="hidden" name="id" value="${shop.shopId}">
                <label for="category">Категория:</label>
                <select name="category" id="category" onchange="this.form.submit()">
                    <option value="">Все</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat}" ${param.category == cat ? 'selected' : ''}>${cat}</option>
                    </c:forEach>
                </select>
            </form>
        </div>

        <div class="products-grid">
            <c:forEach var="product" items="${shop.products}">
                <c:if test="${empty param.category or product.category == param.category}">
                    <div class="product-card">
                        <h3>${product.name}</h3>
                        <p class="description">${product.description}</p>
                        <p class="price">${product.price} ₽</p>
                        <form method="POST" action="${pageContext.request.contextPath}/cart/add">
                            <input type="hidden" name="productId" value="${product.productId}">
                            <label class="sr-only" for="qty-${product.productId}">Количество</label>
                            <input id="qty-${product.productId}" type="number" name="quantity" min="1" value="1">
                            <button type="submit" class="btn btn-primary">В корзину</button>
                        </form>
                    </div>
                </c:if>
            </c:forEach>
        </div>
    </main>

    <%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
</body>
</html>
