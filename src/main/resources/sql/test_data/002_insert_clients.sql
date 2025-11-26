-- Тестовые клиенты
INSERT INTO clients (id, name, phone, password_hash, email, address_id, status, is_active)
VALUES
  (3001, 'Test Client One', '+70000000001', 'hash1', 'client1@test.local', 1001, 'ACTIVE', TRUE),
  (3002, 'Test Client Two', '+70000000002', 'hash2', 'client2@test.local', 1002, 'ACTIVE', TRUE),
  (3003, 'Test Client Three', '+70000000003', 'hash3', 'client3@test.local', 1003, 'ACTIVE', TRUE),
  (3004, 'Test Client Four', '+70000000004', 'hash4', 'client4@test.local', 1004, 'ACTIVE', TRUE)
ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('clients', 'id'), (SELECT COALESCE(MAX(id), 0) FROM clients));
