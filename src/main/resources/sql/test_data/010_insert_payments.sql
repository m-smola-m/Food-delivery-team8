-- Тестовые платежи
INSERT INTO payments (id, order_id, amount, method, status)
VALUES
  (9201, 7001, 930.0, 'CARD', 'PAID'),
  (9202, 7002, 970.0, 'CASH', 'PENDING'),
  (9203, 7003, 760.0, 'ONLINE', 'PAID'),
  (9204, 7004, 820.0, 'CARD', 'PAID')
ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('payments', 'id'), (SELECT COALESCE(MAX(id), 0) FROM payments));
