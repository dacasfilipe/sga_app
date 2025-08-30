package com.seuteste.sga.dao.impl;

import com.seuteste.sga.dao.ClienteDAO;
import com.seuteste.sga.dao.DAOException;
import com.seuteste.sga.model.Cliente;
import com.seuteste.sga.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Implementação do DAO para a entidade Cliente.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class ClienteDAOImpl extends GenericDAOImpl<Cliente, Long> implements ClienteDAO {

    @Override
    public Cliente findByEmail(String email) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT c FROM Cliente c WHERE c.email = :email";
            TypedQuery<Cliente> query = em.createQuery(jpql, Cliente.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar cliente por email: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public boolean existsByEmail(String email) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT COUNT(c) FROM Cliente c WHERE c.email = :email";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("email", email);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            throw new DAOException("Erro ao verificar existência de cliente por email: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Cliente> findAllOrderByNome() throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT c FROM Cliente c ORDER BY c.nome";
            TypedQuery<Cliente> query = em.createQuery(jpql, Cliente.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar clientes ordenados por nome: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Cliente> findByNomeContaining(String nome) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT c FROM Cliente c WHERE LOWER(c.nome) LIKE LOWER(:nome) ORDER BY c.nome";
            TypedQuery<Cliente> query = em.createQuery(jpql, Cliente.class);
            query.setParameter("nome", "%" + nome + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar clientes por nome: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
}

