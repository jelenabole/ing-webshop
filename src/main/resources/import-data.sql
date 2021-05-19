-- customer
INSERT INTO customer(email, first_name, last_name) VALUES ('john@email.com', 'John', 'Doe');

-- products - get, update, delete
INSERT INTO product(code, name, description, price_hrk, is_available) VALUES ('0000000001', 'Product for get', 'Product description', 17.25, true);
INSERT INTO product(code, name, description, price_hrk, is_available) VALUES ('0000000002', 'Product for update', 'Product description', 10,  true);
INSERT INTO product(code, name, description, price_hrk, is_available) VALUES ('0000000003', 'Product for delete', 'Product not available', 14.99, false);
INSERT INTO product(code, name, description, price_hrk, is_available) VALUES ('0000000004', 'Product for additional tests', 'Product description', 14.99, true);


-- orders - get, fail-update/finalize, update, delete
INSERT INTO webshop_order(customer_id, status, total_price_hrk, total_price_eur) VALUES (1, 'DRAFT', 0, 0);
INSERT INTO webshop_order(customer_id, status, total_price_hrk, total_price_eur) VALUES (1, 'SUBMITTED', 64.50, 8.58);
INSERT INTO webshop_order(customer_id, status, total_price_hrk, total_price_eur) VALUES (1, 'DRAFT', 0, 0);
INSERT INTO webshop_order(customer_id, status, total_price_hrk, total_price_eur) VALUES (1, 'DRAFT', 0, 0);
INSERT INTO webshop_order(customer_id, status, total_price_hrk, total_price_eur) VALUES (1, 'DRAFT', 0, 0);

-- order items
INSERT INTO order_item(order_id, product_id, quantity) VALUES (1, 1, 2);
INSERT INTO order_item(order_id, product_id, quantity) VALUES (2, 1, 2);
INSERT INTO order_item(order_id, product_id, quantity) VALUES (2, 2, 3);
INSERT INTO order_item(order_id, product_id, quantity) VALUES (5, 1, 2);
INSERT INTO order_item(order_id, product_id, quantity) VALUES (5, 2, 3);
