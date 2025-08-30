package com.seuteste.sga.dao.impl;

import com.seuteste.sga.dao.DAOException;
import com.seuteste.sga.dao.ProdutoDAO;
import com.seuteste.sga.model.Categoria;
import com.seuteste.sga.model.Produto;
import com.seuteste.sga.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do DAO para a entidade Produto.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class ProdutoDAOImpl extends GenericDAOImpl<Produto, Long> implements ProdutoDAO {

    @Override
    public List<Produto> findByNomeContaining(String nome) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT p FROM Produto p WHERE LOWER(p.nome) LIKE LOWER(:nome)";
            TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
            query.setParameter("nome", "%" + nome + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar produtos por nome contendo: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Produto> findByCategoria(Categoria categoria) throws DAOException {
        if (categoria == null || categoria.getId() == null) {
            return new ArrayList<>();
        }
        return findByCategoriaId(categoria.getId());
    }

    @Override
    public List<Produto> findByCategoriaId(Long categoriaId) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT p FROM Produto p WHERE p.categoria.id = :categoriaId";
            TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
            query.setParameter("categoriaId", categoriaId);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar produtos por categoria: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Produto> findByPrecoRange(BigDecimal precoMin, BigDecimal precoMax) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT p FROM Produto p WHERE p.preco >= :precoMin AND p.preco <= :precoMax";
            TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
            query.setParameter("precoMin", precoMin);
            query.setParameter("precoMax", precoMax);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar produtos por faixa de preço: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Produto> findInStock() throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT p FROM Produto p WHERE p.quantidadeEstoque > 0";
            TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar produtos em estoque: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Produto> findOutOfStock() throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT p FROM Produto p WHERE p.quantidadeEstoque = 0";
            TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar produtos fora de estoque: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Produto> findLowStock(Integer limite) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT p FROM Produto p WHERE p.quantidadeEstoque <= :limite AND p.quantidadeEstoque > 0";
            TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
            query.setParameter("limite", limite);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar produtos com estoque baixo: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Produto> findAllOrderByNome() throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT p FROM Produto p ORDER BY p.nome ASC";
            TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar produtos ordenados por nome: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Produto> findAllOrderByPreco(boolean ascending) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String order = ascending ? "ASC" : "DESC";
            String jpql = "SELECT p FROM Produto p ORDER BY p.preco " + order;
            TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar produtos ordenados por preço: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Produto> findAllOrderByDataCadastro(boolean ascending) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String order = ascending ? "ASC" : "DESC";
            String jpql = "SELECT p FROM Produto p ORDER BY p.dataCadastro " + order;
            TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar produtos ordenados por data de cadastro: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Produto> findWithFilters(String nome, Long categoriaId, BigDecimal precoMin, 
                                        BigDecimal precoMax, boolean apenasEmEstoque) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            
            StringBuilder jpql = new StringBuilder("SELECT p FROM Produto p WHERE 1=1");
            List<String> conditions = new ArrayList<>();
            
            if (nome != null && !nome.trim().isEmpty()) {
                conditions.add("LOWER(p.nome) LIKE LOWER(:nome)");
            }
            
            if (categoriaId != null) {
                conditions.add("p.categoria.id = :categoriaId");
            }
            
            if (precoMin != null) {
                conditions.add("p.preco >= :precoMin");
            }
            
            if (precoMax != null) {
                conditions.add("p.preco <= :precoMax");
            }
            
            if (apenasEmEstoque) {
                conditions.add("p.quantidadeEstoque > 0");
            }
            
            for (String condition : conditions) {
                jpql.append(" AND ").append(condition);
            }
            
            jpql.append(" ORDER BY p.nome ASC");
            
            TypedQuery<Produto> query = em.createQuery(jpql.toString(), Produto.class);
            
            if (nome != null && !nome.trim().isEmpty()) {
                query.setParameter("nome", "%" + nome.trim() + "%");
            }
            
            if (categoriaId != null) {
                query.setParameter("categoriaId", categoriaId);
            }
            
            if (precoMin != null) {
                query.setParameter("precoMin", precoMin);
            }
            
            if (precoMax != null) {
                query.setParameter("precoMax", precoMax);
            }
            
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar produtos com filtros: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
}

