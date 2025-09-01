-- Script para recriar a tabela usuario com estrutura correta

-- Remover tabela existente se houver
DROP TABLE IF EXISTS usuario CASCADE;

-- Criar tabela usuario com estrutura correta
CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    data_cadastro DATE NOT NULL DEFAULT CURRENT_DATE,
    perfil VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT true
);

-- Inserir usuários de teste com senhas BCrypt corretas
INSERT INTO usuario (nome, email, senha, perfil, ativo) VALUES 
('Administrador', 'admin@sga.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye4dyS0jKdTd8VfExnz1YyZXfDZYOjm/G', 'ADMINISTRADOR', true),
('Operador', 'operador@sga.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye4dyS0jKdTd8VfExnz1YyZXfDZYOjm/G', 'OPERADOR', true)
ON CONFLICT (email) DO NOTHING;

-- Verificar estrutura e dados
\d usuario;
SELECT id, nome, email, perfil, ativo, data_cadastro FROM usuario;
