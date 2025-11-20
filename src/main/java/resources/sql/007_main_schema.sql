-- Главный скрипт, который выполняет все остальные в правильном порядке

\i 000_drop_tables.sql

\i 001_create_base_tables/001_create_addresses.sql
\i 001_create_base_tables/002_create_working_hours.sql
\i 001_create_base_tables/003_create_clients.sql

\i 002_create_shop_tables/004_create_shops.sql
\i 002_create_shop_tables/005_create_products.sql
\i 002_create_shop_tables/006_add_shop_foreign_keys.sql

\i 003_create_courier_tables/007_create_couriers.sql

\i 004_create_order_tables/008_create_orders.sql
\i 004_create_order_tables/009_create_order_items.sql

\i 005_create_cart_tables/010_create_carts.sql
\i 005_create_cart_tables/011_create_cart_items.sql
\i 005_create_cart_tables/012_add_cart_foreign_keys.sql

\i 006_create_indexes/013_create_indexes.sql