package com.team8.fooddelivery.model.client;

public enum PaymentMethodForOrder {
    CASH,
    CARD,
    ONLINE;

    public static PaymentMethodForOrder fromDbValue(String value) {
        if (value == null || value.isBlank()) {
            return CASH;
        }
        try {
            return PaymentMethodForOrder.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return CASH;
        }
    }
}
