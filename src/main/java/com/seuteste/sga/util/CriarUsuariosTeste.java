package com.seuteste.sga.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Utilitário para criar usuários de teste no banco de dados
 */
public class CriarUsuariosTeste {
    
    // Configurações de conexão AWS RDS
    private static final String DB_URL = "jdbc:postgresql://sga-database.c12m8qs64i3d.us-west-2.rds.amazonaws.com:5432/sga";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";
    
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            System.out.println("✅ Conectado ao banco de dados AWS RDS PostgreSQL");
            
            // Criar usuário Administrador
            String senhaAdmin = BCrypt.withDefaults().hashToString(10, "123456".toCharArray());
            criarUsuario(connection, 1L, "Administrador do Sistema", "admin@sga.com", senhaAdmin, "ADMIN");
            
            // Criar usuário Operador
            String senhaOperador = BCrypt.withDefaults().hashToString(10, "operador123".toCharArray());
            criarUsuario(connection, 2L, "Operador do Sistema", "operador@sga.com", senhaOperador, "OPERADOR");
            
            connection.close();
            System.out.println("✅ Usuários de teste criados com sucesso!");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao criar usuários: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void criarUsuario(Connection connection, Long id, String nome, String email, String senha, String perfil) throws SQLException {
        String sql = "INSERT INTO Usuario (id, nome, email, senha, perfil, data_criacao) VALUES (?, ?, ?, ?, ?::perfil_usuario, ?) " +
                    "ON CONFLICT (email) DO UPDATE SET " +
                    "nome = EXCLUDED.nome, " +
                    "senha = EXCLUDED.senha, " +
                    "perfil = EXCLUDED.perfil::perfil_usuario";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.setString(2, nome);
            stmt.setString(3, email);
            stmt.setString(4, senha);
            stmt.setString(5, perfil);
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            
            int resultado = stmt.executeUpdate();
            if (resultado > 0) {
                System.out.println("✅ Usuário criado/atualizado: " + email + " (" + perfil + ")");
            }
        }
    }
}
