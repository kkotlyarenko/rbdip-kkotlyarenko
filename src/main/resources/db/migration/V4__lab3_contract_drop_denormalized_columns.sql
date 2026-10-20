DELETE FROM order_items WHERE product_id IS NULL;

ALTER TABLE order_items ALTER COLUMN product_id SET NOT NULL;
ALTER TABLE orders ALTER COLUMN customer_id SET NOT NULL;

ALTER TABLE customers ALTER COLUMN first_name SET NOT NULL;
ALTER TABLE customers ALTER COLUMN last_name SET NOT NULL;

ALTER TABLE orders DROP COLUMN customer_full_name;
ALTER TABLE orders DROP COLUMN customer_address;
ALTER TABLE orders DROP COLUMN customer_phone;

ALTER TABLE order_items DROP COLUMN product_name;
ALTER TABLE order_items DROP COLUMN product_price;

ALTER TABLE customers DROP COLUMN full_name;
