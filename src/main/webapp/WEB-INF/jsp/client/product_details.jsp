<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.name} - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <style>
        .product-details {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 40px;
            margin: 20px 0;
        }
        .product-image {
            max-width: 100%;
            height: auto;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        .product-info h1 {
            color: #333;
            margin-bottom: 10px;
        }
        .product-price {
            font-size: 24px;
            font-weight: bold;
            color: #28a745;
            margin: 10px 0;
        }
        .product-description {
            margin: 20px 0;
            line-height: 1.6;
        }
        .product-meta {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 10px;
            margin: 20px 0;
        }
        .product-meta div {
            padding: 10px;
            background: #f8f9fa;
            border-radius: 4px;
        }
        .rating {
            display: flex;
            align-items: center;
            gap: 10px;
            margin: 10px 0;
        }
        .stars {
            color: #ffc107;
        }
        .reviews-section {
            margin-top: 40px;
        }
        .review-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            margin: 10px 0;
            background: #fff;
        }
        .review-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }
        .review-rating {
            font-weight: bold;
        }
        .add-review-form {
            margin-top: 20px;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 8px;
            background: #f8f9fa;
        }
        .rating-input {
            display: flex;
            gap: 5px;
            margin: 10px 0;
        }
        .rating-input input[type="radio"] {
            display: none;
        }
        .rating-input label {
            font-size: 24px;
            color: #ddd;
            cursor: pointer;
        }
        .rating-input input[type="radio"]:checked ~ label,
        .rating-input label:hover,
        .rating-input label:hover ~ label {
            color: #ffc107;
        }
        @media (max-width: 768px) {
            .product-details {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/jsp/layout/navbar.jsp" %>

    <main class="container">
        <div class="product-details">
            <div class="product-image-container">
                <c:choose>
                    <c:when test="${not empty product.photoUrl and fn:startsWith(product.photoUrl, 'http')}">
                        <img src="${product.photoUrl}" alt="${product.name}" class="product-image">
                    </c:when>
                    <c:when test="${not empty product.photoUrl}">
                        <img src="${pageContext.request.contextPath}${product.photoUrl}" alt="${product.name}" class="product-image">
                    </c:when>
                    <c:otherwise>
                        <div style="width: 100%; height: 300px; background: #f8f9fa; display: flex; align-items: center; justify-content: center; border-radius: 8px; font-size: 100px;">
                            <script>
                                document.write(getProductEmoji('${product.name}'));
                            </script>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="product-info">
                <h1>${product.name}</h1>

                <div class="rating">
                    <div class="stars">
                        <c:forEach var="i" begin="1" end="5">
                            <c:choose>
                                <c:when test="${i <= averageRating}">
                                    ★
                                </c:when>
                                <c:otherwise>
                                    ☆
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </div>
                    <span>${averageRating > 0 ? String.format("%.1f", averageRating) : "Нет оценок"} (${reviews.size()} отзывов)</span>
                </div>

                <div class="product-price">${product.price} ₽</div>

                <div class="product-description">
                    ${product.description}
                </div>

                <div class="product-meta">
                    <div>
                        <strong>Категория:</strong> ${product.category}
                    </div>
                    <div>
                        <strong>Вес:</strong> ${product.weight} г
                    </div>
                    <div>
                        <strong>Время приготовления:</strong> ${product.cookingTimeMinutes.toMinutes()} мин
                    </div>
                    <div>
                        <strong>Доступность:</strong> ${product.available ? 'В наличии' : 'Нет в наличии'}
                    </div>
                </div>

                <button class="btn btn-primary" onclick="addToCart(${product.productId})">
                    Добавить в корзину
                </button>
            </div>
        </div>

        <div class="reviews-section">
            <h2>Отзывы (${reviews.size()})</h2>

            <div class="add-review-form">
                <h3>Оставить отзыв</h3>
                <form id="reviewForm">
                    <input type="hidden" name="productId" value="${product.productId}">

                    <div class="form-group">
                        <label>Рейтинг:</label>
                        <div class="rating-input">
                            <input type="radio" id="star5" name="rating" value="5"><label for="star5">★</label>
                            <input type="radio" id="star4" name="rating" value="4"><label for="star4">★</label>
                            <input type="radio" id="star3" name="rating" value="3"><label for="star3">★</label>
                            <input type="radio" id="star2" name="rating" value="2"><label for="star2">★</label>
                            <input type="radio" id="star1" name="rating" value="1"><label for="star1">★</label>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="comment">Комментарий:</label>
                        <textarea id="comment" name="comment" rows="4" placeholder="Поделитесь своим мнением..."></textarea>
                    </div>

                    <button type="submit" class="btn btn-primary">Отправить отзыв</button>
                </form>
            </div>

            <div id="reviewsList">
                <c:forEach var="review" items="${reviews}">
                    <div class="review-card">
                        <div class="review-header">
                            <div class="review-rating">
                                <c:forEach var="i" begin="1" end="5">
                                    <c:choose>
                                        <c:when test="${i <= review.rating}">
                                            ★
                                        </c:when>
                                        <c:otherwise>
                                            ☆
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                            <div class="review-date">
                                <fmt:formatDate value="${review.createdAt}" pattern="dd.MM.yyyy HH:mm"/>
                            </div>
                        </div>
                        <div class="review-comment">
                            ${review.comment}
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </main>

    <%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>

    <script>
        function addToCart(productId) {
            fetch('${pageContext.request.contextPath}/cart/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'productId=' + productId + '&quantity=1'
            })
            .then(response => {
                if (response.ok) {
                    // alert('Продукт добавлен в корзину!');
                    try { showToast('✅ Продукт добавлен в корзину!'); } catch(e) { alert('Продукт добавлен в корзину!'); }
                } else {
                    try { showToast('Ошибка при добавлении в корзину'); } catch(e) { alert('Ошибка при добавлении в корзину'); }
                }
            })
            .catch(error => {
                console.error('Error:', error);
                try { showToast('Ошибка при добавлении в корзину'); } catch(e) { alert('Ошибка при добавлении в корзину'); }
            });
        }

        function getProductEmoji(name) {
            if (!name) return '🍽️';
            const lowerName = name.toLowerCase();
            if (/(пицц|pizza)/.test(lowerName)) return '🍕';
            if (/(бургер|гамбургер|burger)/.test(lowerName)) return '🍔';
            if (/(сэндвич|sandwich|бутерброд)/.test(lowerName)) return '🥪';
            if (/(паста|спагетти|macaroni|pasta)/.test(lowerName)) return '🍝';
            if (/(суши|ролл|роллы|sushi|roll)/.test(lowerName)) return '🍣';
            if (/(рамен|рамэн|ramen|лапша)/.test(lowerName)) return '🍜';
            if (/(салат|salad)/.test(lowerName)) return '🥗';
            if (/(суп|soup)/.test(lowerName)) return '🍲';
            if (/(рыб|лосос|salmon|fish)/.test(lowerName)) return '🐟';
            if (/(куриц|цыпленок|chicken)/.test(lowerName)) return '🍗';
            if (/(говядин|мясо|steak|beef)/.test(lowerName)) return '🥩';
            if (/(свин|pork)/.test(lowerName)) return '🥓';
            if (/(кревет|shrimp|prawn|морепродукт)/.test(lowerName)) return '🍤';
            if (/(омар|раков|lobster|crab)/.test(lowerName)) return '🦞';
            if (/(тако|taco)/.test(lowerName)) return '🌮';
            if (/(шаурм|shawarma|буррито|burrito)/.test(lowerName)) return '🌯';
            if (/(фри|картофел|fries|potato)/.test(lowerName)) return '🍟';
            if (/(блин|панкейк|pancake)/.test(lowerName)) return '🥞';
            if (/(хлеб|булоч|булка|bake|bakery|bun|bagel)/.test(lowerName)) return '🍞';
            if (/(сыр|cheese)/.test(lowerName)) return '🧀';
            if (/(яйц|egg)/.test(lowerName)) return '🥚';
            if (/(торт|пирог|десерт|cake|pie|cookie|печеньк)/.test(lowerName)) return '🍰';
            if (/(морожен|ice cream|ice-cream|icecream)/.test(lowerName)) return '🍦';
            if (/(кофе|coffee)/.test(lowerName)) return '☕';
            if (/(чай|tea)/.test(lowerName)) return '🍵';
            if (/(сок|напиток|juice|drink|smoothie|milkshake)/.test(lowerName)) return '🥤';
            if (/(коктейль|cocktail|mojito|martini)/.test(lowerName)) return '🍹';
            if (/(пиво|beer)/.test(lowerName)) return '🍺';
            if (/(вино|wine)/.test(lowerName)) return '🍷';
            if (/(фрукт|яблок|груша|banana|яблоко|orange|апельсин)/.test(lowerName)) return '🍎';
            if (/(овощ|томат|огурец|carrot|vegetable|veggie)/.test(lowerName)) return '🥕';
            if (/(орех|nuts|snack)/.test(lowerName)) return '🥜';
            if (/(печень|cookie|cupcake|muffin|сладко|сладкое)/.test(lowerName)) return '🍪';
            if (/(хотдог|hotdog)/.test(lowerName)) return '🌭';
            return '🍽️';
        }

        document.getElementById('reviewForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);
            const rating = formData.get('rating');
            const comment = formData.get('comment');

            if (!rating) {
                try { showToast('Пожалуйста, выберите рейтинг'); } catch(e){ alert('Пожалуйста, выберите рейтинг'); }
                return;
            }

            fetch('${pageContext.request.contextPath}/product/review/${product.productId}', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'rating=' + rating + '&comment=' + encodeURIComponent(comment)
            })
            .then(response => {
                if (response.ok) {
                    try { showToast('Отзыв добавлен!'); } catch(e){ alert('Отзыв добавлен!'); }
                    location.reload();
                } else {
                    try { showToast('Ошибка при добавлении отзыва'); } catch(e){ alert('Ошибка при добавлении отзыва'); }
                }
            })
            .catch(error => {
                console.error('Error:', error);
                try { showToast('Ошибка при добавлении отзыва'); } catch(e){ alert('Ошибка при добавлении отзыва'); }
            });
        });
    </script>
</body>
</html>
