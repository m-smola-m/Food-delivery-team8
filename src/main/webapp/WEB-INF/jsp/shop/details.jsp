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
        <div class="shop-header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 12px; margin-bottom: 30px;">
            <h1 style="margin: 0 0 10px 0;">${shop.naming}</h1>
            <p class="description" style="font-size: 16px; margin: 10px 0;">${shop.description}</p>
            <div style="display: flex; gap: 20px; margin-top: 15px; flex-wrap: wrap;">
                <p class="rating" style="margin: 0; font-size: 18px; font-weight: bold;">★ ${shop.rating != null ? shop.rating : '0.0'}</p>
                <c:if test="${shop.type != null}">
                    <p style="margin: 0; font-size: 14px; opacity: 0.9;">Тип: ${shop.type.displayName}</p>
                </c:if>
                <c:if test="${shop.publicPhone != null}">
                    <p style="margin: 0; font-size: 14px; opacity: 0.9;">☎ ${shop.publicPhone}</p>
                </c:if>
                <c:if test="${shop.publicEmail != null}">
                    <p style="margin: 0; font-size: 14px; opacity: 0.9;">📧 ${shop.publicEmail}</p>
                </c:if>
            </div>
        </div>

        <div class="categories-filter" style="margin-bottom: 25px; padding: 15px; background: #f8f9fa; border-radius: 8px;">
            <form method="GET" action="${pageContext.request.contextPath}/shop/details" style="display: flex; align-items: center; gap: 15px; flex-wrap: wrap;">
                <input type="hidden" name="id" value="${shop.shopId}">
                <label for="category" style="font-weight: bold; margin: 0;">Категория:</label>
                <select name="category" id="category" onchange="this.form.submit()" style="padding: 8px 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 16px; flex: 1; min-width: 200px;">
                    <option value="">Все категории</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat}" ${param.category == cat ? 'selected' : ''}>${cat}</option>
                    </c:forEach>
                </select>
            </form>
        </div>

        <div class="products-grid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px;">
            <c:choose>
                <c:when test="${shop.products != null and not empty shop.products}">
                    <c:forEach var="product" items="${shop.products}">
                        <c:if test="${empty param.category or product.category == param.category}">
                            <div class="product-card" style="border: 1px solid #ddd; border-radius: 8px; padding: 20px; background: #fff; transition: box-shadow 0.2s;">
                                <h3 style="margin-top: 0; color: #2c3e50;">${product.name != null ? product.name : 'Без названия'}</h3>
                                <p class="description" style="color: #666; margin: 10px 0; font-size: 14px;">${product.description != null ? product.description : (product.descriptionOfProduct != null ? product.descriptionOfProduct : '')}</p>
                                <c:if test="${product.weight != null and product.weight > 0}">
                                    <p style="color: #888; font-size: 12px; margin: 5px 0;">Вес: ${product.weight} г</p>
                                </c:if>
                                <p class="price" style="font-size: 24px; font-weight: bold; color: #28a745; margin: 15px 0;">${product.price != null ? product.price : '0'} ₽</p>
                                <form method="POST" action="${pageContext.request.contextPath}/cart/add" style="display: flex; gap: 10px; align-items: center; margin-top: 15px;">
                                    <input type="hidden" name="productId" value="${product.productId}">
                                    <label class="sr-only" for="qty-${product.productId}">Количество</label>
                                    <input id="qty-${product.productId}" type="number" name="quantity" min="1" value="1" style="width: 70px; padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
                                    <button type="submit" class="btn btn-primary" style="flex: 1;">В корзину</button>
                                </form>
                            </div>
                        </c:if>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div style="grid-column: 1 / -1; text-align: center; padding: 40px; color: #666; border: 1px dashed #ddd; border-radius: 8px;">
                        <p>В этом магазине пока нет товаров.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
</body>
</html>
