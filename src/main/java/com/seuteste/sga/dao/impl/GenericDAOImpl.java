package com.seuteste.sga.dao.impl;

import com.seuteste.sga.dao.DAOException;
import com.seuteste.sga.dao.GenericDAO;
import com.seuteste.sga.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Implementação genérica das operações CRUD básicas.
 * Utiliza reflexão para determinar o tipo da entidade em tempo de execução.
 * 
 * @param <T> Tipo da entidade
 * @param <ID> Tipo do identificador da entidade
 * 
 * @author SGA Team
 * @version 1.0
 */
public abstract class GenericDAOImpl<T, ID> implements GenericDAO<T, ID> {

    protected Class<T> entityClass;

    /**
     * Construtor que determina o tipo da entidade através de reflexão.
     */
    @SuppressWarnings("unchecked")
    public GenericDAOImpl() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    @Override
    public T save(T entity) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            JPAUtil.rollback(em);
            throw new DAOException("Erro ao salvar entidade: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public T update(T entity) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            T updatedEntity = em.merge(entity);
            em.getTransaction().commit();
            return updatedEntity;
        } catch (Exception e) {
            JPAUtil.rollback(em);
            throw new DAOException("Erro ao atualizar entidade: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public void delete(ID id) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            JPAUtil.rollback(em);
            throw new DAOException("Erro ao deletar entidade com ID " + id + ": " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public void delete(T entity) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            T managedEntity = em.merge(entity);
            em.remove(managedEntity);
            em.getTransaction().commit();
        } catch (Exception e) {
            JPAUtil.rollback(em);
            throw new DAOException("Erro ao deletar entidade: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public T findById(ID id) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return em.find(entityClass, id);
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar entidade com ID " + id + ": " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<T> findAll() throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar todas as entidades: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public long count() throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new DAOException("Erro ao contar entidades: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public boolean exists(ID id) throws DAOException {
        return findById(id) != null;
    }

    /**
     * Método utilitário para criar consultas personalizadas.
     * 
     * @param jpql Consulta JPQL
     * @return Lista de entidades
     * @throws DAOException em caso de erro
     */
    protected List<T> executeQuery(String jpql) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao executar consulta: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    /**
     * Método utilitário para criar consultas personalizadas com parâmetros.
     * 
     * @param jpql Consulta JPQL
     * @param parameters Parâmetros da consulta (nome, valor)
     * @return Lista de entidades
     * @throws DAOException em caso de erro
     */
    protected List<T> executeQuery(String jpql, Object... parameters) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            
            // Define parâmetros (assumindo que são passados em pares: nome, valor)
            for (int i = 0; i < parameters.length; i += 2) {
                if (i + 1 < parameters.length) {
                    query.setParameter((String) parameters[i], parameters[i + 1]);
                }
            }
            
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao executar consulta com parâmetros: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
}

