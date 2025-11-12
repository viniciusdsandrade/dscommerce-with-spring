-- Garante categorias básicas
INSERT INTO tb_category (id, name) VALUES (1, 'Informática');
INSERT INTO tb_category (id, name) VALUES (2, 'Acessórios');

-- Insere produto explicitamente com id=7 (para DELETE 204)
INSERT INTO tb_product (id, name, description, price, img_url)
VALUES (7, 'Teclado Membrana', 'Teclado silencioso ABNT2', 129.90, 'https://example.com/teclado.jpg');

-- Relacionamento (produto 7 -> categoria 2)
INSERT INTO tb_product_category (product_id, category_id) VALUES (7, 2);
