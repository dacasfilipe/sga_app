package com.seuteste.sga.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Utilitário para gerar hashes BCrypt para senhas de teste
 */
public class GeradorHashBCrypt {
    
    public static void main(String[] args) {
        // Gerar hash para senha "123456" (admin)
        String senhaAdmin = "123456";
        String hashAdmin = BCrypt.withDefaults().hashToString(12, senhaAdmin.toCharArray());
        
        // Gerar hash para senha "operador123" (operador)
        String senhaOperador = "operador123";
        String hashOperador = BCrypt.withDefaults().hashToString(12, senhaOperador.toCharArray());
        
        System.out.println("=== HASHES BCRYPT GERADOS ===");
        System.out.println("Admin (senha: " + senhaAdmin + "):");
        System.out.println(hashAdmin);
        System.out.println();
        System.out.println("Operador (senha: " + senhaOperador + "):");
        System.out.println(hashOperador);
        System.out.println();
        
        // Testar verificação
        System.out.println("=== TESTES DE VERIFICAÇÃO ===");
        boolean testAdmin = BCrypt.verifyer().verify(senhaAdmin.toCharArray(), hashAdmin).verified;
        boolean testOperador = BCrypt.verifyer().verify(senhaOperador.toCharArray(), hashOperador).verified;
        
        System.out.println("Teste admin: " + testAdmin);
        System.out.println("Teste operador: " + testOperador);
        
        System.out.println();
        System.out.println("=== SQL PARA ATUALIZAÇÃO ===");
        System.out.println("UPDATE usuario SET senha = '" + hashAdmin + "' WHERE email = 'admin@sga.com';");
        System.out.println("UPDATE usuario SET senha = '" + hashOperador + "' WHERE email = 'operador@sga.com';");
    }
}
