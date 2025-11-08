package com.team8.fooddelivery.utils;

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
    // Телефон (формат 89XXXXXXXXX)
    // =====================
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^8\\d{10}$");
    }

    // =====================
    // Адрес (объект Address)
    // =====================
    public static boolean isValidAddress(Address address) {
        if (address == null) return false;
        try {
            if (address.getCountry() == null || address.getCountry().isBlank()) return false;
            if (address.getCity() == null || address.getCity().isBlank()) return false;
            if (address.getStreet() == null || address.getStreet().isBlank()) return false;
            if (address.getBuilding() == null) return false;
            if (address.getApartment() == null) return false;
            if (address.getEntrance() == null) return false;
            if (address.getFloor() == null) return false;
            if (address.getLatitude() == 0 || address.getLongitude() == 0) return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // =====================
    // Пароль (≥8 символов, 1 спецзнак, 1 цифра, 1 заглавная буква)
    // =====================
    public static boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$");
    }
}

