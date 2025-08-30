package com.seuteste.sga.dao;

import com.seuteste.sga.model.Produto;
import com.seuteste.sga.model.Categoria;
import java.math.BigDecimal;
import java.util.List;

/**
 * Interface DAO específica para a entidade Produto.
 * Estende GenericDAO e adiciona métodos específicos para Produto.
 * 
 * @author SGA Team
 * @version 1.0
 */
public interface ProdutoDAO extends GenericDAO<Produto, Long> {

    /**
     * Busca produtos pelo nome (busca parcial).
     * 
     * @param nome Nome ou parte do nome do produto
     * @return Lista de produtos encontrados
     * @throws DAOException em caso de erro na operação
     */
    List<Produto> findByNomeContaining(String nome) throws DAOException;

    /**
     * Busca produtos por categoria.
     * 
     * @param categoria Categoria dos produtos
     * @return Lista de produtos da categoria
     * @throws DAOException em caso de erro na operação
     */
    List<Produto> findByCategoria(Categoria categoria) throws DAOException;

    /**
     * Busca produtos por ID da categoria.
     * 
     * @param categoriaId ID da categoria
     * @return Lista de produtos da categoria
     * @throws DAOException em caso de erro na operação
     */
    List<Produto> findByCategoriaId(Long categoriaId) throws DAOException;

    /**
     * Busca produtos com preço entre os valores especificados.
     * 
     * @param precoMin Preço mínimo
     * @param precoMax Preço máximo
     * @return Lista de produtos na faixa de preço
     * @throws DAOException em caso de erro na operação
     */
    List<Produto> findByPrecoRange(BigDecimal precoMin, BigDecimal precoMax) throws DAOException;

    /**
     * Busca produtos em estoque (quantidade > 0).
     * 
     * @return Lista de produtos em estoque
     * @throws DAOException em caso de erro na operação
     */
    List<Produto> findInStock() throws DAOException;

    /**
     * Busca produtos fora de estoque (quantidade = 0).
     * 
     * @return Lista de produtos fora de estoque
     * @throws DAOException em caso de erro na operação
     */
    List<Produto> findOutOfStock() throws DAOException;

    /**
     * Busca produtos com estoque baixo (quantidade <= limite).
     * 
     * @param limite Limite de estoque baixo
     * @return Lista de produtos com estoque baixo
     * @throws DAOException em caso de erro na operação
     */
    List<Produto> findLowStock(Integer limite) throws DAOException;

    /**
     * Lista todos os produtos ordenados por nome.
     * 
     * @return Lista de produtos ordenados por nome
     * @throws DAOException em caso de erro na operação
     */
    List<Produto> findAllOrderByNome() throws DAOException;

    /**
     * Lista todos os produtos ordenados por preço.
     * 
     * @param ascending true para ordem crescente, false para decrescente
     * @return Lista de produtos ordenados por preço
     * @throws DAOException em caso de erro na operação
     */
    List<Produto> findAllOrderByPreco(boolean ascending) throws DAOException;

    /**
     * Lista todos os produtos ordenados por data de cadastro.
     * 
     * @param ascending true para ordem crescente, false para decrescente
     * @return Lista de produtos ordenados por data de cadastro
     * @throws DAOException em caso de erro na operação
     */
    List<Produto> findAllOrderByDataCadastro(boolean ascending) throws DAOException;

    /**
     * Busca produtos com filtros combinados.
     * 
     * @param nome Nome ou parte do nome (pode ser null)
     * @param categoriaId ID da categoria (pode ser null)
     * @param precoMin Preço mínimo (pode ser null)
     * @param precoMax Preço máximo (pode ser null)
     * @param apenasEmEstoque true para buscar apenas produtos em estoque
     * @return Lista de produtos que atendem aos critérios
     * @throws DAOException em caso de erro na operação
     */
    List<Produto> findWithFilters(String nome, Long categoriaId, BigDecimal precoMin, 
                                 BigDecimal precoMax, boolean apenasEmEstoque) throws DAOException;
}

