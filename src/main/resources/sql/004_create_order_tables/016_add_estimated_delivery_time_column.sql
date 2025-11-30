ALTER TABLE orders
ADD COLUMN IF NOT EXISTS estimated_delivery_time TIMESTAMP;
