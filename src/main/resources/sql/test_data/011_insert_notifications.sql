-- Тестовые данные для уведомлений
INSERT INTO notifications (client_id, type, message, is_read, created_at) VALUES
(3001, 'ORDER', 'Ваш заказ #1 на сумму 670₽ принят.', false, CURRENT_TIMESTAMP - INTERVAL '2 hours'),
(3001, 'PAYMENT', 'Оплата заказа #1 прошла успешно.', false, CURRENT_TIMESTAMP - INTERVAL '1.5 hours'),
(3001, 'ORDER', 'Ваш заказ #2 на сумму 410₽ принят.', false, CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(3001, 'ORDER', 'Ваш заказ #3 на сумму 860₽ принят.', true, CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
(3001, 'PAYMENT', 'Оплата заказа #3 прошла успешно.', true, CURRENT_TIMESTAMP - INTERVAL '25 minutes'),
(3002, 'ORDER', 'Ваш заказ #4 на сумму 550₽ принят.', false, CURRENT_TIMESTAMP - INTERVAL '3 hours'),
(3002, 'ORDER', 'Ваш заказ #5 на сумму 720₽ принят.', true, CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(3003, 'ORDER', 'Ваш заказ #6 на сумму 320₽ принят.', false, CURRENT_TIMESTAMP - INTERVAL '4 hours'),
(3003, 'PAYMENT', 'Оплата заказа #6 прошла успешно.', false, CURRENT_TIMESTAMP - INTERVAL '3.5 hours');

