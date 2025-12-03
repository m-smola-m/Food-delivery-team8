package com.team8.fooddelivery.util;

import com.team8.fooddelivery.service.DatabaseInitializerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    /**
     * Инициализирует базу данных: создает схему и загружает тестовые данные
     */
    public static void initializeDatabase() {
        try {
            logger.info("🔧 Инициализация структуры базы данных...");
            DatabaseInitializerService.initializeDatabase();
            logger.info("✅ Структура базы данных создана успешно");
        } catch (Exception e) {
            logger.error("❌ Ошибка при инициализации структуры БД", e);
            throw new RuntimeException("Не удалось инициализировать структуру БД", e);
        }
    }

    /**
     * Загружает тестовые данные в базу данных
     */
    public static void loadTestData() {
        try {
            logger.info("📊 Загрузка тестовых данных...");
            DatabaseInitializerService.loadTestData();
            logger.info("✅ Тестовые данные загружены успешно");
        } catch (Exception e) {
            logger.error("❌ Ошибка при загрузке тестовых данных", e);
            throw new RuntimeException("Не удалось загрузить тестовые данные", e);
        }
    }
}
