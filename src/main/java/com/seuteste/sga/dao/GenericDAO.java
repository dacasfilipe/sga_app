package com.seuteste.sga.dao;

import java.util.List;

/**
 * Interface genérica para operações CRUD básicas.
 * Define os métodos padrão que devem ser implementados por todos os DAOs.
 * 
 * @param <T> Tipo da entidade
 * @param <ID> Tipo do identificador da entidade
 * 
 * @author SGA Team
 * @version 1.0
 */
public interface GenericDAO<T, ID> {

    /**
     * Salva uma nova entidade no banco de dados.
     * 
     * @param entity Entidade a ser salva
     * @return Entidade salva com ID gerado
     * @throws DAOException em caso de erro na operação
     */
    T save(T entity) throws DAOException;

    /**
     * Atualiza uma entidade existente no banco de dados.
     * 
     * @param entity Entidade a ser atualizada
     * @return Entidade atualizada
     * @throws DAOException em caso de erro na operação
     */
    T update(T entity) throws DAOException;

    /**
     * Remove uma entidade do banco de dados pelo ID.
     * 
     * @param id ID da entidade a ser removida
     * @throws DAOException em caso de erro na operação
     */
    void delete(ID id) throws DAOException;

    /**
     * Remove uma entidade do banco de dados.
     * 
     * @param entity Entidade a ser removida
     * @throws DAOException em caso de erro na operação
     */
    void delete(T entity) throws DAOException;

    /**
     * Busca uma entidade pelo ID.
     * 
     * @param id ID da entidade
     * @return Entidade encontrada ou null se não existir
     * @throws DAOException em caso de erro na operação
     */
    T findById(ID id) throws DAOException;

    /**
     * Lista todas as entidades.
     * 
     * @return Lista de todas as entidades
     * @throws DAOException em caso de erro na operação
     */
    List<T> findAll() throws DAOException;

    /**
     * Conta o número total de entidades.
     * 
     * @return Número total de entidades
     * @throws DAOException em caso de erro na operação
     */
    long count() throws DAOException;

    /**
     * Verifica se existe uma entidade com o ID especificado.
     * 
     * @param id ID da entidade
     * @return true se existir, false caso contrário
     * @throws DAOException em caso de erro na operação
     */
    boolean exists(ID id) throws DAOException;
}

