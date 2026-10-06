ALTER TABLE customers ADD COLUMN first_name VARCHAR(255);
ALTER TABLE customers ADD COLUMN last_name VARCHAR(255);

UPDATE customers
SET first_name = split_part(full_name, ' ', 1),
    last_name = CASE
        WHEN strpos(full_name, ' ') = 0 THEN ''
        ELSE substr(full_name, strpos(full_name, ' ') + 1)
    END;
