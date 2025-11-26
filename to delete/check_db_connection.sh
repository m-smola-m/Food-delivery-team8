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

    # Дополнительная проверка ключевых таблиц
    echo "🔍 Проверка ключевых таблиц..."
    KEY_TABLES=("clients" "shops" "products" "orders" "couriers" "carts" "cart_items" "order_items")
    MISSING_TABLES=()

    for table in "${KEY_TABLES[@]}"; do
        EXISTS=$(PGPASSWORD="$DB_PASSWORD" psql -U "$DB_USER" -d food_delivery -tAc "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = '$table');" 2>/dev/null)
        if [ "$EXISTS" = "t" ]; then
            echo "   ✅ Таблица '$table' существует"
        else
            echo "   ❌ Таблица '$table' отсутствует"
            MISSING_TABLES+=("$table")
        fi
    done

    if [ ${#MISSING_TABLES[@]} -gt 0 ]; then
        echo ""
        echo "⚠️  Отсутствуют некоторые таблицы: ${MISSING_TABLES[*]}"
        echo "   Выполните инициализацию схемы:"
        echo "   PGPASSWORD=$DB_PASSWORD psql -U $DB_USER -d food_delivery -f src/main/resources/sql/007_main_schema.sql"
    fi
else
    echo "❌ Схема не создана или пуста"
    echo "   Выполните: PGPASSWORD=$DB_PASSWORD psql -U $DB_USER -d food_delivery -f src/main/resources/sql/007_main_schema.sql"
    exit 1
fi

# Проверяем индексы
echo "🔍 Проверка индексов..."
INDEX_COUNT=$(PGPASSWORD="$DB_PASSWORD" psql -U "$DB_USER" -d food_delivery -tAc "SELECT COUNT(*) FROM pg_indexes WHERE schemaname = 'public';" 2>/dev/null)
if [ "$INDEX_COUNT" -gt 0 ]; then
    echo "✅ Индексы созданы (найдено: $INDEX_COUNT)"
else
    echo "⚠️  Индексы не найдены"
    echo "   Убедитесь, что скрипт 013_create_indexes.sql выполнен"
fi

echo ""
echo "✅ Все проверки пройдены!"
echo ""
echo "📝 Параметры подключения для тестов:"
echo "   URL: jdbc:postgresql://localhost:5432/food_delivery"
echo "   User: $DB_USER"
echo "   Password: $DB_PASSWORD"
echo ""
echo "📁 Структура SQL файлов:"
echo "   Основная схема: src/main/resources/sql/007_main_schema.sql"
echo "   Отдельные таблицы: src/main/resources/sql/[папки]/"
echo ""
echo "🚀 Запуск тестов:"
echo "   mvn test -Ddb.user=$DB_USER -Ddb.password=\"$DB_PASSWORD\""
echo ""
echo "🔄 Пересоздание схемы:"
echo "   PGPASSWORD=$DB_PASSWORD psql -U $DB_USER -d food_delivery -f run_scheme.sh"