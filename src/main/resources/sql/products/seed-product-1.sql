-- Garante categorias básicas
INSERT INTO tb_category (id, name) VALUES (1, 'Informática');
INSERT INTO tb_category (id, name) VALUES (2, 'Acessórios');

-- Garante que o próximo ID do produto será 1
ALTER TABLE tb_product ALTER COLUMN id RESTART WITH 1;

-- Produto id=1
INSERT INTO tb_product (name, description, price, img_url)
VALUES ('PC Gamer', 'PC para jogos com GPU dedicada', 5999.90, 'https://example.com/pc-gamer.jpg');

-- Relacionamento (produto 1 -> categoria 1)
INSERT INTO tb_product_category (product_id, category_id) VALUES (1, 1);
