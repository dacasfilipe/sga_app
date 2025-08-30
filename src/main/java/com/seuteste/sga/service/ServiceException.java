package com.seuteste.sga.service;

/**
 * Exceção específica para operações de serviço.
 * Encapsula erros que podem ocorrer durante operações de lógica de negócio.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class ServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor padrão.
     */
    public ServiceException() {
        super();
    }

    /**
     * Construtor com mensagem.
     * 
     * @param message Mensagem de erro
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Construtor com causa.
     * 
     * @param cause Causa da exceção
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * Construtor com mensagem e causa.
     * 
     * @param message Mensagem de erro
     * @param cause Causa da exceção
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

