package com.team8.fooddelivery.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Класс для инициализации структуры базы данных
 */
public class DatabaseInitializerService {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializerService.class);

  // Порядок выполнения SQL файлов
  private static final List<String> SQL_FILES = Arrays.asList(
      // Сначала создаем все таблицы без внешних ключей
      "sql/001_create_base_tables/001_create_addresses.sql",
      "sql/001_create_base_tables/002_create_working_hours.sql",
      "sql/001_create_base_tables/003_create_clients.sql",
      "sql/002_create_shop_tables/004_create_shops.sql",
      "sql/002_create_shop_tables/005_create_products.sql",
      "sql/003_create_courier_tables/007_create_courier_tables.sql",
      "sql/004_create_order_tables/008_create_orders.sql",
      "sql/004_create_order_tables/009_create_order_items.sql",
      "sql/004_create_order_tables/010_create_carts.sql",
      "sql/004_create_order_tables/011_create_cart_items.sql",
      // Потом добавляем внешние ключи
      "sql/002_create_shop_tables/006_add_shop_foreign_keys.sql",
      "sql/004_create_order_tables/012_add_cart_foreign_keys.sql",
      "sql/004_create_order_tables/013_create_payments.sql",
      "sql/005_create_indexes/013_create_indexes.sql"
  );

  /**
   * Инициализирует всю структуру базы данных
   */
  public static void initializeDatabase() {
    if (isDatabaseInitialized()) {
      logger.info("База данных уже инициализирована, повторная загрузка схемы пропущена");
      return;
    }

    logger.info("Начинается инициализация базы данных...");

    try (Connection conn = DatabaseConnectionService.getConnection()) {
      conn.setAutoCommit(false); // Начинаем транзакцию

      try {
        // Выполняем все SQL файлы в правильном порядке
        for (String sqlFile : SQL_FILES) {
          executeSqlFile(conn, sqlFile);
        }

        conn.commit(); // Коммитим транзакцию
        logger.info("✅ База данных успешно инициализирована");

      } catch (SQLException e) {
        conn.rollback(); // Откатываем при ошибке
        logger.error("❌ Ошибка инициализации базы данных, транзакция откачена", e);
        throw new RuntimeException("Не удалось инициализировать базу данных", e);
      } finally {
        conn.setAutoCommit(true); // Восстанавливаем auto-commit
      }

    } catch (SQLException e) {
      logger.error("❌ Ошибка подключения при инициализации базы данных", e);
      throw new RuntimeException("Не удалось подключиться к базе данных", e);
    }
  }

  /**
   * Выполняет SQL команды с улучшенной обработкой ошибок
   */
  private static void executeSqlStatements(Connection conn, String sql) throws SQLException {
    // Разделяем SQL на отдельные команды, игнорируя точки с запятой внутри кавычек
    String[] statements = sql.split(";(?=(?:[^']*'[^']*')*[^']*$)");

    for (String statement : statements) {
      String trimmed = statement.trim();

      if (isExecutableStatement(trimmed)) {
        try (Statement stmt = conn.createStatement()) {
          stmt.execute(trimmed);
          if (logger.isDebugEnabled()) {
            logger.debug("Выполнено: {}", getStatementPreview(trimmed));
          }
        } catch (SQLException e) {
          handleSqlException(trimmed, e);
        }
      }
    }
  }

  private static boolean isExecutableStatement(String statement) {
    return !statement.isEmpty() &&
        !statement.startsWith("--") &&
        !statement.startsWith("/*") &&
        !statement.startsWith("//");
  }

  private static String getStatementPreview(String statement) {
    return statement.substring(0, Math.min(80, statement.length())) +
        (statement.length() > 80 ? "..." : "");
  }

  private static void handleSqlException(String statement, SQLException e) throws SQLException {
    String errorMessage = e.getMessage().toLowerCase();

    // Игнорируем ошибки "уже существует"
    if (errorMessage.contains("already exists") ||
        errorMessage.contains("существует") ||
        errorMessage.contains("exists") ||
        errorMessage.contains("duplicate")) {
      logger.debug("Объект уже существует: {}", e.getMessage());
    } else {
      logger.error("❌ Критическая ошибка выполнения SQL: {}", e.getMessage());
      logger.error("Проблемный SQL: {}", getStatementPreview(statement));
      throw e;
    }
  }

  private static void executeSqlFile(Connection conn, String filePath) throws SQLException {
    logger.info("Выполнение SQL файла: {}", filePath);

    try (InputStream inputStream = DatabaseInitializerService.class.getClassLoader()
        .getResourceAsStream(filePath)) {

      if (inputStream == null) {
        logger.error("Файл не найден в classpath: {}", filePath);
        throw new SQLException("SQL файл не найден: " + filePath);
      }

      String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      executeSqlStatements(conn, sql);

      logger.info("✅ Файл выполнен: {}", filePath);

    } catch (Exception e) {
      logger.error("❌ Ошибка выполнения файла: {}", filePath, e);
      throw new SQLException("Ошибка выполнения SQL файла: " + filePath, e);
    }
  }

  public static boolean isDatabaseInitialized() {
    // Проверяем наличие ключевых таблиц
    String[] testTables = {"addresses", "clients", "shops", "orders", "products"};

    try (Connection conn = DatabaseConnectionService.getConnection();
         Statement stmt = conn.createStatement()) {

      for (String table : testTables) {
        stmt.executeQuery("SELECT 1 FROM " + table + " LIMIT 1");
      }
      return true;

    } catch (SQLException e) {
      logger.debug("База данных не инициализирована: {}", e.getMessage());
      return false;
    }
  }

  public static void fullCleanDatabase() {
    logger.info("Полная очистка базы данных...");

    try (Connection conn = DatabaseConnectionService.getConnection()) {
      conn.setAutoCommit(false);

      try (Statement stmt = conn.createStatement()) {
        // Отключаем проверку внешних ключей для безопасного удаления
        stmt.execute("SET session_replication_role = 'replica'");

        // Удаляем таблицы в правильном порядке (от дочерних к родительским)
        String[] tables = {
            "cart_items", "carts", "order_items", "payments", "orders",
            "products", "couriers", "shops", "clients",
            "working_hours", "addresses"
        };

        int droppedTables = 0;
        for (String table : tables) {
          try {
            stmt.execute("DROP TABLE IF EXISTS " + table + " CASCADE");
            droppedTables++;
            logger.debug("Таблица {} удалена", table);
          } catch (SQLException e) {
            logger.warn("Не удалось удалить таблицу {}: {}", table, e.getMessage());
          }
        }

        // Включаем проверку внешних ключей обратно
        stmt.execute("SET session_replication_role = 'origin'");
        conn.commit();

        logger.info("✅ Удалено таблиц: {}/{}", droppedTables, tables.length);

      } catch (SQLException e) {
        conn.rollback();
        throw e;
      } finally {
        conn.setAutoCommit(true);
      }

    } catch (SQLException e) {
      logger.error("❌ Ошибка полной очистки базы данных: {}", e.getMessage());
      throw new RuntimeException("Не удалось очистить базу данных", e);
    }
  }

  public static void cleanTestData() {
    logger.info("Очистка тестовых данных...");

    try (Connection conn = DatabaseConnectionService.getConnection();
         Statement stmt = conn.createStatement()) {

      // Очищаем таблицы в правильном порядке (из-за foreign keys)
      String[] tablesToClean = {
          "cart_items", "carts", "order_items", "payments", "orders",
          "products", "couriers", "shops", "clients", "addresses"
      };

      int cleanedTables = 0;
      for (String table : tablesToClean) {
        try {
          int rows = stmt.executeUpdate("DELETE FROM " + table);
          if (rows > 0) {
            logger.debug("Очищено {} строк из {}", rows, table);
          }
          cleanedTables++;
        } catch (SQLException e) {
          logger.warn("Не удалось очистить таблицу {}: {}", table, e.getMessage());
        }
      }

      logger.info("✅ Очищено таблиц: {}/{}", cleanedTables, tablesToClean.length);

    } catch (SQLException e) {
      logger.error("❌ Ошибка очистки тестовых данных: {}", e.getMessage());
    }
  }
}