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
