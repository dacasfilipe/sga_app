package com.seuteste.sga.dao.impl;

import com.seuteste.sga.dao.DAOException;
import com.seuteste.sga.dao.PedidoDAO;
import com.seuteste.sga.model.Cliente;
import com.seuteste.sga.model.Pedido;
import com.seuteste.sga.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementação do DAO para a entidade Pedido.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class PedidoDAOImpl extends GenericDAOImpl<Pedido, Long> implements PedidoDAO {

    @Override
    public List<Pedido> findAllOrderByDataPedidoDesc() throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT p FROM Pedido p ORDER BY p.dataPedido DESC";
            TypedQuery<Pedido> query = em.createQuery(jpql, Pedido.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao listar pedidos ordenados por data: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Pedido> findByCliente(Cliente cliente) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT p FROM Pedido p WHERE p.cliente = :cliente ORDER BY p.dataPedido DESC";
            TypedQuery<Pedido> query = em.createQuery(jpql, Pedido.class);
            query.setParameter("cliente", cliente);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar pedidos por cliente: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Pedido> findByStatus(String status) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT p FROM Pedido p WHERE p.status = :status ORDER BY p.dataPedido DESC";
            TypedQuery<Pedido> query = em.createQuery(jpql, Pedido.class);
            query.setParameter("status", status);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar pedidos por status: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }

    @Override
    public List<Pedido> findByDataPedidoBetween(LocalDate dataInicio, LocalDate dataFim) throws DAOException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            String jpql = "SELECT p FROM Pedido p WHERE p.dataPedido BETWEEN :dataInicio AND :dataFim ORDER BY p.dataPedido DESC";
            TypedQuery<Pedido> query = em.createQuery(jpql, Pedido.class);
            query.setParameter("dataInicio", dataInicio);
            query.setParameter("dataFim", dataFim);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar pedidos por período: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
}

