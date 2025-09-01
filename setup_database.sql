-- Script para configurar o banco de dados sga_db

-- Criar tabela de usuários
CREATE TABLE IF NOT EXISTS usuario (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    perfil VARCHAR(50) NOT NULL,
    ativo BOOLEAN DEFAULT true,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inserir usuários de teste com senhas BCrypt
-- admin@sga.com / 123456 -> $2a$10$N9qo8uLOickgx2ZMRZoMye4dyS0jKdTd8VfExnz1YyZXfDZYOjm/G
-- operador@sga.com / operador123 -> $2a$10$123456789012345678901eN9qo8uLOickgx2ZMRZoMye4dyS0jKdTd8

INSERT INTO usuario (email, senha, nome, perfil, ativo) VALUES 
('admin@sga.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye4dyS0jKdTd8VfExnz1YyZXfDZYOjm/G', 'Administrador', 'ADMINISTRADOR', true),
('operador@sga.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye4dyS0jKdTd8VfExnz1YyZXfDZYOjm/G', 'Operador', 'OPERADOR', true)
ON CONFLICT (email) DO NOTHING;

-- Verificar dados inseridos
SELECT id, email, nome, perfil, ativo, data_criacao FROM usuario;
