package com.seuteste.sga.dao;

import com.seuteste.sga.model.ItemPedido;
import com.seuteste.sga.model.Pedido;
import com.seuteste.sga.model.Produto;

import java.util.List;

/**
 * Interface DAO para a entidade ItemPedido.
 * Estende GenericDAO para operações CRUD básicas.
 * 
 * @author SGA Team
 * @version 1.0
 */
public interface ItemPedidoDAO extends GenericDAO<ItemPedido, Long> {

    /**
     * Busca todos os itens de um pedido específico.
     * @param pedido Pedido ao qual os itens pertencem.
     * @return Lista de ItemPedido para o pedido dado.
     * @throws DAOException Em caso de erro no acesso a dados.
     */
    List<ItemPedido> findByPedido(Pedido pedido) throws DAOException;

    /**
     * Busca itens de pedido por produto.
     * @param produto Produto associado aos itens.
     * @return Lista de ItemPedido para o produto dado.
     * @throws DAOException Em caso de erro no acesso a dados.
     */
    List<ItemPedido> findByProduto(Produto produto) throws DAOException;
}

