-- Тестовые курьеры
INSERT INTO couriers (id, name, password, phone_number, status, transport_type, current_balance, bank_card)
VALUES
  (6001, 'Courier One', 'pwd1', '+70000030001', 'ON_SHIFT', 'BIKE', 0.0, 1111222233334444),
  (6002, 'Courier Two', 'pwd2', '+70000030002', 'OFF_SHIFT', 'CAR', 1500.0, 5555666677778888),
  (6003, 'Courier Three', 'pwd3', '+70000030003', 'ON_SHIFT', 'SCOOTER', 250.0, 9999888877776666),
  (6004, 'Courier Four', 'pwd4', '+70000030004', 'OFF_SHIFT', 'FOOT', 80.0, 4444333322221111)
ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('couriers', 'id'), (SELECT COALESCE(MAX(id), 0) FROM couriers));
