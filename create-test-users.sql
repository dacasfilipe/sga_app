INSERT INTO Usuario (id, nome, email, senha, perfil, data_criacao) VALUES 
(1, 'Administrador do Sistema', 'admin@sga.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.3xWy5oKgz4ZYF9LdUKgWwGKmwZc6Jm', 'ADMIN', NOW()),
(2, 'Operador do Sistema', 'operador@sga.com', '$2a$10$KIJzaZKhqGEQK6xo5R7oFebE9vMcJlUJWTJGc8V5ksm3vVzEzKzEG', 'OPERADOR', NOW());

-- Senha para admin@sga.com: 123456 (hash BCrypt)
-- Senha para operador@sga.com: operador123 (hash BCrypt)
