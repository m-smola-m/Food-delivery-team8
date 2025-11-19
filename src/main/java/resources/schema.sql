-- Схема базы данных для Food Delivery Service
-- PostgreSQL

-- Удаление таблиц если они существуют (для пересоздания)
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS carts CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS clients CASCADE;
DROP TABLE IF EXISTS shops CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS couriers CASCADE;
DROP TABLE IF EXISTS addresses CASCADE;
DROP TABLE IF EXISTS working_hours CASCADE;



-- Таблица клиентов
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    address_id BIGINT REFERENCES addresses(id),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    order_history TEXT[]
);

-- Таблица магазинов
CREATE TABLE shops (
    shop_id BIGSERIAL PRIMARY KEY,
    naming VARCHAR(200) NOT NULL,
    description TEXT,
    public_email VARCHAR(255),
    email_for_auth VARCHAR(255) UNIQUE NOT NULL,
    phone_for_auth VARCHAR(20) UNIQUE NOT NULL,
    public_phone VARCHAR(20),
    status VARCHAR(50) NOT NULL,
    address_id BIGINT REFERENCES addresses(id),
    working_hours_id BIGINT,
    owner_name VARCHAR(200),
    owner_contact_phone VARCHAR(20),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    rating DOUBLE PRECISION DEFAULT 0.0,
    type VARCHAR(50),
    password VARCHAR(255) NOT NULL
);

-- Таблица рабочих часов
CREATE TABLE working_hours (
    id BIGSERIAL PRIMARY KEY,
    monday VARCHAR(50),
    tuesday VARCHAR(50),
    wednesday VARCHAR(50),
    thursday VARCHAR(50),
    friday VARCHAR(50),
    saturday VARCHAR(50),
    sunday VARCHAR(50)
);

-- Добавляем внешний ключ для shops после создания working_hours
ALTER TABLE shops ADD CONSTRAINT fk_shops_working_hours
    FOREIGN KEY (working_hours_id) REFERENCES working_hours(id);

-- Таблица продуктов
CREATE TABLE products (
    product_id BIGSERIAL PRIMARY KEY,
    shop_id BIGINT REFERENCES shops(shop_id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    weight DOUBLE PRECISION,
    price DOUBLE PRECISION NOT NULL,
    category VARCHAR(50),
    is_available BOOLEAN DEFAULT TRUE,
    cooking_time_minutes BIGINT -- храним как секунды (Duration)
);

-- Таблица курьеров
CREATE TABLE couriers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL,
    transport_type VARCHAR(50),
    current_order_id BIGINT,
    current_balance DOUBLE PRECISION DEFAULT 0.0,
    bank_card BIGINT
);

-- Таблица корзин
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT REFERENCES clients(id) ON DELETE CASCADE,
    UNIQUE(client_id)
);

-- Таблица элементов корзины
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT REFERENCES carts(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES products(product_id) ON DELETE CASCADE,
    product_name VARCHAR(200) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    price DOUBLE PRECISION NOT NULL
);

-- Таблица заказов
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    customer_id BIGINT REFERENCES clients(id),
    restaurant_id BIGINT REFERENCES shops(shop_id),
    delivery_address_id BIGINT REFERENCES addresses(id),
    courier_id BIGINT REFERENCES couriers(id),
    total_price DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица элементов заказа
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES products(product_id),
    product_name VARCHAR(200) NOT NULL,
    quantity INTEGER NOT NULL,
    price DOUBLE PRECISION NOT NULL
);

-- Индексы для улучшения производительности
CREATE INDEX idx_clients_phone ON clients(phone);
CREATE INDEX idx_clients_email ON clients(email);
CREATE INDEX idx_shops_email_auth ON shops(email_for_auth);
CREATE INDEX idx_shops_phone_auth ON shops(phone_for_auth);
CREATE INDEX idx_couriers_phone ON couriers(phone_number);
CREATE INDEX idx_carts_client_id ON carts(client_id);
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_courier_id ON orders(courier_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_products_shop_id ON products(shop_id);

