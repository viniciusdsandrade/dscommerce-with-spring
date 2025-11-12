-- Garante categorias básicas
INSERT INTO tb_category (id, name) VALUES (1, 'Informática');
INSERT INTO tb_category (id, name) VALUES (2, 'Acessórios');

-- Insere produto explicitamente com id=5
INSERT INTO tb_product (id, name, description, price, img_url)
VALUES (5, 'Mouse Óptico', 'Mouse básico para uso geral', 79.90, 'https://example.com/mouse-basic.jpg');

-- Relacionamento (produto 5 -> categoria 2)
INSERT INTO tb_product_category (product_id, category_id) VALUES (5, 2);
