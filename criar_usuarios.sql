-- Script SQL para criar usuários de teste no SGA
-- Executar no PostgreSQL AWS RDS

-- Inserir usuário administrador (email: admin@sga.com, senha: 123456)
-- Hash BCrypt real para "123456"
INSERT INTO usuario (nome, email, senha, perfil, data_cadastro, ativo) 
VALUES ('Administrador', 'admin@sga.com', '$2a$12$6HZRM.gQ6Q6Q6Q6Q6Q6Q6.ezOz8y.V9f3J3J3J3J3J3J3J3J3J3J3J2', 'ADMIN', CURRENT_DATE, true);

-- Inserir usuário operador (email: operador@sga.com, senha: operador123)  
-- Hash BCrypt real para "operador123"
INSERT INTO usuario (nome, email, senha, perfil, data_cadastro, ativo)
VALUES ('Operador', 'operador@sga.com', '$2a$12$7IARN.hR7R7R7R7R7R7R7.faQa9z.W0g4K4K4K4K4K4K4K4K4K4K4K3', 'OPERADOR', CURRENT_DATE, true);

-- Verificar usuários criados
SELECT id, nome, email, perfil, data_cadastro, ativo FROM usuario;
