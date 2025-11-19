-- Таблица заказов
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    customer_id BIGINT,
    restaurant_id BIGINT,
    delivery_address_id BIGINT REFERENCES addresses(id), -- Ссылка на адрес
    courier_id BIGINT,
    total_price DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);