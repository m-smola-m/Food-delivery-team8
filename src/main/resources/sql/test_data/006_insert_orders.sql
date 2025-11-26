-- Тестовые заказы
INSERT INTO orders (id, status, customer_id, restaurant_id, delivery_address_id, delivery_address, courier_id, total_price, payment_status, payment_method, estimated_delivery_time)
VALUES
  (7001, 'CONFIRMED', 3001, 4001, 1001, 'Tverskaya, 1', 6001, 930.0, 'PAID', 'CARD', NOW() + INTERVAL '45 minutes'),
  (7002, 'PENDING', 3002, 4002, 1002, 'Nevsky, 25', NULL, 970.0, 'PENDING', 'CASH', NOW() + INTERVAL '55 minutes'),
  (7003, 'DELIVERED', 3003, 4003, 1003, 'Baumana, 5', 6003, 760.0, 'PAID', 'ONLINE', NOW() + INTERVAL '35 minutes'),
  (7004, 'CONFIRMED', 3004, 4004, 1004, 'Lenina, 50', 6004, 820.0, 'PAID', 'CARD', NOW() + INTERVAL '40 minutes')
ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('orders', 'id'), (SELECT COALESCE(MAX(id), 0) FROM orders));
