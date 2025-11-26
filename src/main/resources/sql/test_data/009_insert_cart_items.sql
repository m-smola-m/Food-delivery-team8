-- Тестовые позиции корзины
INSERT INTO cart_items (id, cart_id, product_id, product_name, quantity, price)
VALUES
  (9101, 9001, 5001, 'Pepperoni', 1, 450.0),
  (9102, 9001, 5002, 'Four Cheese', 2, 480.0),
  (9103, 9002, 5003, 'Philadelphia', 1, 520.0),
  (9104, 9003, 5005, 'Classic Burger', 1, 390.0),
  (9105, 9003, 5006, 'Chicken Burger', 1, 370.0),
  (9106, 9004, 5007, 'Green Bowl', 2, 420.0)
ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('cart_items', 'id'), (SELECT COALESCE(MAX(id), 0) FROM cart_items));
