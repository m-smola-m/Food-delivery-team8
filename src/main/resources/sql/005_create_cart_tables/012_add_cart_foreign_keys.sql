-- Добавляем внешние ключи для корзин и связанных таблиц
ALTER TABLE carts ADD CONSTRAINT fk_carts_client
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE;

ALTER TABLE cart_items ADD CONSTRAINT fk_cart_items_cart
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE;

ALTER TABLE cart_items ADD CONSTRAINT fk_cart_items_product
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE;