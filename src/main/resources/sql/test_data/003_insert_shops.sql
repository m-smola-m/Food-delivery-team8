-- Тестовые магазины
INSERT INTO shops (shop_id, naming, description, public_email, email_for_auth, phone_for_auth, public_phone, status, address_id, working_hours_id, owner_name, owner_contact_phone, type, password)
VALUES
  (4001, 'Demo Pizza', 'Handmade pizza', 'contact@demopizza.test', 'auth@demopizza.test', '+70000010001', '+70000020001', 'OPEN', 1001, 2001, 'Owner One', '+70000090001', 'RESTAURANT', 'pwd1'),
  (4002, 'Demo Sushi', 'Fresh sushi', 'contact@demosushi.test', 'auth@demosushi.test', '+70000010002', '+70000020002', 'OPEN', 1002, 2002, 'Owner Two', '+70000090002', 'RESTAURANT', 'pwd2'),
  (4003, 'Burger Hub', 'Street burger spot', 'contact@burgerhub.test', 'auth@burgerhub.test', '+70000010003', '+70000020003', 'OPEN', 1003, 2003, 'Owner Three', '+70000090003', 'RESTAURANT', 'pwd3'),
  (4004, 'Vegan Bowl', 'Healthy bowls and salads', 'contact@veganbowl.test', 'auth@veganbowl.test', '+70000010004', '+70000020004', 'OPEN', 1004, 2004, 'Owner Four', '+70000090004', 'RESTAURANT', 'pwd4')
ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('shops', 'shop_id'), (SELECT COALESCE(MAX(shop_id), 0) FROM shops));
