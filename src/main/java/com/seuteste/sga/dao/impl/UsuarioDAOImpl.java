package com.seuteste.sga.dao.impl;

import com.seuteste.sga.dao.DAOException;
import com.seuteste.sga.dao.UsuarioDAO;
import com.seuteste.sga.model.Usuario;
import com.seuteste.sga.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do DAO para a entidade Usuario.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class UsuarioDAOImpl extends GenericDAOImpl<Usuario, Long> implements UsuarioDAO {

    @Override
    public Usuario findByEmail(String email) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT u FROM Usuario u WHERE u.email = :email";
            TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar usuário por email: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Usuario> findByNomeContaining(String nome) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(:nome)";
            TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
            query.setParameter("nome", "%" + nome + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar usuários por nome contendo: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public boolean existsByEmail(String email) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT COUNT(u) FROM Usuario u WHERE u.email = :email";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("email", email);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            throw new DAOException("Erro ao verificar existência de usuário por email: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Usuario> findAllOrderByNome() throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT u FROM Usuario u ORDER BY u.nome ASC";
            TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar usuários ordenados por nome: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Usuario> findAllOrderByDataCadastro(boolean ascending) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String order = ascending ? "ASC" : "DESC";
            String jpql = "SELECT u FROM Usuario u ORDER BY u.dataCadastro " + order;
            TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar usuários ordenados por data de cadastro: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public Usuario authenticate(String email, String senha) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT u FROM Usuario u WHERE u.email = :email AND u.senha = :senha";
            TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
            query.setParameter("email", email);
            query.setParameter("senha", senha);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new DAOException("Erro ao autenticar usuário: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Usuario> findWithFilters(String nome, String email) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            
            StringBuilder jpql = new StringBuilder("SELECT u FROM Usuario u WHERE 1=1");
            List<String> conditions = new ArrayList<>();
            
            if (nome != null && !nome.trim().isEmpty()) {
                conditions.add("LOWER(u.nome) LIKE LOWER(:nome)");
            }
            
            if (email != null && !email.trim().isEmpty()) {
                conditions.add("LOWER(u.email) LIKE LOWER(:email)");
            }
            
            for (String condition : conditions) {
                jpql.append(" AND ").append(condition);
            }
            
            jpql.append(" ORDER BY u.nome ASC");
            
            TypedQuery<Usuario> query = em.createQuery(jpql.toString(), Usuario.class);
            
            if (nome != null && !nome.trim().isEmpty()) {
                query.setParameter("nome", "%" + nome.trim() + "%");
            }
            
            if (email != null && !email.trim().isEmpty()) {
                query.setParameter("email", "%" + email.trim() + "%");
            }
            
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar usuários com filtros: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public Usuario save(Usuario entity) throws DAOException {
        // Verifica se já existe um usuário com o mesmo email
        if (entity.getEmail() != null && existsByEmail(entity.getEmail())) {
            throw new DAOException("Já existe um usuário cadastrado com este email.");
        }
        return super.save(entity);
    }

    @Override
    public Usuario update(Usuario entity) throws DAOException {
        // Verifica se existe outro usuário com o mesmo email
        if (entity.getEmail() != null) {
            Usuario existingUser = findByEmail(entity.getEmail());
            if (existingUser != null && !existingUser.getId().equals(entity.getId())) {
                throw new DAOException("Já existe outro usuário cadastrado com este email.");
            }
        }
        return super.update(entity);
    }
}

