package com.seuteste.sga.dao;

import com.seuteste.sga.model.Usuario;
import java.util.List;

/**
 * Interface DAO específica para a entidade Usuario.
 * Estende GenericDAO e adiciona métodos específicos para Usuario.
 * 
 * @author SGA Team
 * @version 1.0
 */
public interface UsuarioDAO extends GenericDAO<Usuario, Long> {

    /**
     * Busca um usuário pelo email.
     * 
     * @param email Email do usuário
     * @return Usuario encontrado ou null se não existir
     * @throws DAOException em caso de erro na operação
     */
    Usuario findByEmail(String email) throws DAOException;

    /**
     * Busca usuários cujo nome contenha o texto especificado (busca parcial).
     * 
     * @param nome Texto a ser buscado no nome
     * @return Lista de usuários encontrados
     * @throws DAOException em caso de erro na operação
     */
    List<Usuario> findByNomeContaining(String nome) throws DAOException;

    /**
     * Verifica se existe um usuário com o email especificado.
     * 
     * @param email Email do usuário
     * @return true se existir, false caso contrário
     * @throws DAOException em caso de erro na operação
     */
    boolean existsByEmail(String email) throws DAOException;

    /**
     * Lista todos os usuários ordenados por nome.
     * 
     * @return Lista de usuários ordenados por nome
     * @throws DAOException em caso de erro na operação
     */
    List<Usuario> findAllOrderByNome() throws DAOException;

    /**
     * Lista todos os usuários ordenados por data de cadastro.
     * 
     * @param ascending true para ordem crescente, false para decrescente
     * @return Lista de usuários ordenados por data de cadastro
     * @throws DAOException em caso de erro na operação
     */
    List<Usuario> findAllOrderByDataCadastro(boolean ascending) throws DAOException;

    /**
     * Autentica um usuário com email e senha.
     * 
     * @param email Email do usuário
     * @param senha Senha do usuário
     * @return Usuario autenticado ou null se credenciais inválidas
     * @throws DAOException em caso de erro na operação
     */
    Usuario authenticate(String email, String senha) throws DAOException;

    /**
     * Busca usuários com filtros combinados.
     * 
     * @param nome Nome ou parte do nome (pode ser null)
     * @param email Email ou parte do email (pode ser null)
     * @return Lista de usuários que atendem aos critérios
     * @throws DAOException em caso de erro na operação
     */
    List<Usuario> findWithFilters(String nome, String email) throws DAOException;
}

