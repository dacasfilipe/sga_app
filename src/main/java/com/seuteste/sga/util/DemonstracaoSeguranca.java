package com.seuteste.sga.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Demonstração do sistema de autenticação BCrypt
 */
public class DemonstracaoSeguranca {
    
    public static void main(String[] args) {
        System.out.println("=== DEMONSTRAÇÃO DO SISTEMA DE SEGURANÇA ===\n");
        
        // Gerar hashes para as senhas de teste
        System.out.println("🔐 GERANDO HASHES BCRYPT:");
        
        String senhaAdmin = "123456";
        String hashAdmin = BCrypt.withDefaults().hashToString(10, senhaAdmin.toCharArray());
        System.out.println("Admin senha: " + senhaAdmin);
        System.out.println("Admin hash:  " + hashAdmin);
        
        String senhaOperador = "operador123";
        String hashOperador = BCrypt.withDefaults().hashToString(10, senhaOperador.toCharArray());
        System.out.println("\nOperador senha: " + senhaOperador);
        System.out.println("Operador hash:  " + hashOperador);
        
        // Testar verificação de senhas
        System.out.println("\n✅ TESTE DE VERIFICAÇÃO:");
        boolean adminVerificado = BCrypt.verifyer().verify(senhaAdmin.toCharArray(), hashAdmin).verified;
        boolean operadorVerificado = BCrypt.verifyer().verify(senhaOperador.toCharArray(), hashOperador).verified;
        
        System.out.println("Verificação Admin: " + (adminVerificado ? "✅ VÁLIDA" : "❌ INVÁLIDA"));
        System.out.println("Verificação Operador: " + (operadorVerificado ? "✅ VÁLIDA" : "❌ INVÁLIDA"));
        
        // Teste com senha incorreta
        boolean senhaIncorreta = BCrypt.verifyer().verify("senhaerrada".toCharArray(), hashAdmin).verified;
        System.out.println("Senha incorreta: " + (senhaIncorreta ? "✅ VÁLIDA" : "❌ INVÁLIDA"));
        
        System.out.println("\n📋 SQL PARA INSERIR USUÁRIOS:");
        System.out.println("INSERT INTO Usuario (id, nome, email, senha, perfil, data_criacao) VALUES");
        System.out.println("(1, 'Administrador do Sistema', 'admin@sga.com', '" + hashAdmin + "', 'ADMIN', NOW()),");
        System.out.println("(2, 'Operador do Sistema', 'operador@sga.com', '" + hashOperador + "', 'OPERADOR', NOW());");
        
        System.out.println("\n🛡️ SISTEMA DE SEGURANÇA IMPLEMENTADO:");
        System.out.println("• AuthenticationFilter protege rotas");
        System.out.println("• SessionController gerencia sessões");
        System.out.println("• MenuController menu dinâmico por permissão");
        System.out.println("• Template unificado com controle de acesso");
        System.out.println("• Interface de login com botões de teste");
        
        System.out.println("\n🔗 ESTRUTURA DE PERMISSÕES:");
        System.out.println("📋 PÁGINAS PÚBLICAS: /login.xhtml, /index.xhtml");
        System.out.println("🔧 SÓ ADMIN: /categorias.xhtml, /pedidos.xhtml");
        System.out.println("👨‍💼 OPERADOR E ADMIN: /produtos.xhtml, /clientes.xhtml");
    }
}
