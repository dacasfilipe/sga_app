-- Script para corrigir a estrutura da tabela usuario

-- Renomear coluna data_criacao para data_cadastro
ALTER TABLE usuario RENAME COLUMN data_criacao TO data_cadastro;

-- Verificar estrutura da tabela
\d usuario;

-- Verificar dados
SELECT id, email, nome, perfil, ativo, data_cadastro FROM usuario;
