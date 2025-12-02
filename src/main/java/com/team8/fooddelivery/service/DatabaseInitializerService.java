package com.team8.fooddelivery.service;

import com.team8.fooddelivery.util.SchemaBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
      "sql/004_create_order_tables/017_create_notifications.sql",
      "sql/005_create_indexes/013_create_indexes.sql"
  );

  private static final List<String> TEST_DATA_FILES = Arrays.asList(
      "sql/test_data/000_insert_addresses.sql",
      "sql/test_data/001_insert_working_hours.sql",
      "sql/test_data/002_insert_clients.sql",
      "sql/test_data/003_insert_shops.sql",
      "sql/test_data/004_insert_products.sql",
      "sql/test_data/005_insert_couriers.sql",
      "sql/test_data/006_insert_orders.sql",
      "sql/test_data/007_insert_order_items.sql",
      "sql/test_data/008_insert_carts.sql",
      "sql/test_data/009_insert_cart_items.sql",
      "sql/test_data/010_insert_payments.sql",
      "sql/test_data/011_insert_notifications.sql"
  );

  /**
   * Инициализирует всю структуру базы данных
   */
  public static void initializeDatabase() {
    if (isDatabaseInitialized()) {
      logger.info("База данных уже инициализирована, повторная загрузка схемы пропущена");
      return;
    }
    runSqlFiles(SQL_FILES);
    ensureOrderColumns();
  }

  /**
   * Полный цикл для user story: очистить БД, пересоздать схему и залить тестовые данные
   */
  public static void resetDatabaseWithTestData() {
    logger.info("Полный сброс БД перед user story...");
    tryFullCleanDatabase();
    runSqlFiles(SQL_FILES);
    ensureOrderColumns();
    loadTestData();
  }

  /**
   * Загружает тестовые данные в базу данных
   */
  public static void loadTestData() {
    logger.info("Загрузка тестовых данных из sql/test_data...");
    try {
      runSqlFiles(TEST_DATA_FILES);
      ensureOrderColumns();
    } catch (Exception e) {
      logger.warn("⚠️ Не удалось загрузить некоторые тестовые данные. Приложение все равно запустится, но без тестовых данных", e);
      // Не выбрасываем исключение, позволяем приложению работать без тестовых данных
    }
  }

  /**
   * Выполняет SQL команды с улучшенной обработкой ошибок
   */
  private static void executeSqlStatements(Connection conn, String sql) throws SQLException {
    // Удаляем комментарии перед выполнением
    String cleanedSql = removeComments(sql);

    // Разделяем SQL на отдельные команды, игнорируя точки с запятой внутри кавычек
    String[] statements = cleanedSql.split(";(?=(?:[^']*'[^']*')*[^']*$)");

    for (String statement : statements) {
      String trimmed = statement.trim();

      if (isExecutableStatement(trimmed)) {
        try (Statement stmt = conn.createStatement()) {
          stmt.execute(trimmed);
          if (logger.isDebugEnabled()) {
            logger.debug("Выполнено: {}", getStatementPreview(trimmed));
          }
        } catch (SQLException e) {
          // Логируем ошибку, но продолжаем выполнение остальных команд
          String errorMsg = e.getMessage().toLowerCase();
          if (errorMsg.contains("already exists") || errorMsg.contains("существует") ||
              errorMsg.contains("duplicate key") || errorMsg.contains("уникальн")) {
            logger.debug("⚠️ Объект уже существует, пропускаем: {}", getStatementPreview(trimmed));
          } else {
            logger.warn("⚠️ Ошибка выполнения SQL команды (пропускаем): {} | Ошибка: {}",
                       getStatementPreview(trimmed), e.getMessage());
          }
        }
      }
    }
  }

  /**
   * Удаляет комментарии из SQL текста
   */
  private static String removeComments(String sql) {
    StringBuilder result = new StringBuilder();
    String[] lines = sql.split("\n");

    for (String line : lines) {
      // Удаляем однострочные комментарии
      int commentIndex = line.indexOf("--");
      if (commentIndex >= 0) {
        line = line.substring(0, commentIndex);
      }

      // Пропускаем пустые строки
      String trimmed = line.trim();
      if (!trimmed.isEmpty()) {
        result.append(trimmed).append(" ");
      }
    }

    return result.toString();
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

  private static void runSqlFiles(List<String> files) {
    for (String file : files) {
      try (Connection conn = DatabaseConnectionService.getConnection()) {
        conn.setAutoCommit(true);
        executeSqlFile(conn, file);
      } catch (SQLException e) {
        logger.warn("⚠️ Ошибка при выполнении файла {}, продолжаем со следующего: {}", file, e.getMessage());
        // Продолжаем выполнение остальных файлов
      }
    }
  }

  private static void ensureOrderColumns() {
    try (Connection conn = DatabaseConnectionService.getConnection()) {
      SchemaBootstrap.ensureOrderColumns(conn);
    } catch (SQLException e) {
      logger.warn("Не удалось проверить структуру таблицы orders", e);
    }
  }

  public static boolean isDatabaseInitialized() {
    // Проверяем наличие ключевых таблиц
    String[] testTables = {"addresses", "clients", "shops", "orders", "products", "notifications"};

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

      Statement stmt = conn.createStatement();
      try {
        boolean fkDisabled = false;
        try {
          stmt.execute("SET session_replication_role = 'replica'");
          fkDisabled = true;
        } catch (SQLException e) {
          logger.warn("Не удалось отключить проверку внешних ключей: {}", e.getMessage());
          conn.rollback();
          conn.setAutoCommit(false);
          stmt.close();
          stmt = conn.createStatement();
        }

        // Удаляем таблицы в правильном порядке (от дочерних к родительским)
        String[] tables = {
            "notifications",
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
            conn.rollback();
            conn.setAutoCommit(false);
            stmt.close();
            stmt = conn.createStatement();
          }
        }

        if (fkDisabled) {
          try {
            stmt.execute("SET session_replication_role = 'origin'");
          } catch (SQLException e) {
            logger.warn("Не удалось вернуть session_replication_role: {}", e.getMessage());
            conn.rollback();
            conn.setAutoCommit(false);
            stmt.close();
            stmt = conn.createStatement();
          }
        }
        conn.commit();

        logger.info("✅ Удалено таблиц: {}/{}", droppedTables, tables.length);

      } catch (SQLException e) {
        conn.rollback();
        throw e;
      } finally {
        stmt.close();
        conn.setAutoCommit(true);
      }

    } catch (SQLException e) {
      logger.error("❌ Ошибка полной очистки базы данных: {}", e.getMessage());
      throw new RuntimeException("Не удалось очистить базу данных", e);
    }
  }

  private static void tryFullCleanDatabase() {
    try {
      fullCleanDatabase();
    } catch (RuntimeException cleanupError) {
      logger.warn("Не удалось выполнить полную очистку, пробуем мягкую очистку", cleanupError);
      cleanTestData();
    }
  }

  public static void cleanTestData() {
    logger.info("Очистка тестовых данных...");

    try (Connection conn = DatabaseConnectionService.getConnection();
         Statement stmt = conn.createStatement()) {

      // Очищаем таблицы в правильном порядке (из-за foreign keys)
      String[] tablesToClean = {
          "notifications",
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
