package com.team8.fooddelivery.util;

import com.team8.fooddelivery.service.DatabaseConnectionService;
import com.team8.fooddelivery.service.DatabaseInitializerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@DisplayName("Простая проверка подключения к БД")
public class SimpleConnectionTest {

    @Test
    @DisplayName("Проверка подключения к PostgreSQL")
    void testDatabaseConnectionService() throws SQLException {
        // Настройка параметров подключения
        String dbUrl = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/food_delivery");
        String dbUser = System.getProperty("db.user", "postgres");
        String dbPassword = System.getProperty("db.password", "postgres");

        DatabaseConnectionService.setConnectionParams(dbUrl, dbUser, dbPassword);
        // Убедимся, что соединение можно получить и БД в чистом состоянии
        DatabaseConnectionService.getConnection();
        DatabaseInitializerService.fullCleanDatabase();
        try {
            DatabaseInitializerService.resetDatabaseWithTestData();
            System.out.println("✅ База данных успешно пересоздана и наполнена тестовыми данными");
        } catch (Exception e) {
            System.err.println("❌ Ошибка инициализации БД: " + e.getMessage());
            throw new RuntimeException("Не удалось инициализировать тестовую БД", e);
        }
        
        // Проверка подключения
        boolean connected = DatabaseConnectionService.testConnection();
        
        if (!connected) {
            System.err.println("\n❌ ОШИБКА: Не удалось подключиться к базе данных!");
            System.err.println("Проверьте:");
            System.err.println("1. PostgreSQL запущен");
            System.err.println("2. База данных 'food_delivery' создана");
            System.err.println("3. Схема создана (выполнен schema.sql)");
            System.err.println("4. Параметры подключения:");
            System.err.println("   URL: " + dbUrl);
            System.err.println("   User: " + dbUser);
            System.err.println("\nДля изменения параметров используйте системные свойства:");
            System.err.println("   -Ddb.url=... -Ddb.user=... -Ddb.password=...");
        }
        DatabaseInitializerService.isDatabaseInitialized();
        DatabaseInitializerService.fullCleanDatabase();
    }
}