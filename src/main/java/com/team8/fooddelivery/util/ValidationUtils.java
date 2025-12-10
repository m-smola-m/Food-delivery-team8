package com.team8.fooddelivery.util;

import com.team8.fooddelivery.model.Address;

public class ValidationUtils {

    // =====================
    // Email
    // =====================
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return email.matches("^[\\p{L}0-9._%+-]+@[\\p{L}0-9.-]+\\.[\\p{L}]{2,}$");
    }

    // =====================
    // Телефон (формат 8[+7]9XXXXXXXXX)
    // =====================
    public static boolean isValidPhone(String phone) {return phone != null && phone.matches("^(8|\\+7)\\d{10}$");}
    // =====================
    // Адрес (объект Address)
    // =====================
    public static java.util.Map<String, String> validateAddress(Address address) {
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        if (address == null) {
            errors.put("address", "Адрес обязателен");
            return errors;
        }
        if (address.getCountry() == null || address.getCountry().isBlank()) {
            errors.put("country", "Страна обязательна");
        }
        if (address.getCity() == null || address.getCity().isBlank()) {
            errors.put("city", "Город обязателен");
        }
        if (address.getStreet() == null || address.getStreet().isBlank()) {
            errors.put("street", "Улица обязательна");
        }
        if (address.getBuilding() == null || address.getBuilding().isBlank()) {
            errors.put("building", "Номер здания обязателен");
        } else {
            String b = address.getBuilding().trim();
            if (b.length() > 10) {
                errors.put("building", "Номер здания слишком длинный (макс. 10 символов)");
            } else {
                // Разрешаем буквы, цифры, дефис и слеш (например: 12A, 12/1, A-3)
                if (!b.matches("^[A-Za-zА-Яа-яЁё0-9\\-\\/]+$")) {
                    errors.put("building", "Неверный формат номера здания (только буквы, цифры, '-' и '/')");
                } else {
                    // если полностью числовой, то не допускаем отрицательные и нулевые значения
                    try {
                        int bi = Integer.parseInt(b);
                        if (bi <= 0) {
                            errors.put("building", "Номер здания должен быть положительным числом");
                        }
                    } catch (NumberFormatException nfe) {
                        // содержит буквы — допустимо
                    }
                }
            }
        }
        // apartment and entrance optional
        if (address.getFloor() != null) {
            int f = address.getFloor();
            // parseInt may return Integer.MIN_VALUE as a marker of invalid (non-numeric) input
            if (f == Integer.MIN_VALUE) {
                errors.put("floor", "Этаж должен быть целым числом");
            } else {
                // допустимые этажи: от -2 (подвальные) до 100
                if (f < -2 || f > 100) {
                    errors.put("floor", "Этаж должен быть целым числом в диапазоне -2..100");
                }
            }
        }
        // latitude/longitude optional - if present, validate numeric range
        if (address.getLatitude() != null) {
            double lat = address.getLatitude();
            if (lat < -90 || lat > 90) errors.put("latitude", "Широта вне диапазона (-90..90)");
        }
        if (address.getLongitude() != null) {
            double lon = address.getLongitude();
            if (lon < -180 || lon > 180) errors.put("longitude", "Долгота вне диапазона (-180..180)");
        }
        return errors;
    }

    // keep existing isValidAddress for compatibility
    public static boolean isValidAddress(Address address) {
        return validateAddress(address).isEmpty();
    }

    // =====================
    // Пароль (≥8 символов, 1 спецзнак, 1 цифра, 1 заглавная буква)
    // =====================
    public static boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$");
    }
}
