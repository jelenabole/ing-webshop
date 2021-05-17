-- customer
INSERT INTO customer(id, email, first_name, last_name) VALUES (1, 'email@email.com', 'John', 'Doe');

-- products - get, update, delete
INSERT INTO product(id, code, name, description, price_hrk, is_available) VALUES (1, '0000000001', 'Product for get', 'Product description', 17.25, true);
INSERT INTO product(id, code, name, description, price_hrk, is_available) VALUES (2, '0000000002', 'Product for update', 'Product description', 10,  true);
INSERT INTO product(id, code, name, description, price_hrk, is_available) VALUES (3, '0000000003', 'Product for delete', 'Product not available', 14.99, false);

-- orders - get, fail-update/finalize, update, delete
INSERT INTO webshop_order(id, customer_id, status, total_price_hrk, total_price_eur) VALUES (1, 1, 'DRAFT', 0, 0);
INSERT INTO webshop_order(id, customer_id, status, total_price_hrk, total_price_eur) VALUES (2, 1, 'SUBMITTED', 64.50, 8.58);
INSERT INTO webshop_order(id, customer_id, status, total_price_hrk, total_price_eur) VALUES (3, 1, 'DRAFT', 0, 0);
INSERT INTO webshop_order(id, customer_id, status, total_price_hrk, total_price_eur) VALUES (4, 1, 'DRAFT', 0, 0);
INSERT INTO webshop_order(id, customer_id, status, total_price_hrk, total_price_eur) VALUES (5, 1, 'DRAFT', 0, 0);

-- order items
INSERT INTO order_item(id, order_id, product_id, quantity) VALUES (1, 1, 1, 2);
INSERT INTO order_item(id, order_id, product_id, quantity) VALUES (2, 2, 1, 2);
INSERT INTO order_item(id, order_id, product_id, quantity) VALUES (3, 2, 2, 3);
INSERT INTO order_item(id, order_id, product_id, quantity) VALUES (4, 5, 1, 2);
INSERT INTO order_item(id, order_id, product_id, quantity) VALUES (5, 5, 2, 3);
