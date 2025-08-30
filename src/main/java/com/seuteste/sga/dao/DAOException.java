package com.seuteste.sga.dao;

/**
 * Exceção específica para operações de DAO.
 * Encapsula erros que podem ocorrer durante operações de acesso a dados.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class DAOException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor padrão.
     */
    public DAOException() {
        super();
    }

    /**
     * Construtor com mensagem.
     * 
     * @param message Mensagem de erro
     */
    public DAOException(String message) {
        super(message);
    }

    /**
     * Construtor com causa.
     * 
     * @param cause Causa da exceção
     */
    public DAOException(Throwable cause) {
        super(cause);
    }

    /**
     * Construtor com mensagem e causa.
     * 
     * @param message Mensagem de erro
     * @param cause Causa da exceção
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}

