package com.seuteste.sga.dao.impl;

import com.seuteste.sga.dao.CategoriaDAO;
import com.seuteste.sga.dao.DAOException;
import com.seuteste.sga.model.Categoria;
import com.seuteste.sga.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Implementação do DAO para a entidade Categoria.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class CategoriaDAOImpl extends GenericDAOImpl<Categoria, Long> implements CategoriaDAO {

    @Override
    public Categoria findByNome(String nome) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT c FROM Categoria c WHERE c.nome = :nome";
            TypedQuery<Categoria> query = em.createQuery(jpql, Categoria.class);
            query.setParameter("nome", nome);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar categoria por nome: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Categoria> findByNomeContaining(String nome) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT c FROM Categoria c WHERE LOWER(c.nome) LIKE LOWER(:nome)";
            TypedQuery<Categoria> query = em.createQuery(jpql, Categoria.class);
            query.setParameter("nome", "%" + nome + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar categorias por nome contendo: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public boolean existsByNome(String nome) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT COUNT(c) FROM Categoria c WHERE c.nome = :nome";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("nome", nome);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            throw new DAOException("Erro ao verificar existência de categoria por nome: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Categoria> findAllOrderByNome() throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT c FROM Categoria c ORDER BY c.nome ASC";
            TypedQuery<Categoria> query = em.createQuery(jpql, Categoria.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar categorias ordenadas por nome: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public boolean hasProducts(Long categoriaId) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT COUNT(p) FROM Produto p WHERE p.categoria.id = :categoriaId";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("categoriaId", categoriaId);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            throw new DAOException("Erro ao verificar se categoria possui produtos: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

}

