DROP DATABASE IF EXISTS db_dscommerce;
CREATE DATABASE IF NOT EXISTS db_dscommerce;
USE db_dscommerce;

CREATE TABLE IF NOT EXISTS tb_user
(
    id              BIGINT UNSIGNED AUTO_INCREMENT,
    user_name       VARCHAR(255) NOT NULL,
    user_email      VARCHAR(255) NOT NULL UNIQUE,
    user_password   VARCHAR(255) NOT NULL,
    user_birth_date DATE         NOT NULL,
    user_phone      VARCHAR(255),
    user_roles      VARCHAR(255) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tb_product
(
    id                  BIGINT UNSIGNED AUTO_INCREMENT,
    product_name        VARCHAR(255) NOT NULL,
    product_description TEXT         NOT NULL,
    product_price       FLOAT(53)    NOT NULL,
    product_img_url     VARCHAR(255) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tb_category
(
    id            BIGINT UNSIGNED AUTO_INCREMENT,
    category_name VARCHAR(255) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tb_product_category
(
    product_id  BIGINT UNSIGNED NOT NULL,
    category_id BIGINT UNSIGNED NOT NULL,

    PRIMARY KEY (category_id, product_id),

    FOREIGN KEY (category_id) REFERENCES tb_category (id),
    FOREIGN KEY (product_id) REFERENCES tb_product (id)
);

CREATE TABLE IF NOT EXISTS tb_order
(
    id           BIGINT UNSIGNED AUTO_INCREMENT,
    client_id    BIGINT UNSIGNED NULL,
    order_moment TIMESTAMP       NOT NULL,

    PRIMARY KEY (id),

    FOREIGN KEY (client_id) REFERENCES tb_user (id)
);

CREATE TABLE tb_order_item
(
    product_id BIGINT UNSIGNED NOT NULL,
    order_id   BIGINT UNSIGNED NOT NULL,
    quantity   INT UNSIGNED    NULL,
    price      DOUBLE          NULL,

    PRIMARY KEY (order_id, product_id),

    FOREIGN KEY (product_id) REFERENCES tb_product (id),
    FOREIGN KEY (order_id) REFERENCES tb_order (id)
);

CREATE TABLE tb_payment
(
    order_id       BIGINT UNSIGNED NOT NULL,
    payment_moment TIMESTAMP       NOT NULL,

    PRIMARY KEY (order_id),

    FOREIGN KEY (order_id) REFERENCES tb_order (id)
);

CREATE TABLE IF NOT EXISTS OrderStatus
(
    id     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    status VARCHAR(255) NOT NULL,
    
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS OrderStatus;

INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone, user_roles)
VALUES ('User1', 'user1@example.com', 'password1', '1990-01-01', '1234567890', 'ROLE_USER'),
       ('User2', 'user2@example.com', 'password2', '1991-02-02', '2345678901', 'ROLE_USER'),
       ('User3', 'user3@example.com', 'password3', '1992-03-03', '3456789012', 'ROLE_USER'),
       ('User4', 'user4@example.com', 'password4', '1993-04-04', '4567890123', 'ROLE_USER'),
       ('User5', 'user5@example.com', 'password5', '1994-05-05', '5678901234', 'ROLE_USER'),
       ('User6', 'user6@example.com', 'password6', '1995-06-06', '6789012345', 'ROLE_USER'),
       ('User7', 'user7@example.com', 'password7', '1996-07-07', '7890123456', 'ROLE_USER'),
       ('User8', 'user8@example.com', 'password8', '1997-08-08', '8901234567', 'ROLE_USER'),
       ('User9', 'user9@example.com', 'password9', '1998-09-09', '9012345678', 'ROLE_USER'),
       ('User10', 'user10@example.com', 'password10', '1999-10-10', '0123456789', 'ROLE_USER'),
       ('User11', 'user11@example.com', 'password11', '2000-11-11', '1234567890', 'ROLE_USER'),
       ('User12', 'user12@example.com', 'password12', '2001-12-12', '2345678901', 'ROLE_USER'),
       ('User13', 'user13@example.com', 'password13', '2002-01-13', '3456789012', 'ROLE_USER'),
       ('User14', 'user14@example.com', 'password14', '2003-02-14', '4567890123', 'ROLE_USER'),
       ('User15', 'user15@example.com', 'password15', '2004-03-15', '5678901234', 'ROLE_USER'),
       ('User16', 'user16@example.com', 'password16', '2005-04-16', '6789012345', 'ROLE_USER'),
       ('User17', 'user17@example.com', 'password17', '2006-05-17', '7890123456', 'ROLE_USER'),
       ('User18', 'user18@example.com', 'password18', '2007-06-18', '8901234567', 'ROLE_USER'),
       ('User19', 'user19@example.com', 'password19', '2008-07-19', '9012345678', 'ROLE_USER'),
       ('User20', 'user20@example.com', 'password20', '2009-08-20', '0123456789', 'ROLE_USER'),
       ('User21', 'user21@example.com', 'password21', '2010-09-21', '1234567890', 'ROLE_USER'),
       ('User22', 'user22@example.com', 'password22', '2011-10-22', '2345678901', 'ROLE_USER'),
       ('User23', 'user23@example.com', 'password23', '2012-11-23', '3456789012', 'ROLE_USER'),
       ('User24', 'user24@example.com', 'password24', '2013-12-24', '4567890123', 'ROLE_USER'),
       ('User25', 'user25@example.com', 'password25', '2014-01-25', '5678901234', 'ROLE_USER'),
       ('User26', 'user26@example.com', 'password26', '2015-02-26', '6789012345', 'ROLE_USER'),
       ('User27', 'user27@example.com', 'password27', '2016-03-27', '7890123456', 'ROLE_USER'),
       ('User28', 'user28@example.com', 'password28', '2017-04-28', '8901234567', 'ROLE_USER'),
       ('User29', 'user29@example.com', 'password29', '2018-05-29', '9012345678', 'ROLE_USER'),
       ('User30', 'user30@example.com', 'password30', '2019-06-30', '0123456789', 'ROLE_USER');


INSERT INTO tb_product (product_name, product_description, product_price, product_img_url)
VALUES ('Laptop', 'Powerful laptop with high performance', 999.99, 'laptop.jpg'),
       ('Smartphone', 'Latest smartphone with advanced features', 599.99, 'smartphone.jpg'),
       ('Headphones', 'High-quality over-ear headphones', 99.99, 'headphones.jpg'),
       ('Camera', 'Professional camera for photography enthusiasts', 799.99, 'camera.jpg'),
       ('Fitness Tracker', 'Track your fitness activities with this wearable', 49.99, 'fitness_tracker.jpg'),
       ('Tablet', 'Portable tablet for productivity on the go', 349.99, 'tablet.jpg'),
       ('Gaming Console', 'Next-gen gaming console for immersive gaming', 449.99, 'gaming_console.jpg'),
       ('Smart Watch', 'Stay connected with this stylish smartwatch', 179.99, 'smart_watch.jpg'),
       ('Bluetooth Speaker', 'Wireless Bluetooth speaker for music lovers', 79.99, 'bluetooth_speaker.jpg'),
       ('4K TV', 'Ultra HD 4K television for an amazing viewing experience', 1299.99, '4k_tv.jpg'),
       ('Coffee Maker', 'Automatic coffee maker for your daily brew', 79.99, 'coffee_maker.jpg'),
       ('Camping Tent', 'Spacious tent for outdoor adventures', 149.99, 'camping_tent.jpg'),
       ('Smart Thermostat', 'Programmable smart thermostat for home comfort', 129.99, 'smart_thermostat.jpg'),
       ('Wireless Mouse', 'Ergonomic wireless mouse for efficient computing', 29.99, 'wireless_mouse.jpg'),
       ('LED Desk Lamp', 'Adjustable LED desk lamp for workspace illumination', 39.99, 'led_desk_lamp.jpg'),
       ('Designer Handbag', 'Stylish designer handbag for a fashionable look', 199.99, 'designer_handbag.jpg'),
       ('Best-Selling Novel', 'A captivating novel by a renowned author', 14.99, 'best_selling_novel.jpg'),
       ('Blender', 'High-performance blender for smoothies and more', 89.99, 'blender.jpg'),
       ('Travel Backpack', 'Durable travel backpack for your adventures', 59.99, 'travel_backpack.jpg'),
       ('HD Projector', 'Home theater HD projector for cinematic experiences', 399.99, 'hd_projector.jpg'),
       ('Skincare Set', 'Complete skincare set for radiant skin', 49.99, 'skincare_set.jpg'),
       ('Board Game', 'Classic board game for family fun', 24.99, 'board_game.jpg'),
       ('Pet Bed', 'Comfortable pet bed for your furry friend', 34.99, 'pet_bed.jpg'),
       ('Acoustic Guitar', 'Quality acoustic guitar for music enthusiasts', 299.99, 'acoustic_guitar.jpg'),
       ('Car Phone Holder', 'Convenient car phone holder for hands-free driving', 9.99, 'car_phone_holder.jpg'),
       ('Tool Set', 'Comprehensive tool set for DIY projects', 69.99, 'tool_set.jpg'),
       ('Diamond Necklace', 'Elegant diamond necklace for special occasions', 499.99, 'diamond_necklace.jpg'),
       ('Running Shoes', 'High-performance running shoes for fitness enthusiasts', 79.99, 'running_shoes.jpg'),
       ('Kids Toy Set', 'Colorful toy set for kids\' playtime', 19.99, 'kids_toy_set.jpg'),
       ('Canvas Wall Art', 'Beautiful canvas wall art for home decor', 59.99, 'canvas_wall_art.jpg');

-- Inserir 10 categorias
INSERT INTO tb_category (category_name)
VALUES ('Electronics'),
       ('Mobile Devices'),
       ('Audio'),
       ('Photography'),
       ('Fitness'),
       ('Tablets'),
       ('Gaming'),
       ('Wearables'),
       ('Audio Accessories'),
       ('Televisions'),
       ('Home Appliances'),
       ('Outdoor Gear'),
       ('Smart Home'),
       ('Office Electronics'),
       ('Smart Lighting'),
       ('Fashion'),
       ('Books'),
       ('Kitchen Appliances'),
       ('Travel Accessories'),
       ('Furniture'),
       ('Health & Beauty'),
       ('Toys & Games'),
       ('Pet Supplies'),
       ('Musical Instruments'),
       ('Car Accessories'),
       ('Tools & Hardware'),
       ('Jewelry'),
       ('Sports & Outdoors'),
       ('Baby & Kids'),
       ('Home Decor');

-- Associar produtos a categorias
INSERT INTO tb_product_category (category_id, product_id)
VALUES (1, 1),
       (2, 1),   -- Laptop pertence a Electronics e Mobile Devices
       (1, 2),
       (2, 2),   -- Smartphone pertence a Electronics e Mobile Devices
       (3, 3),   -- Headphones pertence a Audio
       (4, 4),   -- Camera pertence a Photography
       (5, 5),   -- Fitness Tracker pertence a Fitness
       (1, 6),   -- Tablet pertence a Electronics e Tablets
       (7, 7),   -- Gaming Console pertence a Gaming
       (8, 8),   -- Smart Watch pertence a Wearables
       (9, 9),   -- Bluetooth Speaker pertence a Audio Accessories
       (10, 10),
       (11, 11), -- Coffee Maker pertence a Home Appliances
       (12, 12), -- Camping Tent pertence a Outdoor Gear
       (13, 13), -- Smart Thermostat pertence a Smart Home
       (14, 14), -- Wireless Mouse pertence a Office Electronics
       (15, 15), -- LED Desk Lamp pertence a Smart Lighting
       (16, 16), -- Designer Handbag pertence a Fashion
       (17, 17), -- Best-Selling Novel pertence a Books
       (18, 18), -- Blender pertence a Kitchen Appliances
       (19, 19), -- Travel Backpack pertence a Travel Accessories
       (20, 20), -- HD Projector pertence a Electronics e Home Appliances
       (21, 21), -- Skincare Set pertence a Health & Beauty
       (22, 22), -- Board Game pertence a Toys & Games
       (23, 23), -- Pet Bed pertence a Pet Supplies
       (24, 24), -- Acoustic Guitar pertence a Musical Instruments
       (25, 25), -- Car Phone Holder pertence a Car Accessories
       (26, 26), -- Tool Set pertence a Tools & Hardware
       (27, 27), -- Diamond Necklace pertence a Jewelry
       (28, 28), -- Running Shoes pertence a Sports & Outdoors
       (29, 29), -- Kids Toy Set pertence a Baby & Kids
       (30, 30);


-- Inserir 10 pedidos (orders)
INSERT INTO tb_order (client_id, order_moment)
VALUES (1, '2023-01-01 10:00:00'),
       (2, '2023-02-02 11:30:00'),
       (3, '2023-03-03 13:45:00'),
       (4, '2023-04-04 15:20:00'),
       (5, '2023-05-05 17:10:00'),
       (6, '2023-06-06 19:25:00'),
       (7, '2023-07-07 21:30:00'),
       (8, '2023-08-08 23:40:00'),
       (9, '2023-09-09 08:15:00'),
       (10, '2023-10-10 09:50:00'),
       (11, '2023-11-11 14:00:00'),
       (12, '2023-12-12 16:30:00'),
       (13, '2024-01-01 18:45:00'),
       (14, '2024-02-02 20:20:00'),
       (15, '2024-03-03 22:10:00'),
       (16, '2024-04-04 00:25:00'),
       (17, '2024-05-05 02:30:00'),
       (18, '2024-06-06 04:40:00'),
       (19, '2024-07-07 09:15:00'),
       (20, '2024-08-08 10:50:00'),
       (21, '2024-09-09 15:00:00'),
       (22, '2024-10-10 17:30:00'),
       (23, '2024-11-11 19:45:00'),
       (24, '2024-12-12 21:20:00'),
       (25, '2025-01-01 23:10:00'),
       (26, '2025-02-02 01:25:00'),
       (27, '2025-03-03 03:30:00'),
       (28, '2025-04-04 05:40:00'),
       (29, '2025-05-05 10:15:00'),
       (30, '2025-06-06 11:50:00');

-- Inserir 10 itens de pedido (order items)
INSERT INTO tb_order_item (product_id, order_id, quantity, price)
VALUES (1, 1, 2, 1999.98),
       (2, 1, 1, 599.99),
       (3, 2, 3, 299.97),
       (4, 2, 1, 799.99),
       (5, 3, 1, 49.99),
       (6, 3, 2, 699.98),
       (7, 4, 1, 449.99),
       (8, 4, 1, 179.99),
       (9, 5, 4, 319.96),
       (10, 5, 1, 1299.99),
       (11, 6, 2, 159.98),
       (12, 6, 1, 149.99),
       (13, 7, 3, 899.97),
       (14, 7, 1, 79.99),
       (15, 8, 1, 29.99),
       (16, 8, 2, 79.98),
       (17, 9, 1, 399.99),
       (18, 9, 1, 59.99),
       (19, 10, 2, 159.98),
       (20, 10, 1, 349.99),
       (21, 11, 3, 194.97),
       (22, 11, 1, 129.99),
       (23, 12, 1, 199.99),
       (24, 12, 2, 59.98),
       (25, 13, 1, 109.99),
       (26, 13, 1, 499.99),
       (27, 14, 2, 159.98),
       (28, 14, 1, 799.99),
       (29, 15, 1, 19.99),
       (30, 15, 3, 149.97);

INSERT INTO tb_payment (order_id, payment_moment)
VALUES (1, '2023-01-02 11:00:00'),
       (2, '2023-02-03 12:30:00'),
       (3, '2023-03-04 14:45:00'),
       (4, '2023-04-05 16:20:00'),
       (5, '2023-05-06 18:10:00'),
       (6, '2023-06-07 20:25:00'),
       (7, '2023-07-08 22:30:00'),
       (8, '2023-08-09 00:40:00'),
       (9, '2023-09-10 09:15:00'),
       (10, '2023-10-11 10:50:00'),
       (11, '2023-11-12 13:00:00'),
       (12, '2023-12-13 14:30:00'),
       (13, '2024-01-14 16:45:00'),
       (14, '2024-02-15 18:20:00'),
       (15, '2024-03-16 20:10:00'),
       (16, '2024-04-17 22:25:00'),
       (17, '2024-05-18 00:30:00'),
       (18, '2024-06-19 08:40:00'),
       (19, '2024-07-20 09:15:00'),
       (20, '2024-08-21 10:50:00'),
       (21, '2024-09-22 13:00:00'),
       (22, '2024-10-23 14:30:00'),
       (23, '2024-11-24 16:45:00'),
       (24, '2024-12-25 18:20:00'),
       (25, '2025-01-26 20:10:00'),
       (26, '2025-02-27 22:25:00'),
       (27, '2025-03-28 00:30:00'),
       (28, '2025-04-29 08:40:00'),
       (29, '2025-05-30 09:15:00'),
       (30, '2025-06-01 10:50:00');

INSERT INTO OrderStatus (status)
VALUES ('WAITING_PAYMENT'),
       ('PAID'),
       ('SHIPPED'),
       ('DELIVERED');

SELECT *
FROM tb_user;
SELECT *
FROM tb_product;
SELECT *
FROM tb_category;
SELECT *
FROM tb_product_category;
SELECT *
FROM tb_order;
SELECT *
FROM tb_order_item;
SELECT *
FROM tb_payment;
SELECT *
FROM OrderStatus;