package com.team8.fooddelivery.util;

public class ValidationUtils {
  public static boolean isValidEmail(String email) {
    return email != null && email.matches("^[A-Za-z]+[A-Za-z._]+@(gmail\\.com|mail\\.ru|yandex\\.ru)$");
  }

  public static boolean isValidPhone(String phone) {
    return phone != null && phone.matches("^(\\+79|89|79)[0-9]{9}$");
  }

  public static boolean isValidPassword(String password) {
    return password != null && password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$");
  }
}
