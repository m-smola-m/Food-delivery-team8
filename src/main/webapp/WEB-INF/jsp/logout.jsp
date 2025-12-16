<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Выход - Food Delivery</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        .logout-container {
            max-width: 500px;
            padding: 40px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
            text-align: center;
        }
        .logout-icon {
            font-size: 64px;
            color: #28a745;
            margin-bottom: 20px;
        }
        .logout-title {
            font-size: 28px;
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
        }
        .logout-message {
            font-size: 16px;
            color: #666;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        .logout-actions {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }
        .logout-actions a {
            padding: 12px 30px;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s;
        }
        .btn-login {
            background: #007bff;
            color: white;
        }
        .btn-login:hover {
            background: #0056b3;
        }
        .btn-home {
            background: #6c757d;
            color: white;
        }
        .btn-home:hover {
            background: #545b62;
        }
        .logout-actions a { color: #fff !important; }
    </style>
</head>
<body>
    <div class="logout-container">
        <div class="logout-icon">🍽️</div>
        <h1 class="logout-title">Welcome to Food Delivery</h1>
        <p class="logout-message">
            Нажмите кнопку ниже, чтобы перейти на страницу входа и продолжить.
        </p>

        <div class="logout-actions">
            <a href="<%= request.getContextPath() %>/client/login" class="btn-login">
                Войти
            </a>
            <a href="<%= request.getContextPath() %>/client/register" class="btn-home">
                Зарегистрироваться
            </a>
        </div>
    </div>
</body>
</html>
