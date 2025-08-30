package com.seuteste.sga.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitário para gerenciamento de EntityManager em testes.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class TestJPAUtil {

    private static EntityManagerFactory emfH2;
    private static EntityManagerFactory emfPostgreSQL;
    private static final ThreadLocal<EntityManager> threadLocalEM = new ThreadLocal<>();

    /**
     * Obtém EntityManagerFactory para testes com H2.
     */
    public static EntityManagerFactory getEntityManagerFactoryH2() {
        if (emfH2 == null) {
            emfH2 = Persistence.createEntityManagerFactory("sgaTestPU");
        }
        return emfH2;
    }

    /**
     * Obtém EntityManagerFactory para testes de integração com PostgreSQL.
     * 
     * @param jdbcUrl URL de conexão JDBC
     * @param username Nome de usuário
     * @param password Senha
     * @return EntityManagerFactory configurado
     */
    public static EntityManagerFactory getEntityManagerFactoryPostgreSQL(
            String jdbcUrl, String username, String password) {
        
        if (emfPostgreSQL != null) {
            emfPostgreSQL.close();
        }
        
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
        properties.put("javax.persistence.jdbc.url", jdbcUrl);
        properties.put("javax.persistence.jdbc.user", username);
        properties.put("javax.persistence.jdbc.password", password);
        
        emfPostgreSQL = Persistence.createEntityManagerFactory("sgaIntegrationTestPU", properties);
        return emfPostgreSQL;
    }

    /**
     * Cria um novo EntityManager para testes com H2.
     */
    public static EntityManager createEntityManagerH2() {
        return getEntityManagerFactoryH2().createEntityManager();
    }

    /**
     * Cria um novo EntityManager para testes de integração.
     */
    public static EntityManager createEntityManagerPostgreSQL(
            String jdbcUrl, String username, String password) {
        return getEntityManagerFactoryPostgreSQL(jdbcUrl, username, password).createEntityManager();
    }

    /**
     * Obtém o EntityManager da thread atual.
     */
    public static EntityManager getCurrentEntityManager() {
        return threadLocalEM.get();
    }

    /**
     * Define o EntityManager para a thread atual.
     */
    public static void setCurrentEntityManager(EntityManager em) {
        threadLocalEM.set(em);
    }

    /**
     * Remove o EntityManager da thread atual.
     */
    public static void removeCurrentEntityManager() {
        EntityManager em = threadLocalEM.get();
        if (em != null) {
            if (em.isOpen()) {
                em.close();
            }
            threadLocalEM.remove();
        }
    }

    /**
     * Fecha todas as conexões e limpa recursos.
     */
    public static void closeAll() {
        removeCurrentEntityManager();
        
        if (emfH2 != null && emfH2.isOpen()) {
            emfH2.close();
            emfH2 = null;
        }
        
        if (emfPostgreSQL != null && emfPostgreSQL.isOpen()) {
            emfPostgreSQL.close();
            emfPostgreSQL = null;
        }
    }

    /**
     * Executa uma operação em uma transação.
     * 
     * @param operation Operação a ser executada
     */
    public static void executeInTransaction(Runnable operation) {
        EntityManager em = getCurrentEntityManager();
        if (em == null) {
            throw new IllegalStateException("EntityManager não foi configurado para a thread atual");
        }
        
        em.getTransaction().begin();
        try {
            operation.run();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Executa uma operação em uma transação e retorna um resultado.
     * 
     * @param operation Operação a ser executada
     * @param <T> Tipo do resultado
     * @return Resultado da operação
     */
    public static <T> T executeInTransaction(java.util.function.Supplier<T> operation) {
        EntityManager em = getCurrentEntityManager();
        if (em == null) {
            throw new IllegalStateException("EntityManager não foi configurado para a thread atual");
        }
        
        em.getTransaction().begin();
        try {
            T result = operation.get();
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
}

