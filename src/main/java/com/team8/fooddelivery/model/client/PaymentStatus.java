package com.team8.fooddelivery.model.client;

public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    PAID;

    public static PaymentStatus fromDbValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return PaymentStatus.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return switch (value.trim().toUpperCase()) {
                case "PAID", "SUCCESSFUL", "SUCCESS" -> SUCCESS;
                case "FAILED", "CANCELLED", "CANCELED", "DECLINED" -> FAILED;
                default -> PENDING;
            };
        }
    }
}
