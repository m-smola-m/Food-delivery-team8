-- Таблица корзин
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT REFERENCES clients(id) ON DELETE CASCADE,
    UNIQUE(client_id)
);