-- /sql/products/seed-categories-basic.sql
-- Categorias mínimas para testes de produto
INSERT INTO tb_category (id, name) VALUES (1, 'Informática');
INSERT INTO tb_category (id, name) VALUES (2, 'Acessórios');

-- Opcional: se quiser que próximos INSERTs usem IDs > 2
-- ALTER TABLE tb_category ALTER COLUMN id RESTART WITH 3;
