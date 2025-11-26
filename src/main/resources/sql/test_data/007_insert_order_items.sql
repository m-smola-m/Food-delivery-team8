-- Тестовые позиции заказов
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, price)
VALUES
  (8001, 7001, 5001, 'Pepperoni', 1, 450.0),
  (8002, 7001, 5002, 'Four Cheese', 1, 480.0),
  (8003, 7002, 5003, 'Philadelphia', 1, 520.0),
  (8004, 7002, 5004, 'California', 1, 450.0),
  (8005, 7003, 5005, 'Classic Burger', 1, 390.0),
  (8006, 7003, 5006, 'Chicken Burger', 1, 370.0),
  (8007, 7004, 5007, 'Green Bowl', 1, 420.0),
  (8008, 7004, 5008, 'Tofu Bowl', 1, 400.0)
ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('order_items', 'id'), (SELECT COALESCE(MAX(id), 0) FROM order_items));
