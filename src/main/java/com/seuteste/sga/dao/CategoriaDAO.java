package com.seuteste.sga.dao;

import com.seuteste.sga.model.Categoria;
import java.util.List;

/**
 * Interface DAO específica para a entidade Categoria.
 * Estende GenericDAO e adiciona métodos específicos para Categoria.
 * 
 * @author SGA Team
 * @version 1.0
 */
public interface CategoriaDAO extends GenericDAO<Categoria, Long> {

    /**
     * Busca uma categoria pelo nome.
     * 
     * @param nome Nome da categoria
     * @return Categoria encontrada ou null se não existir
     * @throws DAOException em caso de erro na operação
     */
    Categoria findByNome(String nome) throws DAOException;

    /**
     * Busca categorias cujo nome contenha o texto especificado (busca parcial).
     * 
     * @param nome Texto a ser buscado no nome
     * @return Lista de categorias encontradas
     * @throws DAOException em caso de erro na operação
     */
    List<Categoria> findByNomeContaining(String nome) throws DAOException;

    /**
     * Verifica se existe uma categoria com o nome especificado.
     * 
     * @param nome Nome da categoria
     * @return true se existir, false caso contrário
     * @throws DAOException em caso de erro na operação
     */
    boolean existsByNome(String nome) throws DAOException;

    /**
     * Lista todas as categorias ordenadas por nome.
     * 
     * @return Lista de categorias ordenadas por nome
     * @throws DAOException em caso de erro na operação
     */
    List<Categoria> findAllOrderByNome() throws DAOException;

    /**
     * Verifica se a categoria possui produtos associados.
     * 
     * @param categoriaId ID da categoria
     * @return true se possui produtos, false caso contrário
     * @throws DAOException em caso de erro na operação
     */
    boolean hasProducts(Long categoriaId) throws DAOException;
}

