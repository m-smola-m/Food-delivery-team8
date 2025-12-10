-- Добавить колонку photo_url в products, если её нет
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='products' AND column_name='photo_url'
    ) THEN
        ALTER TABLE products ADD COLUMN photo_url VARCHAR(500);
    END IF;
END$$;

