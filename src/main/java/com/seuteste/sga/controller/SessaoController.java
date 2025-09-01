package com.seuteste.sga.controller;

import com.seuteste.sga.model.Usuario;
import com.seuteste.sga.util.SessaoUtil;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Controller para gerenciar a sessão do usuário e controle de acesso.
 */
@ManagedBean(name = "sessaoController")
@SessionScoped
public class SessaoController implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Obter o usuário logado da sessão.
     * @return Usuario logado ou null se não estiver logado
     */
    public Usuario getUsuarioLogado() {
        return SessaoUtil.getUsuarioLogado();
    }

    /**
     * Verificar se o usuário está logado.
     * @return true se estiver logado, false caso contrário
     */
    public boolean isLogado() {
        return getUsuarioLogado() != null;
    }

    /**
     * Verificar se o usuário é administrador.
     * @return true se for admin, false caso contrário
     */
    public boolean isAdmin() {
        Usuario usuario = getUsuarioLogado();
        return usuario != null && usuario.isAdmin();
    }

    /**
     * Verificar se o usuário é operador.
     * @return true se for operador, false caso contrário
     */
    public boolean isOperador() {
        Usuario usuario = getUsuarioLogado();
        return usuario != null && usuario.isOperador();
    }

    /**
     * Verificar se o usuário tem acesso a produtos.
     * @return true se tiver acesso (admin ou operador), false caso contrário
     */
    public boolean temAcessoProdutos() {
        Usuario usuario = getUsuarioLogado();
        return usuario != null && (usuario.isAdmin() || usuario.isOperador());
    }

    /**
     * Verificar se o usuário tem acesso a clientes.
     * @return true se tiver acesso (admin ou operador), false caso contrário
     */
    public boolean temAcessoClientes() {
        Usuario usuario = getUsuarioLogado();
        return usuario != null && (usuario.isAdmin() || usuario.isOperador());
    }

    /**
     * Verificar se o usuário tem acesso a categorias (apenas admin).
     * @return true se for admin, false caso contrário
     */
    public boolean temAcessoCategorias() {
        return isAdmin();
    }

    /**
     * Verificar se o usuário tem acesso a pedidos (apenas admin).
     * @return true se for admin, false caso contrário
     */
    public boolean temAcessoPedidos() {
        return isAdmin();
    }

    /**
     * Fazer logout do usuário.
     * @return String de redirecionamento para login
     */
    public String logout() {
        SessaoUtil.invalidarSessao();
        return "/login.xhtml?faces-redirect=true";
    }

    /**
     * Obter nome do usuário para exibição.
     * @return Nome do usuário ou "Visitante"
     */
    public String getNomeUsuario() {
        Usuario usuario = getUsuarioLogado();
        return usuario != null ? usuario.getPrimeiroNome() : "Visitante";
    }

    /**
     * Obter perfil do usuário para exibição.
     * @return Perfil do usuário ou "N/A"
     */
    public String getPerfilUsuario() {
        Usuario usuario = getUsuarioLogado();
        return usuario != null ? usuario.getPerfil().getDescricao() : "N/A";
    }
}
