package com.seuteste.sga.dao;

import com.seuteste.sga.model.Pedido;
import com.seuteste.sga.model.Cliente;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface DAO para a entidade Pedido.
 * Estende GenericDAO para operações CRUD básicas.
 * 
 * @author SGA Team
 * @version 1.0
 */
public interface PedidoDAO extends GenericDAO<Pedido, Long> {

    /**
     * Lista todos os pedidos ordenados pela data do pedido (mais recente primeiro).
     * @return Lista de pedidos.
     * @throws DAOException Em caso de erro no acesso a dados.
     */
    List<Pedido> findAllOrderByDataPedidoDesc() throws DAOException;

    /**
     * Busca pedidos por cliente.
     * @param cliente Cliente para buscar os pedidos.
     * @return Lista de pedidos do cliente.
     * @throws DAOException Em caso de erro no acesso a dados.
     */
    List<Pedido> findByCliente(Cliente cliente) throws DAOException;

    /**
     * Busca pedidos por status.
     * @param status Status do pedido.
     * @return Lista de pedidos com o status especificado.
     * @throws DAOException Em caso de erro no acesso a dados.
     */
    List<Pedido> findByStatus(String status) throws DAOException;

    /**
     * Busca pedidos dentro de um período de datas.
     * @param dataInicio Data de início do período.
     * @param dataFim Data de fim do período.
     * @return Lista de pedidos dentro do período.
     * @throws DAOException Em caso de erro no acesso a dados.
     */
    List<Pedido> findByDataPedidoBetween(LocalDate dataInicio, LocalDate dataFim) throws DAOException;
}

