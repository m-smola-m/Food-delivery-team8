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