package com.seuteste.sga.controller;

import com.seuteste.sga.util.SessaoUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Managed Bean para controlar a página principal (index.xhtml).
 * Verifica autenticação e redireciona se necessário.
 * 
 * @author SGA Team
 * @version 1.0
 */
@ManagedBean(name = "indexController")
@ViewScoped
public class IndexController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(IndexController.class.getName());

    @PostConstruct
    public void init() {
        try {
            // Verificar se usuário está logado
            if (!SessaoUtil.isUsuarioLogado()) {
                LOGGER.info("Usuário não logado tentando acessar página principal - redirecionando para login");
                redirecionarParaLogin();
                return;
            }
            
            LOGGER.info("IndexController inicializado - usuário logado: " + SessaoUtil.getNomeUsuarioLogado());
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar IndexController", e);
            redirecionarParaLogin();
        }
    }

    /**
     * Redireciona para a página de login.
     */
    private void redirecionarParaLogin() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao redirecionar para login", e);
        }
    }
}
