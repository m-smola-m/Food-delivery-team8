<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Вход - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="auth-page">
    <nav class="navbar">
        <div class="container">
            <a href="${pageContext.request.contextPath}/" class="btn-back" aria-label="Назад на главную">← Назад</a>
        </div>
        <!-- Локальный логотип удалён: на страницах входа оставляем только кнопку назад -->
    </nav>

    <div class="auth-container">
        <div class="auth-box auth-box--narrow">
            <h2>Вход для клиента</h2>

            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <form method="POST" action="${pageContext.request.contextPath}/login">
                <input type="hidden" name="role" value="CLIENT"/>
                <div class="form-group">
                    <label>Логин (Email или Телефон)</label>
                    <input type="text" name="login" placeholder="example@gmail.com или 89XXXXXXXXX" required>
                </div>

                <div class="form-group">
                    <label>Пароль</label>
                    <input type="password" name="password" required>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Войти</button>
            </form>

            <div class="auth-links">
                <a href="${pageContext.request.contextPath}/client/forgot_password" class="btn btn-link">Забыл пароль?</a>
                <a href="${pageContext.request.contextPath}/client/register" class="btn btn-register">Зарегистрироваться</a>
            </div>

            <div class="demo-credentials">
                <strong>Тестовые аккаунты (данные из БД)</strong>
                <p class="demo-hint">Скрипт: ${clientTestDataSource}</p>
                <c:choose>
                    <c:when test="${not empty demoClients}">
                        <ul>
                            <c:forEach var="client" items="${demoClients}">
                                <li>
                                    <span class="label">Email</span>
                                    <span>${client.email} / ${client.passwordHash}</span>
                                </li>
                                <li>
                                    <span class="label">Телефон</span>
                                    <span>${client.phone} / ${client.passwordHash}</span>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <p>Нет данных. Заполните ${clientTestDataSource}.</p>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </div>
</body>
</html>
