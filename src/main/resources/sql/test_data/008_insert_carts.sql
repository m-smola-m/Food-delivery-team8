-- Тестовые корзины
INSERT INTO carts (id, client_id)
VALUES
  (9001, 3001),
  (9002, 3002),
  (9003, 3003),
  (9004, 3004)
ON CONFLICT (client_id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('carts', 'id'), (SELECT COALESCE(MAX(id), 0) FROM carts));
