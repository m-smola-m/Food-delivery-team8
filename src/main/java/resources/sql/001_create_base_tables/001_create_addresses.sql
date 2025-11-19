-- Таблица адресов
CREATE TABLE addresses (
                           id BIGSERIAL PRIMARY KEY,
                           street VARCHAR(255) NOT NULL,
                           city VARCHAR(100) NOT NULL,
                           postal_code VARCHAR(20),
                           country VARCHAR(100) DEFAULT 'Russia',
                           latitude DOUBLE PRECISION,
                           longitude DOUBLE PRECISION,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);