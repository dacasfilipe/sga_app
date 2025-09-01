package com.seuteste.sga.controller;

import com.seuteste.sga.model.Usuario;
import com.seuteste.sga.service.AutenticacaoService;
import com.seuteste.sga.service.ServiceException;
import com.seuteste.sga.util.SessaoUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Managed Bean para controlar as operações de login e logout.
 * 
 * @author SGA Team
 * @version 1.0
 */
@ManagedBean(name = "loginController")
@ViewScoped
public class LoginController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    // Serviços
    private AutenticacaoService autenticacaoService;

    // Dados do formulário de login
    private String email;
    private String senha;
    private boolean lembrarMe;

    // Controle de estado
    private boolean carregandoLogin;
    private String mensagemErro;

    @PostConstruct
    public void init() {
        try {
            autenticacaoService = new AutenticacaoService();
            
            // Verificar se já está logado
            if (SessaoUtil.isUsuarioLogado()) {
                redirecionarParaPaginaPrincipal();
            }
            
            LOGGER.info("LoginController inicializado com sucesso");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar LoginController", e);
            adicionarMensagemErro("Erro ao inicializar página de login.");
        }
    }

    /**
     * Realiza o login do usuário.
     */
    public void login() {
        try {
            carregandoLogin = true;
            mensagemErro = null;

            // Validar e limpar campos
            String emailLimpo = (email != null) ? email.trim().toLowerCase() : "";
            String senhaLimpa = (senha != null) ? senha.trim() : "";

            LOGGER.info("Tentativa de login para email: " + emailLimpo);

            if (emailLimpo.isEmpty()) {
                mensagemErro = "Email é obrigatório.";
                adicionarMensagemErro(mensagemErro);
                return;
            }

            if (senhaLimpa.isEmpty()) {
                mensagemErro = "Senha é obrigatória.";
                adicionarMensagemErro(mensagemErro);
                return;
            }

            // Tentar autenticar
            Usuario usuario = autenticacaoService.autenticar(emailLimpo, senhaLimpa);

            if (usuario != null) {
                // Login bem-sucedido
                SessaoUtil.setUsuarioLogado(usuario);
                
                adicionarMensagemSucesso("Login realizado com sucesso! Bem-vindo, " + usuario.getPrimeiroNome() + "!");
                
                LOGGER.info("Login bem-sucedido para usuário: " + usuario.getNome());
                
                // Redirecionar para página principal
                redirecionarParaPaginaPrincipal();
                
            } else {
                // Credenciais inválidas
                mensagemErro = "Email ou senha incorretos.";
                adicionarMensagemErro(mensagemErro);
                
                LOGGER.warning("Tentativa de login falhada para email: " + emailLimpo);
            }

        } catch (ServiceException e) {
            mensagemErro = "Erro durante autenticação: " + e.getMessage();
            LOGGER.log(Level.WARNING, "Erro de serviço durante login", e);
            adicionarMensagemErro(mensagemErro);
            
        } catch (Exception e) {
            mensagemErro = "Erro inesperado durante login. Tente novamente.";
            LOGGER.log(Level.SEVERE, "Erro inesperado durante login", e);
            adicionarMensagemErro(mensagemErro);
            
        } finally {
            carregandoLogin = false;
            // Limpar senha por segurança
            senha = null;
        }
    }

    /**
     * Realiza o logout do usuário.
     */
    public void logout() {
        try {
            Usuario usuarioLogado = SessaoUtil.getUsuarioLogado();
            
            if (usuarioLogado != null) {
                LOGGER.info("Logout do usuário: " + usuarioLogado.getNome());
            }
            
            // Invalidar sessão
            SessaoUtil.invalidarSessao();
            
            // Redirecionar para página de login
            redirecionarParaLogin();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro durante logout", e);
            adicionarMensagemErro("Erro durante logout.");
        }
    }

    /**
     * Limpa os campos do formulário.
     */
    public void limparFormulario() {
        email = null;
        senha = null;
        lembrarMe = false;
        mensagemErro = null;
    }

    /**
     * Verifica se o usuário está logado.
     * 
     * @return true se estiver logado, false caso contrário
     */
    public boolean isUsuarioLogado() {
        return SessaoUtil.isUsuarioLogado();
    }

    /**
     * Obtém o nome do usuário logado.
     * 
     * @return Nome do usuário logado ou string vazia
     */
    public String getNomeUsuarioLogado() {
        return SessaoUtil.getNomeUsuarioLogado();
    }

    /**
     * Obtém o perfil do usuário logado.
     * 
     * @return Perfil do usuário logado ou null
     */
    public Usuario.Perfil getPerfilUsuarioLogado() {
        return SessaoUtil.getPerfilUsuarioLogado();
    }

    /**
     * Verifica se o usuário logado é administrador.
     * 
     * @return true se for admin, false caso contrário
     */
    public boolean isUsuarioAdmin() {
        return SessaoUtil.isUsuarioAdmin();
    }

    /**
     * Redireciona para a página principal do sistema.
     */
    private void redirecionarParaPaginaPrincipal() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao redirecionar para página principal", e);
            adicionarMensagemErro("Erro ao redirecionar para página principal.");
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

    /**
     * Adiciona uma mensagem de erro ao contexto JSF.
     * 
     * @param mensagem Mensagem de erro
     */
    private void adicionarMensagemErro(String mensagem) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", mensagem));
    }

    /**
     * Adiciona uma mensagem de sucesso ao contexto JSF.
     * 
     * @param mensagem Mensagem de sucesso
     */
    private void adicionarMensagemSucesso(String mensagem) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", mensagem));
    }

    /**
     * Adiciona uma mensagem de aviso ao contexto JSF.
     * 
     * @param mensagem Mensagem de aviso
     */
    private void adicionarMensagemAviso(String mensagem) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", mensagem));
    }

    // Getters e Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isLembrarMe() {
        return lembrarMe;
    }

    public void setLembrarMe(boolean lembrarMe) {
        this.lembrarMe = lembrarMe;
    }

    public boolean isCarregandoLogin() {
        return carregandoLogin;
    }

    public void setCarregandoLogin(boolean carregandoLogin) {
        this.carregandoLogin = carregandoLogin;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }
}

