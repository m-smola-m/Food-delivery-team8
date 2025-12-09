<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Регистрация - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="auth-page">
    <div class="auth-container">
        <div class="auth-box">
            <h1>Food Delivery</h1>
            <h2>Регистрация</h2>

            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <c:if test="${not empty fieldErrors}">
                <div class="profile-alert" style="background:#fff0f0; border:1px solid #ff4d4d; color:#a70000; padding:10px; border-radius:8px; margin-bottom:12px;">
                    <strong>Пожалуйста, исправьте поля:</strong>
                    <ul style="margin-top:6px;">
                        <c:forEach var="err" items="${fieldErrors}">
                            <li><c:out value="${err.value}"/></li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

            <form method="POST" action="${pageContext.request.contextPath}/client/register">
                <div class="form-group">
                    <label>Имя:</label>
                    <input type="text" name="name" value="${formData.name != null ? formData.name[0] : param.name}" required>
                    <c:if test="${fieldErrors['name'] != null}"><div class="field-error">${fieldErrors['name']}</div></c:if>
                </div>

                <div class="form-group">
                    <label>Email:</label>
                    <input type="email" name="email" value="${formData.email != null ? formData.email[0] : param.email}" required>
                    <c:if test="${fieldErrors['email'] != null}"><div class="field-error">${fieldErrors['email']}</div></c:if>
                </div>

                <div class="form-group">
                    <label>Телефон:</label>
                    <input type="tel" name="phone" placeholder="89XXXXXXXXX" pattern="89[0-9]{9}" value="${formData.phone != null ? formData.phone[0] : param.phone}" required>
                    <c:if test="${fieldErrors['phone'] != null}"><div class="field-error">${fieldErrors['phone']}</div></c:if>
                </div>

                <div class="form-group">
                    <label>Пароль:</label>
                    <input type="password" name="password" required
                           pattern="(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\-]).{8,}"
                           title="Минимум 8 символов, 1 цифра, 1 заглавная буква и 1 спецсимвол">
                </div>

                <div class="section" style="padding: 15px; margin: 20px 0 10px 0;">
                    <h3 style="margin-bottom: 10px;">Адрес</h3>
                    <div class="info-grid">
                        <div class="form-group">
                            <label>Страна:</label>
                            <input type="text" name="country" value="${param.country}" required>
                        </div>
                        <div class="form-group">
                            <label>Город:</label>
                            <input type="text" name="city" value="${param.city}" required>
                        </div>
                        <div class="form-group">
                            <label>Улица:</label>
                            <input type="text" name="street" value="${param.street}" required>
                        </div>
                        <div class="form-group">
                            <label>Здание:</label>
                            <input type="text" name="building" value="${param.building}" required>
                        </div>
                        <div class="form-group">
                            <label>Квартира:</label>
                            <input type="text" name="apartment" value="${param.apartment}">
                        </div>
                        <div class="form-group">
                            <label>Подъезд:</label>
                            <input type="text" name="entrance" value="${param.entrance}">
                        </div>
                        <div class="form-group">
                            <label>Этаж:</label>
                            <input type="text" name="floor" value="${param.floor}">
                        </div>
                        <div class="form-group" style="grid-column: 1 / -1;">
                            <label>Комментарий к адресу:</label>
                            <textarea name="addressNote" rows="2">${param.addressNote}</textarea>
                        </div>
                    </div>
                    <p style="margin-top: 10px; color: #666; font-size: 13px;">
                        После отправки формы вы получите код подтверждения на указанный телефон.
                    </p>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Зарегистрироваться</button>
            </form>

            <p>Уже есть аккаунт? <a href="${pageContext.request.contextPath}/client/login">Войти</a> — <a href="${pageContext.request.contextPath}/">На главную</a></p>
        </div>
    </div>
</body>
</html>
