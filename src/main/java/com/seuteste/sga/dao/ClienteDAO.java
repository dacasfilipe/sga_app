package com.seuteste.sga.dao;

import com.seuteste.sga.model.Cliente;

import java.util.List;

/**
 * Interface DAO para a entidade Cliente.
 * Estende GenericDAO para operações CRUD básicas.
 * 
 * @author SGA Team
 * @version 1.0
 */
public interface ClienteDAO extends GenericDAO<Cliente, Long> {
    
    /**
     * Busca um cliente pelo email.
     * @param email Email do cliente.
     * @return Cliente encontrado ou null se não existir.
     * @throws DAOException Em caso de erro no acesso a dados.
     */
    Cliente findByEmail(String email) throws DAOException;

    /**
     * Verifica se um cliente com o email especificado já existe.
     * @param email Email a ser verificado.
     * @return true se o email já existe, false caso contrário.
     * @throws DAOException Em caso de erro no acesso a dados.
     */
    boolean existsByEmail(String email) throws DAOException;

    /**
     * Lista todos os clientes ordenados pelo nome.
     * @return Lista de clientes.
     * @throws DAOException Em caso de erro no acesso a dados.
     */
    List<Cliente> findAllOrderByNome() throws DAOException;

    /**
     * Busca clientes cujo nome contenha a string especificada.
     * @param nome String a ser buscada no nome do cliente.
     * @return Lista de clientes que correspondem ao critério.
     * @throws DAOException Em caso de erro no acesso a dados.
     */
    List<Cliente> findByNomeContaining(String nome) throws DAOException;
}

