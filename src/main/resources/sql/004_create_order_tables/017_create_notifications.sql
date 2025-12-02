DROP TABLE IF EXISTS notifications CASCADE;

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    type VARCHAR(50),
    message VARCHAR(255) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_client
        FOREIGN KEY(client_id)
            REFERENCES clients(id)
            ON DELETE CASCADE
);

COMMENT ON TABLE notifications IS 'Таблица для хранения уведомлений пользователей';
COMMENT ON COLUMN notifications.id IS 'Уникальный идентификатор уведомления';
COMMENT ON COLUMN notifications.client_id IS 'Идентификатор клиента, которому ��дресовано уведомление';
COMMENT ON COLUMN notifications.type IS 'Тип уведомления (например, ORDER, PROMOTION)';
COMMENT ON COLUMN notifications.message IS 'Текст уведомления';
COMMENT ON COLUMN notifications.is_read IS 'Статус прочтения уведомления';
COMMENT ON COLUMN notifications.created_at IS 'Время создания уведомления';

