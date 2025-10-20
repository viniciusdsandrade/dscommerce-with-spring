INSERT INTO tb_roles (authority)
SELECT 'ROLE_ADMIN'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_roles WHERE authority = 'ROLE_ADMIN');

INSERT INTO tb_roles (authority)
SELECT 'ROLE_CLIENT'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_roles WHERE authority = 'ROLE_CLIENT');

INSERT INTO tb_users (first_name, last_name, email, password)
SELECT 'Bob', 'Stone', 'bob@dscatalog.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_users WHERE email = 'bob@dscatalog.com');

INSERT INTO tb_users (first_name, last_name, email, password)
SELECT 'Ana', 'White', 'ana@dscatalog.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_users WHERE email = 'ana@dscatalog.com');

INSERT INTO tb_users (first_name, last_name, email, password)
SELECT 'John', 'Doe', 'john@dscatalog.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_users WHERE email = 'john@dscatalog.com');

INSERT INTO tb_users (first_name, last_name, email, password)
SELECT 'Beatriz', 'Lima', 'bia@dscatalog.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_users WHERE email = 'bia@dscatalog.com');

INSERT INTO tb_users (first_name, last_name, email, password)
SELECT 'Carlos', 'Silva', 'carlos@dscatalog.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_users WHERE email = 'carlos@dscatalog.com');

INSERT INTO tb_users (first_name, last_name, email, password)
SELECT 'Julia', 'Costa', 'julia@dscatalog.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_users WHERE email = 'julia@dscatalog.com');

INSERT INTO tb_users (first_name, last_name, email, password)
SELECT 'Rafael', 'Souza', 'rafa@dscatalog.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_users WHERE email = 'rafa@dscatalog.com');

INSERT INTO tb_users (first_name, last_name, email, password)
SELECT 'Patricia', 'Gomes', 'patricia@dscatalog.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_users WHERE email = 'patricia@dscatalog.com');

INSERT INTO tb_users_roles (users_id, roles_id)
SELECT u.id, r.id
FROM tb_users u
         JOIN tb_roles r ON r.authority = 'ROLE_CLIENT'
WHERE u.email = 'admin@dscatalog.com'
  AND NOT EXISTS (SELECT 1 FROM tb_users_roles ur WHERE ur.users_id = u.id AND ur.roles_id = r.id);

INSERT INTO tb_users_roles (users_id, roles_id)
SELECT u.id, r.id
FROM tb_users u
         JOIN tb_roles r ON r.authority = 'ROLE_CLIENT'
WHERE u.email = 'bob@dscatalog.com'
  AND NOT EXISTS (SELECT 1 FROM tb_users_roles ur WHERE ur.users_id = u.id AND ur.roles_id = r.id);

INSERT INTO tb_users_roles (users_id, roles_id)
SELECT u.id, r.id
FROM tb_users u
         JOIN tb_roles r ON r.authority = 'ROLE_ADMIN'
WHERE u.email = 'ana@dscatalog.com'
  AND NOT EXISTS (SELECT 1 FROM tb_users_roles ur WHERE ur.users_id = u.id AND ur.roles_id = r.id);

INSERT INTO tb_users_roles (users_id, roles_id)
SELECT u.id, r.id
FROM tb_users u
         JOIN tb_roles r ON r.authority = 'ROLE_CLIENT'
WHERE u.email = 'ana@dscatalog.com'
  AND NOT EXISTS (SELECT 1 FROM tb_users_roles ur WHERE ur.users_id = u.id AND ur.roles_id = r.id);

INSERT INTO tb_users_roles (users_id, roles_id)
SELECT u.id, r.id
FROM tb_users u
         JOIN tb_roles r ON r.authority = 'ROLE_ADMIN'
WHERE u.email = 'john@dscatalog.com'
  AND NOT EXISTS (SELECT 1 FROM tb_users_roles ur WHERE ur.users_id = u.id AND ur.roles_id = r.id);

INSERT INTO tb_users_roles (users_id, roles_id)
SELECT u.id, r.id
FROM tb_users u
         JOIN tb_roles r ON r.authority = 'ROLE_CLIENT'
WHERE u.email = 'john@dscatalog.com'
  AND NOT EXISTS (SELECT 1 FROM tb_users_roles ur WHERE ur.users_id = u.id AND ur.roles_id = r.id);

INSERT INTO tb_users_roles (users_id, roles_id)
SELECT u.id, r.id
FROM tb_users u
         JOIN tb_roles r ON r.authority = 'ROLE_CLIENT'
WHERE u.email = 'bia@dscatalog.com'
  AND NOT EXISTS (SELECT 1 FROM tb_users_roles ur WHERE ur.users_id = u.id AND ur.roles_id = r.id);

INSERT INTO tb_users_roles (users_id, roles_id)
SELECT u.id, r.id
FROM tb_users u
         JOIN tb_roles r ON r.authority = 'ROLE_ADMIN'
WHERE u.email = 'carlos@dscatalog.com'
  AND NOT EXISTS (SELECT 1 FROM tb_users_roles ur WHERE ur.users_id = u.id AND ur.roles_id = r.id);

INSERT INTO tb_users_roles (users_id, roles_id)
SELECT u.id, r.id
FROM tb_users u
         JOIN tb_roles r ON r.authority = 'ROLE_CLIENT'
WHERE u.email = 'julia@dscatalog.com'
  AND NOT EXISTS (SELECT 1 FROM tb_users_roles ur WHERE ur.users_id = u.id AND ur.roles_id = r.id);

INSERT INTO tb_users_roles (users_id, roles_id)
SELECT u.id, r.id
FROM tb_users u
         JOIN tb_roles r ON r.authority = 'ROLE_CLIENT'
WHERE u.email = 'rafa@dscatalog.com'
  AND NOT EXISTS (SELECT 1 FROM tb_users_roles ur WHERE ur.users_id = u.id AND ur.roles_id = r.id);

INSERT INTO tb_users_roles (users_id, roles_id)
SELECT u.id, r.id
FROM tb_users u
         JOIN tb_roles r ON r.authority = 'ROLE_ADMIN'
WHERE u.email = 'patricia@dscatalog.com'
  AND NOT EXISTS (SELECT 1 FROM tb_users_roles ur WHERE ur.users_id = u.id AND ur.roles_id = r.id);

INSERT INTO tb_users_roles (users_id, roles_id)
SELECT u.id, r.id
FROM tb_users u
         JOIN tb_roles r ON r.authority = 'ROLE_CLIENT'
WHERE u.email = 'patricia@dscatalog.com';


-- ============================
-- CATEGORIAS
-- ============================
INSERT INTO tb_category (category_name)
SELECT 'Computers'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_category WHERE category_name = 'Computers');

INSERT INTO tb_category (category_name)
SELECT 'Electronics'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_category WHERE category_name = 'Electronics');

INSERT INTO tb_category (category_name)
SELECT 'Books'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_category WHERE category_name = 'Books');

INSERT INTO tb_category (category_name)
SELECT 'Office'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_category WHERE category_name = 'Office');

INSERT INTO tb_category (category_name)
SELECT 'Games'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_category WHERE category_name = 'Games');

INSERT INTO tb_category (category_name)
SELECT 'Home & Kitchen'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_category WHERE category_name = 'Home & Kitchen');

INSERT INTO tb_category (category_name)
SELECT 'Sports'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_category WHERE category_name = 'Sports');

INSERT INTO tb_category (category_name)
SELECT 'Clothing'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_category WHERE category_name = 'Clothing');

-- ============================
-- PRODUTOS (nome único por NOT EXISTS)
-- ============================
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url)
SELECT 'The Pragmatic Programmer',
       'Classic software craftsmanship book.',
       199.90,
       'https://example.com/img/pragmatic-programmer.jpg'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_product WHERE product_name = 'The Pragmatic Programmer');

INSERT INTO tb_product (product_name, product_description, product_price, product_img_url)
SELECT 'Clean Code',
       'A Handbook of Agile Software Craftsmanship.',
       179.90,
       'https://example.com/img/clean-code.jpg'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_product WHERE product_name = 'Clean Code');

INSERT INTO tb_product (product_name, product_description, product_price, product_img_url)
SELECT 'Mechanical Keyboard 87-Key',
       'TKL mechanical keyboard with hot-swap switches.',
       499.00,
       'https://example.com/img/mech-keyboard.jpg'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_product WHERE product_name = 'Mechanical Keyboard 87-Key');

INSERT INTO tb_product (product_name, product_description, product_price, product_img_url)
SELECT 'Wireless Mouse',
       'Low-latency wireless mouse, USB receiver.',
       149.00,
       'https://example.com/img/wireless-mouse.jpg'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_product WHERE product_name = 'Wireless Mouse');

INSERT INTO tb_product (product_name, product_description, product_price, product_img_url)
SELECT 'USB-C Hub 7-in-1',
       'HDMI + USB 3.0 + SD Card + PD charging.',
       229.00,
       'https://example.com/img/usb-c-hub.jpg'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_product WHERE product_name = 'USB-C Hub 7-in-1');

INSERT INTO tb_product (product_name, product_description, product_price, product_img_url)
SELECT 'Monitor 27 4K',
       '27-inch 4K UHD IPS monitor.',
       1699.00,
       'https://example.com/img/monitor-27-4k.jpg'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_product WHERE product_name = 'Monitor 27 4K');

INSERT INTO tb_product (product_name, product_description, product_price, product_img_url)
SELECT 'Office Chair Ergonomic',
       'Adjustable lumbar support, breathable mesh.',
       899.00,
       'https://example.com/img/office-chair.jpg'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_product WHERE product_name = 'Office Chair Ergonomic');

INSERT INTO tb_product (product_name, product_description, product_price, product_img_url)
SELECT 'Notebook 15 i7 16GB 512GB',
       '15-inch laptop, Intel i7, 16GB RAM, 512GB SSD.',
       5299.00,
       'https://example.com/img/notebook-15.jpg'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_product WHERE product_name = 'Notebook 15 i7 16GB 512GB');

INSERT INTO tb_product (product_name, product_description, product_price, product_img_url)
SELECT 'Noise-Cancelling Headphones',
       'Over-ear ANC, Bluetooth.',
       1299.00,
       'https://example.com/img/anc-headphones.jpg'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_product WHERE product_name = 'Noise-Cancelling Headphones');

INSERT INTO tb_product (product_name, product_description, product_price, product_img_url)
SELECT 'Stainless Water Bottle 1L',
       'Insulated stainless steel bottle 1L.',
       119.90,
       'https://example.com/img/water-bottle-1l.jpg'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tb_product WHERE product_name = 'Stainless Water Bottle 1L');

-- ============================
-- PRODUTO x CATEGORIA (idempotente)
-- ============================

-- Pragmatic Programmer -> Books
INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Books'
WHERE p.product_name = 'The Pragmatic Programmer'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Clean Code -> Books
INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Books'
WHERE p.product_name = 'Clean Code'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Mechanical Keyboard -> Computers, Electronics
INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Computers'
WHERE p.product_name = 'Mechanical Keyboard 87-Key'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Electronics'
WHERE p.product_name = 'Mechanical Keyboard 87-Key'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Wireless Mouse -> Computers, Electronics
INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Computers'
WHERE p.product_name = 'Wireless Mouse'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Electronics'
WHERE p.product_name = 'Wireless Mouse'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- USB-C Hub -> Computers, Electronics
INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Computers'
WHERE p.product_name = 'USB-C Hub 7-in-1'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Electronics'
WHERE p.product_name = 'USB-C Hub 7-in-1'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Monitor 27 4K -> Computers, Electronics
INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Computers'
WHERE p.product_name = 'Monitor 27 4K'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Electronics'
WHERE p.product_name = 'Monitor 27 4K'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Office Chair Ergonomic -> Office, Home & Kitchen
INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Office'
WHERE p.product_name = 'Office Chair Ergonomic'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Home & Kitchen'
WHERE p.product_name = 'Office Chair Ergonomic'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Notebook 15 i7 16GB 512GB -> Computers, Electronics
INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Computers'
WHERE p.product_name = 'Notebook 15 i7 16GB 512GB'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Electronics'
WHERE p.product_name = 'Notebook 15 i7 16GB 512GB'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Noise-Cancelling Headphones -> Electronics
INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Electronics'
WHERE p.product_name = 'Noise-Cancelling Headphones'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Stainless Water Bottle 1L -> Sports, Home & Kitchen
INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Sports'
WHERE p.product_name = 'Stainless Water Bottle 1L'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

INSERT INTO tb_product_category (product_id, category_id)
SELECT p.id, c.id
FROM tb_product p
         JOIN tb_category c ON c.category_name = 'Home & Kitchen'
WHERE p.product_name = 'Stainless Water Bottle 1L'
  AND NOT EXISTS (SELECT 1 FROM tb_product_category pc WHERE pc.product_id = p.id AND pc.category_id = c.id);
