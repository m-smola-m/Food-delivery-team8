package com.team8.fooddelivery.model.payment;

public enum PaymentStatus {
    PENDING,   // Ожидает оплаты
    PAID,      // Оплачен онлайн
    CONFIRMED, // Оплата при получении подтверждена
    FAILED     // Не удалось оплатить
}
