package com.seuteste.sga.dao.impl;

import com.seuteste.sga.dao.DAOException;
import com.seuteste.sga.dao.ItemPedidoDAO;
import com.seuteste.sga.model.ItemPedido;
import com.seuteste.sga.model.Pedido;
import com.seuteste.sga.model.Produto;
import com.seuteste.sga.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Implementação do DAO para a entidade ItemPedido.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class ItemPedidoDAOImpl extends GenericDAOImpl<ItemPedido, Long> implements ItemPedidoDAO {

    @Override
    public List<ItemPedido> findByPedido(Pedido pedido) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT ip FROM ItemPedido ip WHERE ip.pedido = :pedido ORDER BY ip.id";
            TypedQuery<ItemPedido> query = em.createQuery(jpql, ItemPedido.class);
            query.setParameter("pedido", pedido);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar itens de pedido por pedido: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<ItemPedido> findByProduto(Produto produto) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT ip FROM ItemPedido ip WHERE ip.produto = :produto ORDER BY ip.id";
            TypedQuery<ItemPedido> query = em.createQuery(jpql, ItemPedido.class);
            query.setParameter("produto", produto);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar itens de pedido por produto: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
}

