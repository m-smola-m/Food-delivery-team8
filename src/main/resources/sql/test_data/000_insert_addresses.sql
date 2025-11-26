-- Тестовые адреса
INSERT INTO addresses (id, country, city, street, building, apartment, postal_code, address_note)
VALUES
  (1001, 'Russia', 'Moscow', 'Tverskaya', '1', '10', '101000', 'Test address near Kremlin'),
  (1002, 'Russia', 'Saint Petersburg', 'Nevsky', '25', '15', '190000', 'Test address near Hermitage'),
  (1003, 'Russia', 'Kazan', 'Baumana', '5', NULL, '420111', 'Central street'),
  (1004, 'Russia', 'Novosibirsk', 'Lenina', '50', '8', '630000', 'Close to the Opera and Ballet Theatre'),
  (1005, 'Russia', 'Yekaterinburg', 'Malysheva', '15', '21', '620000', 'Historic city center')
ON CONFLICT DO NOTHING;

-- Обновляем последовательность, чтобы новые записи не конфликтовали
SELECT setval(pg_get_serial_sequence('addresses', 'id'), (SELECT COALESCE(MAX(id), 0) FROM addresses));
