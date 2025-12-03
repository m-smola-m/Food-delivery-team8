package com.team8.fooddelivery.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

  private static final String BCRYPT_PREFIX = "$2";

  // Хеширование пароля
  public static String hashPassword(String plainPassword) {
    return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
  }

  // Проверка пароля
  public static boolean checkPassword(String plainPassword, String hashedPassword) {
    if (plainPassword == null || hashedPassword == null) {
      return false;
    }
    if (hashedPassword.startsWith(BCRYPT_PREFIX)) {
      return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    return hashedPassword.equals(plainPassword);
  }
}
