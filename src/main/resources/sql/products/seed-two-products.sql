-- Garante categorias básicas
INSERT INTO tb_category (id, name) VALUES (1, 'Informática');
INSERT INTO tb_category (id, name) VALUES (2, 'Acessórios');

-- Faz os próximos produtos iniciarem em 10 para bater com os asserts (10 e 11)
ALTER TABLE tb_product ALTER COLUMN id RESTART WITH 10;

-- Produtos id=10 e id=11
INSERT INTO tb_product (name, description, price, img_url)
VALUES ('PC Gamer',  'PC para jogos com GPU dedicada', 5999.90, 'https://example.com/pc-gamer.jpg');

INSERT INTO tb_product (name, description, price, img_url)
VALUES ('PC Office', 'PC para escritório e tarefas gerais', 2499.90, 'https://example.com/pc-office.jpg');

-- Relacionamentos
INSERT INTO tb_product_category (product_id, category_id) VALUES (10, 1);
INSERT INTO tb_product_category (product_id, category_id) VALUES (11, 1);
