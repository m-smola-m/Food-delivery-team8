-- Тестовые продукты
INSERT INTO products (product_id, shop_id, name, description, weight, price, category, is_available, cooking_time_minutes)
VALUES
  (5001, 4001, 'Pepperoni', 'Classic pepperoni pizza', 0.5, 450.0, 'PIZZA', TRUE, 20),
  (5002, 4001, 'Four Cheese', 'Cheese mix pizza', 0.55, 480.0, 'PIZZA', TRUE, 22),
  (5003, 4002, 'Philadelphia', 'Roll with salmon', 0.25, 520.0, 'SUSHI', TRUE, 18),
  (5004, 4002, 'California', 'Roll with crab', 0.24, 450.0, 'SUSHI', TRUE, 16),
  (5005, 4003, 'Classic Burger', 'Beef burger with cheddar', 0.3, 390.0, 'BURGER', TRUE, 15),
  (5006, 4003, 'Chicken Burger', 'Grilled chicken burger', 0.3, 370.0, 'BURGER', TRUE, 14),
  (5007, 4004, 'Green Bowl', 'Quinoa, avocado and greens', 0.45, 420.0, 'SALAD', TRUE, 12),
  (5008, 4004, 'Tofu Bowl', 'Tofu with vegetables', 0.4, 400.0, 'SALAD', TRUE, 12)
ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('products', 'product_id'), (SELECT COALESCE(MAX(product_id), 0) FROM products));
