package com.team8.fooddelivery.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Утилитный класс для управления подключениями к PostgreSQL базе данных
 */
public class DatabaseConnectionService {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionService.class);

  private static final String DEFAULT_URL_CONTAINER = "jdbc:postgresql://db:5432/food_delivery";
  private static final String DEFAULT_URL_LOCAL = "jdbc:postgresql://localhost:5432/food_delivery";
  private static final String DEFAULT_USER = "postgres";
  private static final String DEFAULT_PASSWORD = "postgres";

  private static String dbUrl = resolveDatabaseUrl();
  private static String dbUser = resolve("db.user", "DB_USER", DEFAULT_USER);
  private static String dbPassword = resolve("db.password", "DB_PASSWORD", DEFAULT_PASSWORD);

  static {
    try {
      Class.forName("org.postgresql.Driver");
      logger.info("PostgreSQL JDBC Driver загружен успешно");
    } catch (ClassNotFoundException e) {
      logger.error("Не удалось загрузить PostgreSQL JDBC Driver", e);
      throw new RuntimeException("PostgreSQL JDBC Driver не найден", e);
    }
  }

  public static Connection getConnection() throws SQLException {
    try {
      Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
      if (logger.isDebugEnabled()) {
        logger.debug("Подключение к БД установлено: {}", getSafeConnectionString());
      }
      return connection;
    } catch (SQLException e) {
      logger.error("Ошибка подключения к БД: {}", getSafeConnectionString(), e);
      throw e;
    }
  }

  public static void setConnectionParams(String url, String user, String password) {
    dbUrl = url;
    dbUser = user;
    dbPassword = password;
    logger.info("Параметры подключения обновлены: url={}, user={}", url, user);
  }

  private static String resolveDatabaseUrl() {
    // Сначала проверяем системные свойства и переменные окружения
    String fromSystem = System.getProperty("db.url");
    if (fromSystem != null && !fromSystem.isBlank()) {
      logger.info("Используем URL БД из системного свойства db.url");
      return fromSystem;
    }

    String fromEnv = System.getenv("DB_URL");
    if (fromEnv != null && !fromEnv.isBlank()) {
      logger.info("Используем URL БД из переменной окружения DB_URL");
      return fromEnv;
    }

    // Автоматическое определение: контейнерная среда или локальная
    boolean isContainerEnv = isContainerEnvironment();
    String url = isContainerEnv ? DEFAULT_URL_CONTAINER : DEFAULT_URL_LOCAL;
    logger.info("Автоопределение среды: {} -> URL: {}",
        isContainerEnv ? "контейнер" : "локальная", url);
    return url;
  }

  private static boolean isContainerEnvironment() {
    // Проверяем, работаем ли мы в контейнерной среде
    return System.getenv("CONTAINER_ENV") != null ||
        System.getenv("KUBERNETES_SERVICE_HOST") != null ||
        System.getenv("DOCKER_CONTAINER") != null ||
        "true".equals(System.getProperty("container.env"));
  }

  private static String resolve(String sysPropKey, String envKey, String defaultValue) {
    String fromSystem = System.getProperty(sysPropKey);
    if (fromSystem != null && !fromSystem.isBlank()) {
      logger.info("Используем параметр из системного свойства {}", sysPropKey);
      return fromSystem;
    }

    String fromEnv = System.getenv(envKey);
    if (fromEnv != null && !fromEnv.isBlank()) {
      logger.info("Используем параметр из переменной окружения {}", envKey);
      return fromEnv;
    }

    logger.info("Используем параметр по умолчанию для {}", sysPropKey);
    return defaultValue;
  }

  public static void initializeDatabase() {
    if (!DatabaseInitializerService.isDatabaseInitialized()) {
      DatabaseInitializerService.initializeDatabase();
    } else {
      logger.info("База данных уже инициализирована");
    }
  }

  public static boolean testConnection() {
    try (Connection conn = getConnection()) {
      boolean isValid = conn != null && !conn.isClosed() && conn.isValid(2);
      logger.info("Тест подключения: {}", isValid ? "УСПЕХ" : "НЕУДАЧА");
      return isValid;
    } catch (SQLException e) {
      logger.error("Тест подключения не пройден: {}", e.getMessage());
      return false;
    }
  }

  public static void closeConnection(Connection connection) {
    if (connection != null) {
      try {
        if (!connection.isClosed()) {
          connection.close();
          logger.debug("Подключение закрыто");
        }
      } catch (SQLException e) {
        logger.error("Ошибка при закрытии подключения", e);
      }
    }
  }

  private static String getSafeConnectionString() {
    // Безопасное логирование без пароля
    return dbUrl.replaceAll("://[^@]+@", "://***@");
  }

  // Метод для получения информации о подключении (для отладки)
  public static String getConnectionInfo() {
    return String.format("URL: %s, User: %s", getSafeConnectionString(), dbUser);
  }
}