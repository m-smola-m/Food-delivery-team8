ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS delivery_address TEXT,
    ADD COLUMN IF NOT EXISTS delivery_address_id BIGINT REFERENCES addresses(id);
