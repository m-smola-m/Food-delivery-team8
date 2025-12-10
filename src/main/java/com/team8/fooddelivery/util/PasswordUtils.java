package com.team8.fooddelivery.util;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordUtils {

  private static final Logger logger = LoggerFactory.getLogger(PasswordUtils.class);
  private static final String BCRYPT_PREFIX = "$2";

  // Хеширование пароля
  public static String hashPassword(String plainPassword) {
    if (plainPassword == null || plainPassword.trim().isEmpty()) {
      throw new IllegalArgumentException("Пароль не может быть пустым");
    }
    return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
  }

  // Проверка пароля
  public static boolean checkPassword(String plainPassword, String hashedPassword) {
    if (plainPassword == null || hashedPassword == null) {
      return false;
    }
    
    // Если пароль начинается с $2, пытаемся проверить через BCrypt
    if (hashedPassword.startsWith(BCRYPT_PREFIX)) {
      try {
        return BCrypt.checkpw(plainPassword, hashedPassword);
      } catch (Exception e) {
        // Если возникает ошибка "Invalid salt version" или другая ошибка BCrypt,
        // логируем и пробуем сравнить как plain text (для обратной совместимости)
        logger.warn("Ошибка при проверке BCrypt пароля: {}. Пробуем сравнить как plain text.", e.getMessage());
        return hashedPassword.equals(plainPassword);
      }
    }
    
    // Если пароль не в формате BCrypt, сравниваем как plain text
    return hashedPassword.equals(plainPassword);
  }
}
