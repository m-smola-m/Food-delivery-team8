<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Заработок - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="container">
    <header>
        <h1>Заработок</h1>
        <p>Курьер: <span id="courierName">${sessionScope.courier.name}</span></p>
        <a href="${pageContext.request.contextPath}/courier/dashboard" class="btn btn-secondary">Назад к панели</a>
    </header>

    <div class="earnings-summary">
        <div class="info-grid">
            <div>
                <h2>Общий заработок</h2>
                <p id="totalEarnings">0 руб.</p>
            </div>
            <div>
                <h2>Заработок за сегодня</h2>
                <p id="todayEarnings">0 руб.</p>
            </div>
            <div>
                <h2>Текущий баланс</h2>
                <p id="currentBalance">${sessionScope.courier.currentBalance} руб.</p>
            </div>
        </div>
        <button id="withdrawBtn" class="btn btn-warning" style="margin-top: 20px;">Вывести деньги</button>
    </div>

    <div class="earnings-details">
        <h2>Детали заработка</h2>
        <table class="earnings-table">
            <thead>
                <tr>
                    <th>Дата</th>
                    <th>Заказ</th>
                    <th>Сумма</th>
                </tr>
            </thead>
            <tbody id="earningsTableBody">
            </tbody>
        </table>
    </div>

    <!-- Modal for withdrawal -->
    <div id="withdrawModal" class="modal" style="display:none;">
        <div class="modal-content">
            <span class="close">&times;</span>
            <h2>Вывод средств</h2>
            <p>Текущий баланс: <span id="modalBalance">0</span> руб.</p>
            <form id="withdrawForm">
                <label for="withdrawAmount">Сумма для вывода:</label>
                <input type="number" id="withdrawAmount" name="amount" min="1" required>
                <button type="submit" class="btn btn-success">Подтвердить вывод</button>
            </form>
        </div>
    </div>
</div>

<script>
let courierId = ${sessionScope.courier.id};

loadEarningsDetails();

document.getElementById('withdrawBtn').addEventListener('click', () => {
    document.getElementById('withdrawModal').style.display = 'block';
    document.getElementById('modalBalance').textContent = document.getElementById('currentBalance').textContent;
});
document.querySelector('.close').addEventListener('click', () => {
    document.getElementById('withdrawModal').style.display = 'none';
});
document.getElementById('withdrawForm').addEventListener('submit', withdrawEarnings);

function loadEarningsDetails() {
    fetch('/courier/today-history')
    .then(response => response.json())
    .then(orders => {
        const tbody = document.getElementById('earningsTableBody');
        tbody.innerHTML = '';
        let total = 0;
        orders.forEach(order => {
            const tr = document.createElement('tr');
            const date = new Date(order.updatedAt).toLocaleDateString();
            const amount = 100; // Mock
            total += amount;
            tr.innerHTML = `
                <td>${date}</td>
                <td>Заказ #${order.id}</td>
                <td>${amount} руб.</td>
            `;
            tbody.appendChild(tr);
        });
        document.getElementById('todayEarnings').textContent = total + ' руб.';
        document.getElementById('totalEarnings').textContent = total + ' руб.'; // For simplicity
    });
}

function withdrawEarnings(event) {
    event.preventDefault();
    const amount = document.getElementById('withdrawAmount').value;
    fetch('/courier/withdraw', {
        method: 'POST'
    }).then(response => {
        if (response.ok) {
            alert(`Вывод денег выполнен: ${amount} руб.`);
            document.getElementById('withdrawModal').style.display = 'none';
            document.getElementById('withdrawForm').reset();
            location.reload(); // Reload to update balance
        } else {
            alert('Ошибка при выводе денег');
        }
    });
}
</script>
</body>
</html>
