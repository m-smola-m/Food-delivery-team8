package com.team8.fooddelivery.model.order;

public enum OrderStatus {
    NEW,             // Только создан
    PICK_AND_PACKING,// Собирается магазинами
    SHIPPED,         // Передан в доставку
    DELIVERED,       // Доставлен клиенту
    CANCELLED        // Отменён
}
