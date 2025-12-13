<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Панель курьера - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="container">
    <header>
        <h1>Панель курьера</h1>
        <p>Добро пожаловать, <span id="courierName">${sessionScope.courier.name}</span></p>
        <div>
            <a href="${pageContext.request.contextPath}/courier/history" class="btn btn-info">История заказов</a>
            <a href="${pageContext.request.contextPath}/courier/earnings" class="btn btn-success">Заработок</a>
            <button id="logoutBtn" class="btn btn-secondary">Выйти</button>
        </div>
    </header>

    <div class="status-section">
        <h2>Статус: <span id="status">offline</span></h2>
        <button id="startShiftBtn" class="btn btn-primary">НАЧАТЬ СМЕНУ</button>
        <button id="endShiftBtn" class="btn btn-primary" style="display:none;">ЗАВЕРШИТЬ СМЕНУ</button>
    </div>

    <div id="activeOrderSection" style="display:none;">
        <h2>Текущий заказ</h2>
        <p id="activeOrderDetails"></p>
        <button id="pickupOrderBtn" class="btn btn-success">ЗАКАЗ ЗАБРАЛ</button>
        <button id="deliverOrderBtn" class="btn btn-success">ЗАКАЗ ПЕРЕДАЛ</button>
        <button id="callClientBtn" class="btn btn-info">ПОЗВОНИТЬ КЛИЕНТУ</button>
        <button id="problemBtn" class="btn btn-warning">ПРОБЛЕМА С ЗАКАЗОМ</button>
    </div>

    <div id="availableOrdersSection">
        <h2>Доступные заказы</h2>
        <ul id="ordersList"></ul>
    </div>

    <div class="history-section">
        <h2>История заказов за сегодня</h2>
        <ul id="historyList">
        </ul>
    </div>

    <div class="earnings-section">
        <h2>Заработок за смену: <span id="earnings">0</span> руб.</h2>
        <button id="withdrawBtn" class="btn btn-warning">Вывести деньги</button>
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
let currentStatus = '${sessionScope.courier.status}';
let currentOrderId = ${sessionScope.courier.currentOrderId != null ? sessionScope.courier.currentOrderId : 0};

document.getElementById('status').textContent = currentStatus;

loadEarnings();
loadTodayHistory();

if (currentStatus === 'online') {
    document.getElementById('startShiftBtn').style.display = 'none';
    document.getElementById('endShiftBtn').style.display = 'inline';
    loadAvailableOrders();
} else if (currentStatus === 'on_delivery') {
    document.getElementById('startShiftBtn').style.display = 'none';
    document.getElementById('endShiftBtn').style.display = 'none';
    showActiveOrder();
}

document.getElementById('startShiftBtn').addEventListener('click', startShift);
document.getElementById('endShiftBtn').addEventListener('click', endShift);
document.getElementById('pickupOrderBtn').addEventListener('click', pickupOrder);
document.getElementById('deliverOrderBtn').addEventListener('click', deliverOrder);
document.getElementById('callClientBtn').addEventListener('click', callClient);
document.getElementById('problemBtn').addEventListener('click', reportProblem);
document.getElementById('logoutBtn').addEventListener('click', logout);
document.getElementById('withdrawBtn').addEventListener('click', () => {
    document.getElementById('withdrawModal').style.display = 'block';
    document.getElementById('modalBalance').textContent = document.getElementById('earnings').textContent;
});
document.querySelector('.close').addEventListener('click', () => {
    document.getElementById('withdrawModal').style.display = 'none';
});
document.getElementById('withdrawForm').addEventListener('submit', withdrawEarnings);

function startShift() {
    fetch('/courier/status', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ newStatus: 'online' })
    }).then(response => {
        if (response.ok) {
            currentStatus = 'online';
            document.getElementById('status').textContent = currentStatus;
            document.getElementById('startShiftBtn').style.display = 'none';
            document.getElementById('endShiftBtn').style.display = 'inline';
            loadAvailableOrders();
        }
    });
}

function endShift() {
    fetch('/courier/status', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ newStatus: 'offline' })
    }).then(response => {
        if (response.ok) {
            currentStatus = 'offline';
            document.getElementById('status').textContent = currentStatus;
            document.getElementById('startShiftBtn').style.display = 'inline';
            document.getElementById('endShiftBtn').style.display = 'none';
            document.getElementById('availableOrdersSection').style.display = 'none';
        }
    });
}

function loadAvailableOrders() {
    fetch('/courier/orders')
    .then(response => response.json())
    .then(orders => {
        const list = document.getElementById('ordersList');
        list.innerHTML = '';
        orders.forEach(order => {
            const li = document.createElement('li');
            li.textContent = `Заказ #${order.id}: ${order.restaurantAddress.street} ${order.restaurantAddress.building} -> ${order.deliveryAddress.street} ${order.deliveryAddress.building}`;
            const btn = document.createElement('button');
            btn.textContent = 'ПРИНЯТЬ ЗАКАЗ';
            btn.className = 'btn btn-primary';
            btn.addEventListener('click', () => acceptOrder(order.id));
            li.appendChild(btn);
            list.appendChild(li);
        });
    });
}

function acceptOrder(orderId) {
    // Assuming POST to /courier/accept or something, but since not, maybe update status
    // For now, set status to on_delivery, currentOrderId = orderId
    fetch('/courier/status', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ newStatus: 'on_delivery', currentOrderId: orderId })
    }).then(response => {
        if (response.ok) {
            currentStatus = 'on_delivery';
            currentOrderId = orderId;
            document.getElementById('status').textContent = currentStatus;
            document.getElementById('endShiftBtn').style.display = 'none';
            document.getElementById('availableOrdersSection').style.display = 'none';
            showActiveOrder();
        }
    });
}

function showActiveOrder() {
    fetch('/courier/active-order')
    .then(response => response.json())
    .then(order => {
        if (order) {
            document.getElementById('activeOrderDetails').textContent = `Заказ #${order.id}: ${order.restaurantAddress ? order.restaurantAddress.street + ' ' + order.restaurantAddress.building : 'Адрес ресторана'} -> ${order.deliveryAddress ? order.deliveryAddress.street + ' ' + order.deliveryAddress.building : 'Адрес доставки'}`;
            document.getElementById('activeOrderSection').style.display = 'block';
        }
    });
}

function pickupOrder() {
    fetch('/courier/pickup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ orderId: currentOrderId })
    }).then(response => {
        if (response.ok) {
            // Update lastAddress to from
        }
    });
}

function deliverOrder() {
    fetch('/courier/complete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ orderId: currentOrderId })
    }).then(response => {
        if (response.ok) {
            currentStatus = 'online';
            currentOrderId = 0;
            document.getElementById('status').textContent = currentStatus;
            document.getElementById('activeOrderSection').style.display = 'none';
            document.getElementById('endShiftBtn').style.display = 'inline';
            loadAvailableOrders();
            loadTodayHistory();
        }
    });
}

function callClient() {
    // Mock call
    alert('Звонок клиенту');
}

function reportProblem() {
    // Mock
    alert('Связь с менеджером');
}

function logout() {
    window.location.href = '/logout';
}

function loadEarnings() {
    fetch('/courier/earnings')
    .then(response => response.json())
    .then(data => {
        document.getElementById('earnings').textContent = data.earnings + ' руб.';
    });
}

function loadTodayHistory() {
    fetch('/courier/today-history')
    .then(response => response.json())
    .then(orders => {
        const list = document.getElementById('historyList');
        list.innerHTML = '';
        orders.forEach(order => {
            const li = document.createElement('li');
            li.innerHTML = `
                <div class="order-card">
                    <h4>Заказ #${order.id}</h4>
                    <p>Адрес: ${order.deliveryAddress.street} ${order.deliveryAddress.building}</p>
                    <p>Время: ${new Date(order.updatedAt).toLocaleString()}</p>
                </div>
            `;
            list.appendChild(li);
        });
    });
}

function withdrawEarnings(event) {
    event.preventDefault();
    const amount = document.getElementById('withdrawAmount').value;
    // Send POST to /courier/withdraw
    fetch('/courier/withdraw', {
        method: 'POST'
    }).then(response => {
        if (response.ok) {
            alert(`Вывод денег выполнен: ${amount} руб.`);
            document.getElementById('withdrawModal').style.display = 'none';
            document.getElementById('withdrawForm').reset();
            loadEarnings(); // Reload earnings
        } else {
            alert('Ошибка при выводе денег');
        }
    });
}
</script>
</body>
</html>