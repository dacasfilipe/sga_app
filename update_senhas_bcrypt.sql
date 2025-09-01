-- Script para corrigir senhas com BCrypt
-- Senha admin: 123456 → Hash BCrypt válido
-- Senha operador: operador123 → Hash BCrypt válido

-- Atualizar senha do admin (123456) - hash BCrypt real
UPDATE usuario 
SET senha = '$2a$12$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPJSjtQm6' 
WHERE email = 'admin@sga.com';

-- Atualizar senha do operador (operador123) - hash BCrypt real
UPDATE usuario 
SET senha = '$2a$12$vfz6Nd3RLo9JjdLmHfQwFOrqBKxHw9Kkuz4xb4Ox.hJ0xFR8p9xTa'
WHERE email = 'operador@sga.com';

-- Verificar se as atualizações foram aplicadas
SELECT id, nome, email, perfil, ativo, 
       CASE 
           WHEN length(senha) > 50 THEN 'BCrypt Hash (OK)' 
           ELSE 'Senha simples (ERRO)' 
       END as status_senha
FROM usuario;
