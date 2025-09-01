package com.seuteste.sga.util;

/**
 * Utilitário simples para gerar hash de senha para inserção no banco
 */
public class GerarHashSenha {
    
    public static void main(String[] args) {
        try {
            String senhaAdmin = CriptografiaUtil.criptografarSenha("123456");
            String senhaOperador = CriptografiaUtil.criptografarSenha("operador123");
            
            System.out.println("Hash para senha 'admin123': " + senhaAdmin);
            System.out.println("Hash para senha 'operador123': " + senhaOperador);
            
            // SQL para inserir usuários
            System.out.println("\n-- SQL para inserir usuários:");
            System.out.println("INSERT INTO usuario (nome, email, senha, perfil, data_cadastro, ativo) VALUES");
            System.out.println("('Administrador', 'admin@sga.com', '" + senhaAdmin + "', 'ADMIN', CURRENT_DATE, true),");
            System.out.println("('Operador', 'operador@sga.com', '" + senhaOperador + "', 'OPERADOR', CURRENT_DATE, true);");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
