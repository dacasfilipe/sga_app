package com.seuteste.sga;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Classe principal para executar o WAR em ambiente de desenvolvimento
 */
public class TestServer {
    public static void main(String[] args) {
        System.out.println("=== Sistema de Gerenciamento de Armazém ===");
        System.out.println("Para executar a aplicação, use um dos comandos:");
        System.out.println("===============================================");
        
        System.out.println("\n🚀 COMANDOS PARA EXECUTAR:");
        System.out.println("mvn clean compile");
        System.out.println("mvn tomcat7:run");
        System.out.println("\n📍 URL da aplicação: http://localhost:8080/sga");
        
        System.out.println("\n✅ FUNCIONALIDADES IMPLEMENTADAS:");
        System.out.println("🔐 Sistema de autenticação com BCrypt");
        System.out.println("👥 Controle de usuários (Admin/Operador)");
        System.out.println("🛡️ Filtro de segurança para rotas protegidas");
        System.out.println("📱 Interface responsiva com template unificado");
        System.out.println("🏷️ Menu dinâmico baseado em permissões");
        System.out.println("🎯 Dashboard personalizado por perfil");
        
        System.out.println("\n🔒 CONTROLE DE ACESSO:");
        System.out.println("👤 OPERADOR: Produtos ✅ | Clientes ✅ | Categorias ❌ | Pedidos ❌");
        System.out.println("🔧 ADMIN: Produtos ✅ | Clientes ✅ | Categorias ✅ | Pedidos ✅");
        
        System.out.println("\n🧪 CONTAS DE TESTE:");
        System.out.println("📧 admin@sga.com / senha: 123456 (Administrador)");
        System.out.println("📧 operador@sga.com / senha: operador123 (Operador)");
        
        System.out.println("\n📦 OUTRAS OPÇÕES:");
        System.out.println("• mvn clean package → Gera WAR em target/sga.war");
        System.out.println("• Deploy manual em Tomcat/GlassFish");
        System.out.println("🌐 Deploy pronto para AWS Elastic Beanstalk");
        
        System.out.println("\n⚠️  IMPORTANTE:");
        System.out.println("1. Certifique-se que o banco H2 está configurado");
        System.out.println("2. Execute na pasta raiz do projeto (onde está pom.xml)");
        System.out.println("3. Java 8+ e Maven devem estar instalados");
    }
}
