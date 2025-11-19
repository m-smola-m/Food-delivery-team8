ALTER TABLE shops ADD CONSTRAINT fk_shops_working_hours
    FOREIGN KEY (working_hours_id) REFERENCES working_hours(id);