-- Универсальный главный скрипт SQL схемы
-- С правильными путями к файлам

\echo '🚀 Начало выполнения SQL схемы...'

\echo '📁 Выполнение файла: 000_drop_tables.sql'
\ir 000_drop_tables.sql

\echo '📁 Выполнение файла: 001_create_addresses.sql'
\ir 001_create_base_tables/001_create_addresses.sql

\echo '📁 Выполнение файла: 002_create_working_hours.sql'
\ir 001_create_base_tables/002_create_working_hours.sql

\echo '📁 Выполнение файла: 003_create_clients.sql'
\ir 001_create_base_tables/003_create_clients.sql

\echo '📁 Выполнение файла: 004_create_shops.sql'
\ir 002_create_shop_tables/004_create_shops.sql

\echo '📁 Выполнение файла: 005_create_products.sql'
\ir 002_create_shop_tables/005_create_products.sql

\echo '📁 Выполнение файла: 006_add_shop_foreign_keys.sql'
\ir 002_create_shop_tables/006_add_shop_foreign_keys.sql

\echo '📁 Выполнение файла: 003_create_courier_tables.sql'
\ir 003_create_courier_tables/003_create_courier_tables.sql

\echo '📁 Выполнение файла: 008_create_orders.sql'
\ir 004_create_order_tables/008_create_orders.sql

\echo '📁 Выполнение файла: 009_create_order_items.sql'
\ir 004_create_order_tables/009_create_order_items.sql

\echo '📁 Выполнение файла: 010_create_carts.sql'
\ir 004_create_order_tables/010_create_carts.sql

\echo '📁 Выполнение файла: 011_create_payments.sql'
\ir 004_create_order_tables/011_create_payments.sql

\echo '📁 Выполнение файла: 011_create_payments.sql'
\i ./004_create_order_tables/011_create_payments.sql

\echo '📁 Выполнение файла: 011_create_cart_items.sql'
\ir 005_create_cart_tables/011_create_cart_items.sql

\echo '📁 Выполнение файла: 012_add_cart_foreign_keys.sql'
\ir 005_create_cart_tables/012_add_cart_foreign_keys.sql

\echo '📁 Выполнение файла: 013_create_indexes.sql'
\ir 006_create_indexes/013_create_indexes.sql

\echo '📁 Выполнение файла: 014_add_delivery_address_columns.sql'
\ir 004_create_order_tables/014_add_delivery_address_columns.sql

\echo '📁 Выполнение файла: 015_add_payment_columns.sql'
\ir 004_create_order_tables/015_add_payment_columns.sql

\echo '📁 Выполнение файла: 016_add_estimated_delivery_time_column.sql'
\ir 004_create_order_tables/016_add_estimated_delivery_time_column.sql

\echo '📁 Выполнение файла: 014_add_delivery_address_columns.sql'
\i ./004_create_order_tables/014_add_delivery_address_columns.sql

\echo '📁 Выполнение файла: 015_add_payment_columns.sql'
\i ./004_create_order_tables/015_add_payment_columns.sql

\echo '📁 Выполнение файла: 016_add_estimated_delivery_time_column.sql'
\i ./004_create_order_tables/016_add_estimated_delivery_time_column.sql

\echo '🎉 Все SQL файлы выполнены успешно!'
\echo '📊 База данных food_delivery готова к использованию.'
