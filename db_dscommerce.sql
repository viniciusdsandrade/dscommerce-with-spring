DROP DATABASE IF EXISTS db_dscommerce;
CREATE DATABASE IF NOT EXISTS db_dscommerce;
USE db_dscommerce;

-- Consulta para obter informações do usuário e suas permissões
SELECT tb_user.user_email AS username, tb_user.user_password, tb_role.id AS roleId, tb_role.authority
FROM tb_user
         INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
         INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
WHERE tb_user.user_email = 'viniciusdsandrade0663@gmail.com';


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
    id             BIGINT UNSIGNED AUTO_INCREMENT,
    payment_moment TIMESTAMP       NOT NULL,
    order_id       BIGINT UNSIGNED NOT NULL,

    PRIMARY KEY (id),

    FOREIGN KEY (order_id) REFERENCES tb_order (id)
);

CREATE TABLE IF NOT EXISTS OrderStatus
(
    id     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    status VARCHAR(255)    NOT NULL,

    PRIMARY KEY (id)
);

INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('Vinícius', 'vinicius_andrade2011@hotmail.com', '$2a$10$9S/26SgTEPlofyZRgGcxGOauHI5Fp/JfT7Q25kkE2VYQoj08oilHa','2001-12-06', '(19) 974133884)');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('Vinícius', 'viniciusdsandrade0663@gmail.com', '$2a$10$9S/26SgTEPlofyZRgGcxGOauHI5Fp/JfT7Q25kkE2VYQoj08oilHa','2001-12-06', '(19) 974133884)');

INSERT INTO tb_role(authority) VALUES ('ROLE_OPERATOR');
INSERT INTO tb_role(authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_user_role(user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role(user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role(user_id, role_id) VALUES (2, 2);

INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User1', 'user1@example.com', 'password1', '1990-01-01', '1234567890');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User2', 'user2@example.com', 'password2', '1991-02-02', '2345678901');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User3', 'user3@example.com', 'password3', '1992-03-03', '3456789012');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User4', 'user4@example.com', 'password4', '1993-04-04', '4567890123');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User5', 'user5@example.com', 'password5', '1994-05-05', '5678901234');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User6', 'user6@example.com', 'password6', '1995-06-06', '6789012345');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User7', 'user7@example.com', 'password7', '1996-07-07', '7890123456');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User8', 'user8@example.com', 'password8', '1997-08-08', '8901234567');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User9', 'user9@example.com', 'password9', '1998-09-09', '9012345678');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User10', 'user10@example.com', 'password10', '1999-10-10', '0123456789');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User11', 'user11@example.com', 'password11', '2000-11-11', '1234567890');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User12', 'user12@example.com', 'password12', '2001-12-12', '2345678901');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User13', 'user13@example.com', 'password13', '2002-01-13', '3456789012');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User14', 'user14@example.com', 'password14', '2003-02-14', '4567890123');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User15', 'user15@example.com', 'password15', '2004-03-15', '5678901234');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User16', 'user16@example.com', 'password16', '2005-04-16', '6789012345');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User17', 'user17@example.com', 'password17', '2006-05-17', '7890123456');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User18', 'user18@example.com', 'password18', '2007-06-18', '8901234567');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User19', 'user19@example.com', 'password19', '2008-07-19', '9012345678');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User20', 'user20@example.com', 'password20', '2009-08-20', '0123456789');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User21', 'user21@example.com', 'password21', '2010-09-21', '1234567890');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User22', 'user22@example.com', 'password22', '2011-10-22', '2345678901');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User23', 'user23@example.com', 'password23', '2012-11-23', '3456789012');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User24', 'user24@example.com', 'password24', '2013-12-24', '4567890123');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User25', 'user25@example.com', 'password25', '2014-01-25', '5678901234');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User26', 'user26@example.com', 'password26', '2015-02-26', '6789012345');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User27', 'user27@example.com', 'password27', '2016-03-27', '7890123456');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User28', 'user28@example.com', 'password28', '2017-04-28', '8901234567');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User29', 'user29@example.com', 'password29', '2018-05-29', '9012345678');
INSERT INTO tb_user (user_name, user_email, user_password, user_birth_date, user_phone) VALUES ('User30', 'user30@example.com', 'password30', '2019-06-30', '0123456789');

INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Laptop', 'Powerful laptop with high performance', 999.99, 'laptop.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Smartphone', 'Latest smartphone with advanced features', 599.99, 'smartphone.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Headphones', 'High-quality over-ear headphones', 99.99, 'headphones.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Camera', 'Professional camera for photography enthusiasts', 799.99, 'camera.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Fitness Tracker', 'Track your fitness activities with this wearable', 49.99, 'fitness_tracker.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Tablet', 'Portable tablet for productivity on the go', 349.99, 'tablet.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Gaming Console', 'Next-gen gaming console for immersive gaming', 449.99, 'gaming_console.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Smart Watch', 'Stay connected with this stylish smartwatch', 179.99, 'smart_watch.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Bluetooth Speaker', 'Wireless Bluetooth speaker for music lovers', 79.99, 'bluetooth_speaker.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('4K TV', 'Ultra HD 4K television for an amazing viewing experience', 1299.99, '4k_tv.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Coffee Maker', 'Automatic coffee maker for your daily brew', 79.99, 'coffee_maker.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Camping Tent', 'Spacious tent for outdoor adventures', 149.99, 'camping_tent.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Smart Thermostat', 'Programmable smart thermostat for home comfort', 129.99, 'smart_thermostat.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Wireless Mouse', 'Ergonomic wireless mouse for efficient computing', 29.99, 'wireless_mouse.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('LED Desk Lamp', 'Adjustable LED desk lamp for workspace illumination', 39.99, 'led_desk_lamp.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Designer Handbag', 'Stylish designer handbag for a fashionable look', 199.99, 'designer_handbag.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Best-Selling Novel', 'A captivating novel by a renowned author', 14.99, 'best_selling_novel.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Blender', 'High-performance blender for smoothies and more', 89.99, 'blender.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Travel Backpack', 'Durable travel backpack for your adventures', 59.99, 'travel_backpack.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('HD Projector', 'Home theater HD projector for cinematic experiences', 399.99, 'hd_projector.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Skincare Set', 'Complete skincare set for radiant skin', 49.99, 'skincare_set.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Board Game', 'Classic board game for family fun', 24.99, 'board_game.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Pet Bed', 'Comfortable pet bed for your furry friend', 34.99, 'pet_bed.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Acoustic Guitar', 'Quality acoustic guitar for music enthusiasts', 299.99, 'acoustic_guitar.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Car Phone Holder', 'Convenient car phone holder for hands-free driving', 9.99, 'car_phone_holder.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Tool Set', 'Comprehensive tool set for DIY projects', 69.99, 'tool_set.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Diamond Necklace', 'Elegant diamond necklace for special occasions', 499.99, 'diamond_necklace.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Running Shoes', 'High-performance running shoes for fitness enthusiasts', 79.99, 'running_shoes.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Kids Toy Set', 'Colorful toy set for kids\' playtime', 19.99, 'kids_toy_set.jpg');
INSERT INTO tb_product (product_name, product_description, product_price, product_img_url) VALUES ('Canvas Wall Art', 'Beautiful canvas wall art for home decor', 59.99, 'canvas_wall_art.jpg');


INSERT INTO tb_category (category_name) VALUES ('Electronics');
INSERT INTO tb_category (category_name) VALUES ('Mobile Devices');
INSERT INTO tb_category (category_name) VALUES ('Audio');
INSERT INTO tb_category (category_name) VALUES ('Photography');
INSERT INTO tb_category (category_name) VALUES ('Fitness');
INSERT INTO tb_category (category_name) VALUES ('Tablets');
INSERT INTO tb_category (category_name) VALUES ('Gaming');
INSERT INTO tb_category (category_name) VALUES ('Wearables');
INSERT INTO tb_category (category_name) VALUES ('Audio Accessories');
INSERT INTO tb_category (category_name) VALUES ('Televisions');
INSERT INTO tb_category (category_name) VALUES ('Home Appliances');
INSERT INTO tb_category (category_name) VALUES ('Outdoor Gear');
INSERT INTO tb_category (category_name) VALUES ('Smart Home');
INSERT INTO tb_category (category_name) VALUES ('Office Electronics');
INSERT INTO tb_category (category_name) VALUES ('Smart Lighting');
INSERT INTO tb_category (category_name) VALUES ('Fashion');
INSERT INTO tb_category (category_name) VALUES ('Books');
INSERT INTO tb_category (category_name) VALUES ('Kitchen Appliances');
INSERT INTO tb_category (category_name) VALUES ('Travel Accessories');
INSERT INTO tb_category (category_name) VALUES ('Furniture');
INSERT INTO tb_category (category_name) VALUES ('Health & Beauty');
INSERT INTO tb_category (category_name) VALUES ('Toys & Games');
INSERT INTO tb_category (category_name) VALUES ('Pet Supplies');
INSERT INTO tb_category (category_name) VALUES ('Musical Instruments');
INSERT INTO tb_category (category_name) VALUES ('Car Accessories');
INSERT INTO tb_category (category_name) VALUES ('Tools & Hardware');
INSERT INTO tb_category (category_name) VALUES ('Jewelry');
INSERT INTO tb_category (category_name) VALUES ('Sports & Outdoors');
INSERT INTO tb_category (category_name) VALUES ('Baby & Kids');
INSERT INTO tb_category (category_name) VALUES ('Home Decor');

INSERT INTO tb_product_category (category_id, product_id) VALUES (1, 1);
INSERT INTO tb_product_category (category_id, product_id) VALUES (2, 1);
INSERT INTO tb_product_category (category_id, product_id) VALUES (1, 2);
INSERT INTO tb_product_category (category_id, product_id) VALUES (2, 2);
INSERT INTO tb_product_category (category_id, product_id) VALUES (3, 3);
INSERT INTO tb_product_category (category_id, product_id) VALUES (4, 4);
INSERT INTO tb_product_category (category_id, product_id) VALUES (5, 5);
INSERT INTO tb_product_category (category_id, product_id) VALUES (1, 6);
INSERT INTO tb_product_category (category_id, product_id) VALUES (7, 7);
INSERT INTO tb_product_category (category_id, product_id) VALUES (8, 8);
INSERT INTO tb_product_category (category_id, product_id) VALUES (9, 9);
INSERT INTO tb_product_category (category_id, product_id) VALUES (10, 10);
INSERT INTO tb_product_category (category_id, product_id) VALUES (11, 11);
INSERT INTO tb_product_category (category_id, product_id) VALUES (12, 12);
INSERT INTO tb_product_category (category_id, product_id) VALUES (13, 13);
INSERT INTO tb_product_category (category_id, product_id) VALUES (14, 14);
INSERT INTO tb_product_category (category_id, product_id) VALUES (15, 15);
INSERT INTO tb_product_category (category_id, product_id) VALUES (16, 16);
INSERT INTO tb_product_category (category_id, product_id) VALUES (17, 17);
INSERT INTO tb_product_category (category_id, product_id) VALUES (18, 18);
INSERT INTO tb_product_category (category_id, product_id) VALUES (19, 19);
INSERT INTO tb_product_category (category_id, product_id) VALUES (20, 20);
INSERT INTO tb_product_category (category_id, product_id) VALUES (21, 21);
INSERT INTO tb_product_category (category_id, product_id) VALUES (22, 22);
INSERT INTO tb_product_category (category_id, product_id) VALUES (23, 23);
INSERT INTO tb_product_category (category_id, product_id) VALUES (24, 24);
INSERT INTO tb_product_category (category_id, product_id) VALUES (25, 25);
INSERT INTO tb_product_category (category_id, product_id) VALUES (26, 26);
INSERT INTO tb_product_category (category_id, product_id) VALUES (27, 27);
INSERT INTO tb_product_category (category_id, product_id) VALUES (28, 28);
INSERT INTO tb_product_category (category_id, product_id) VALUES (29, 29);
INSERT INTO tb_product_category (category_id, product_id) VALUES (30, 30);

INSERT INTO tb_order (client_id, order_moment) VALUES (1, '2023-01-01 10:00:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (2, '2023-02-02 11:30:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (3, '2023-03-03 13:45:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (4, '2023-04-04 15:20:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (5, '2023-05-05 17:10:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (6, '2023-06-06 19:25:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (7, '2023-07-07 21:30:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (8, '2023-08-08 23:40:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (9, '2023-09-09 08:15:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (10, '2023-10-10 09:50:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (11, '2023-11-11 14:00:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (12, '2023-12-12 16:30:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (13, '2024-01-01 18:45:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (14, '2024-02-02 20:20:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (15, '2024-03-03 22:10:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (16, '2024-04-04 00:25:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (17, '2024-05-05 02:30:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (18, '2024-06-06 04:40:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (19, '2024-07-07 09:15:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (20, '2024-08-08 10:50:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (21, '2024-09-09 15:00:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (22, '2024-10-10 17:30:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (23, '2024-11-11 19:45:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (24, '2024-12-12 21:20:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (25, '2025-01-01 23:10:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (26, '2025-02-02 01:25:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (27, '2025-03-03 03:30:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (28, '2025-04-04 05:40:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (29, '2025-05-05 10:15:00');
INSERT INTO tb_order (client_id, order_moment) VALUES (30, '2025-06-06 11:50:00');

INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (1, 1, 2, 1999.98);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (2, 1, 1, 599.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (3, 2, 3, 299.97);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (4, 2, 1, 799.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (5, 3, 1, 49.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (6, 3, 2, 699.98);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (7, 4, 1, 449.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (8, 4, 1, 179.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (9, 5, 4, 319.96);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (10, 5, 1, 1299.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (11, 6, 2, 159.98);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (12, 6, 1, 149.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (13, 7, 3, 899.97);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (14, 7, 1, 79.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (15, 8, 1, 29.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (16, 8, 2, 79.98);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (17, 9, 1, 399.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (18, 9, 1, 59.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (19, 10, 2, 159.98);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (20, 10, 1, 349.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (21, 11, 3, 194.97);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (22, 11, 1, 129.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (23, 12, 1, 199.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (24, 12, 2, 59.98);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (25, 13, 1, 109.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (26, 13, 1, 499.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (27, 14, 2, 159.98);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (28, 14, 1, 799.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (29, 15, 1, 19.99);
INSERT INTO tb_order_item (product_id, order_id, quantity, price) VALUES (30, 15, 3, 149.97);

INSERT INTO tb_payment (order_id, payment_moment) VALUES (1, '2023-01-02 11:00:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (2, '2023-02-03 12:30:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (3, '2023-03-04 14:45:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (4, '2023-04-05 16:20:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (5, '2023-05-06 18:10:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (6, '2023-06-07 20:25:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (7, '2023-07-08 22:30:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (8, '2023-08-09 00:40:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (9, '2023-09-10 09:15:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (10, '2023-10-11 10:50:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (11, '2023-11-12 13:00:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (12, '2023-12-13 14:30:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (13, '2024-01-14 16:45:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (14, '2024-02-15 18:20:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (15, '2024-03-16 20:10:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (16, '2024-04-17 22:25:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (17, '2024-05-18 00:30:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (18, '2024-06-19 08:40:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (19, '2024-07-20 09:15:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (20, '2024-08-21 10:50:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (21, '2024-09-22 13:00:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (22, '2024-10-23 14:30:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (23, '2024-11-24 16:45:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (24, '2024-12-25 18:20:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (25, '2025-01-26 20:10:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (26, '2025-02-27 22:25:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (27, '2025-03-28 00:30:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (28, '2025-04-29 08:40:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (29, '2025-05-30 09:15:00');
INSERT INTO tb_payment (order_id, payment_moment) VALUES (30, '2025-06-01 10:50:00');

INSERT INTO OrderStatus (status) VALUES ('WAITING_PAYMENT');
INSERT INTO OrderStatus (status) VALUES ('PAID');
INSERT INTO OrderStatus (status) VALUES ('SHIPPED');
INSERT INTO OrderStatus (status) VALUES ('DELIVERED');

SELECT * FROM tb_user;
SELECT * FROM tb_product;
SELECT * FROM tb_category;
SELECT * FROM tb_product_category;
SELECT * FROM tb_order;
SELECT * FROM tb_order_item;
SELECT * FROM tb_payment;
SELECT * FROM OrderStatus;