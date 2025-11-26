SET session_replication_role = 'replica';

DELETE FROM carts;
DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM couriers;
DELETE FROM products;
DELETE FROM shops;
DELETE FROM clients;
DELETE FROM working_hours;
DELETE FROM addresses;
DELETE FROM cart_items;

SET session_replication_role = 'origin';