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