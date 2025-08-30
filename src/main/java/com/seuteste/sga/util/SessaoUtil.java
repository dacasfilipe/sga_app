package com.seuteste.sga.util;

import com.seuteste.sga.model.Usuario;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Classe utilitária para gerenciamento de sessão de usuários.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class SessaoUtil {

    /**
     * Chave para armazenar o usuário logado na sessão.
     */
    private static final String USUARIO_LOGADO_KEY = "usuarioLogado";

    /**
     * Construtor privado para evitar instanciação da classe utilitária.
     */
    private SessaoUtil() {
        throw new UnsupportedOperationException("Esta é uma classe utilitária e não deve ser instanciada");
    }

    /**
     * Obtém a sessão HTTP atual.
     * 
     * @return A sessão HTTP atual ou null se não existir
     */
    public static HttpSession getSessaoAtual() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
            return request.getSession(false);
        }
        return null;
    }

    /**
     * Obtém a sessão HTTP atual, criando uma nova se não existir.
     * 
     * @return A sessão HTTP atual
     */
    public static HttpSession getSessaoAtualOuCriar() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
            return request.getSession(true);
        }
        return null;
    }

    /**
     * Armazena o usuário logado na sessão.
     * 
     * @param usuario O usuário a ser armazenado na sessão
     */
    public static void setUsuarioLogado(Usuario usuario) {
        HttpSession sessao = getSessaoAtualOuCriar();
        if (sessao != null) {
            sessao.setAttribute(USUARIO_LOGADO_KEY, usuario);
        }
    }

    /**
     * Obtém o usuário logado da sessão.
     * 
     * @return O usuário logado ou null se não houver usuário na sessão
     */
    public static Usuario getUsuarioLogado() {
        HttpSession sessao = getSessaoAtual();
        if (sessao != null) {
            return (Usuario) sessao.getAttribute(USUARIO_LOGADO_KEY);
        }
        return null;
    }

    /**
     * Verifica se há um usuário logado na sessão.
     * 
     * @return true se há um usuário logado, false caso contrário
     */
    public static boolean isUsuarioLogado() {
        return getUsuarioLogado() != null;
    }

    /**
     * Remove o usuário da sessão (logout).
     */
    public static void removerUsuarioLogado() {
        HttpSession sessao = getSessaoAtual();
        if (sessao != null) {
            sessao.removeAttribute(USUARIO_LOGADO_KEY);
        }
    }

    /**
     * Invalida a sessão atual (logout completo).
     */
    public static void invalidarSessao() {
        HttpSession sessao = getSessaoAtual();
        if (sessao != null) {
            sessao.invalidate();
        }
    }

    /**
     * Verifica se o usuário logado é administrador.
     * 
     * @return true se o usuário logado for administrador, false caso contrário
     */
    public static boolean isUsuarioAdmin() {
        Usuario usuario = getUsuarioLogado();
        return usuario != null && usuario.isAdmin();
    }

    /**
     * Verifica se o usuário logado é operador.
     * 
     * @return true se o usuário logado for operador, false caso contrário
     */
    public static boolean isUsuarioOperador() {
        Usuario usuario = getUsuarioLogado();
        return usuario != null && usuario.isOperador();
    }

    /**
     * Obtém o ID do usuário logado.
     * 
     * @return O ID do usuário logado ou null se não houver usuário logado
     */
    public static Long getIdUsuarioLogado() {
        Usuario usuario = getUsuarioLogado();
        return usuario != null ? usuario.getId() : null;
    }

    /**
     * Obtém o nome do usuário logado.
     * 
     * @return O nome do usuário logado ou string vazia se não houver usuário logado
     */
    public static String getNomeUsuarioLogado() {
        Usuario usuario = getUsuarioLogado();
        return usuario != null ? usuario.getNome() : "";
    }

    /**
     * Obtém o email do usuário logado.
     * 
     * @return O email do usuário logado ou string vazia se não houver usuário logado
     */
    public static String getEmailUsuarioLogado() {
        Usuario usuario = getUsuarioLogado();
        return usuario != null ? usuario.getEmail() : "";
    }

    /**
     * Obtém o perfil do usuário logado.
     * 
     * @return O perfil do usuário logado ou null se não houver usuário logado
     */
    public static Usuario.Perfil getPerfilUsuarioLogado() {
        Usuario usuario = getUsuarioLogado();
        return usuario != null ? usuario.getPerfil() : null;
    }

    /**
     * Atualiza os dados do usuário na sessão.
     * 
     * @param usuario O usuário com dados atualizados
     */
    public static void atualizarUsuarioLogado(Usuario usuario) {
        if (isUsuarioLogado() && usuario != null) {
            setUsuarioLogado(usuario);
        }
    }
}

