#!/bin/bash

# Скрипт для проверки подключения к PostgreSQL
# Использует фиксированные учетные данные

echo "🔍 Проверка подключения к PostgreSQL..."
echo ""

# Фиксированные учетные данные для БД
DB_USER="fooddelivery_user"
DB_PASSWORD="fooddelivery_pass"
echo "📍 Используем пользователя БД: $DB_USER"

# Проверяем, запущен ли PostgreSQL
echo "🔍 Проверка статуса PostgreSQL..."
if command -v pg_isready &> /dev/null; then
    if pg_isready -h localhost -p 5432 &> /dev/null; then
        echo "✅ PostgreSQL запущен"
    else
        echo "❌ PostgreSQL не запущен или недоступен"
        echo "   Запустите PostgreSQL: brew services start postgresql (macOS) или systemctl start postgresql (Linux)"
        exit 1
    fi
else
    echo "⚠️  pg_isready не найден, пропускаем проверку статуса"
fi

# Проверяем существование базы данных
echo "🔍 Проверка базы данных 'food_delivery'..."
if PGPASSWORD="$DB_PASSWORD" psql -U "$DB_USER" -lqt 2>/dev/null | cut -d \| -f 1 | grep -qw food_delivery; then
    echo "✅ База данных 'food_delivery' существует"
else
    echo "❌ База данных 'food_delivery' не найдена"
    echo "   Выполните скрипт создания: psql -U postgres -f src/main/resources/create_user.sql"
    exit 1
fi

# Проверяем схему
echo "🔍 Проверка схемы БД..."
TABLE_COUNT=$(PGPASSWORD="$DB_PASSWORD" psql -U "$DB_USER" -d food_delivery -tAc "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';" 2>/dev/null)
if [ "$TABLE_COUNT" -gt 0 ]; then
    echo "✅ Схема создана (найдено таблиц: $TABLE_COUNT)"
else
    echo "❌ Схема не создана или пуста"
    echo "   Выполните: PGPASSWORD=$DB_PASSWORD psql -U $DB_USER -d food_delivery -f src/main/resources/schema.sql"
    exit 1
fi

echo ""
echo "✅ Все проверки пройдены!"
echo ""
echo "📝 Параметры подключения для тестов:"
echo "   URL: jdbc:postgresql://localhost:5432/food_delivery"
echo "   User: $DB_USER"
echo "   Password: $DB_PASSWORD"
echo ""
echo "🚀 Запуск тестов:"
echo "   mvn test -Ddb.user=$DB_USER -Ddb.password=\"$DB_PASSWORD\""
