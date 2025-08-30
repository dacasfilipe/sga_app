package com.seuteste.sga.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Classe utilitária para gerenciar o EntityManager do JPA.
 * Implementa o padrão Singleton para garantir uma única instância do EntityManagerFactory.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME = "sgaPU";
    private static EntityManagerFactory entityManagerFactory;

    // Construtor privado para implementar Singleton
    private JPAUtil() {
    }

    /**
     * Obtém a instância do EntityManagerFactory.
     * Cria uma nova instância se ainda não existir.
     * 
     * @return EntityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            try {
                entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            } catch (Exception e) {
                System.err.println("Erro ao criar EntityManagerFactory: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Falha na inicialização do JPA", e);
            }
        }
        return entityManagerFactory;
    }

    /**
     * Cria um novo EntityManager.
     * 
     * @return EntityManager
     */
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /**
     * Fecha o EntityManagerFactory.
     * Deve ser chamado quando a aplicação for finalizada.
     */
    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    /**
     * Fecha um EntityManager de forma segura.
     * 
     * @param entityManager EntityManager a ser fechado
     */
    public static void closeEntityManager(EntityManager entityManager) {
        if (entityManager != null && entityManager.isOpen()) {
            try {
                entityManager.close();
            } catch (Exception e) {
                System.err.println("Erro ao fechar EntityManager: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Executa rollback em uma transação de forma segura.
     * 
     * @param entityManager EntityManager com transação ativa
     */
    public static void rollback(EntityManager entityManager) {
        if (entityManager != null && entityManager.getTransaction().isActive()) {
            try {
                entityManager.getTransaction().rollback();
            } catch (Exception e) {
                System.err.println("Erro ao fazer rollback da transação: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

