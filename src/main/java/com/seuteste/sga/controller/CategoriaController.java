package com.seuteste.sga.controller;

import com.seuteste.sga.model.Categoria;
import com.seuteste.sga.service.CategoriaService;
import com.seuteste.sga.service.ServiceException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Managed Bean para controlar as operações relacionadas à entidade Categoria.
 * Atua como intermediário entre a camada de apresentação (JSF) e a camada de serviço.
 * 
 * @author SGA Team
 * @version 1.0
 */
@ManagedBean(name = "categoriaController")
@ViewScoped
public class CategoriaController implements Serializable {

    private static final long serialVersionUID = 1L;

    private CategoriaService categoriaService;
    private List<Categoria> categorias;
    private Categoria categoria;
    private Categoria categoriaSelecionada;
    private String filtroNome;
    private boolean exibirDialogCadastro;
    private boolean exibirDialogConfirmacao;

    @PostConstruct
    public void init() {
        categoriaService = new CategoriaService();
        categoria = new Categoria();
        categorias = new ArrayList<>();
        carregarCategorias();
    }

    /**
     * Carrega a lista de categorias.
     */
    public void carregarCategorias() {
        try {
            if (filtroNome != null && !filtroNome.trim().isEmpty()) {
                categorias = categoriaService.buscarPorNome(filtroNome);
            } else {
                categorias = categoriaService.listarTodas();
            }
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao carregar categorias: " + e.getMessage());
            categorias = new ArrayList<>();
        }
    }

    /**
     * Prepara o formulário para cadastro de nova categoria.
     */
    public void prepararNovo() {
        categoria = new Categoria();
        exibirDialogCadastro = true;
    }

    /**
     * Prepara o formulário para edição de categoria existente.
     */
    public void prepararEdicao() {
        if (categoriaSelecionada != null) {
            categoria = new Categoria();
            categoria.setId(categoriaSelecionada.getId());
            categoria.setNome(categoriaSelecionada.getNome());
            exibirDialogCadastro = true;
        }
    }

    /**
     * Salva uma categoria (nova ou editada).
     */
    public void salvar() {
        try {
            if (categoria.getId() == null) {
                categoriaService.salvar(categoria);
                adicionarMensagemSucesso("Categoria cadastrada com sucesso!");
            } else {
                categoriaService.atualizar(categoria);
                adicionarMensagemSucesso("Categoria atualizada com sucesso!");
            }
            
            exibirDialogCadastro = false;
            carregarCategorias();
            categoria = new Categoria();
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao salvar categoria: " + e.getMessage());
        }
    }

    /**
     * Prepara a confirmação de exclusão de categoria.
     */
    public void prepararExclusao() {
        if (categoriaSelecionada != null) {
            try {
                if (categoriaService.possuiProdutos(categoriaSelecionada.getId())) {
                    adicionarMensagemAviso("Não é possível excluir a categoria pois existem produtos associados a ela.");
                    return;
                }
                exibirDialogConfirmacao = true;
            } catch (ServiceException e) {
                adicionarMensagemErro("Erro ao verificar categoria: " + e.getMessage());
            }
        }
    }

    /**
     * Confirma e executa a exclusão da categoria.
     */
    public void confirmarExclusao() {
        try {
            if (categoriaSelecionada != null) {
                categoriaService.inativarCategoria(categoriaSelecionada.getId());
                adicionarMensagemSucesso("Categoria inativada com sucesso!");
                carregarCategorias();
                categoriaSelecionada = null;
            }
            exibirDialogConfirmacao = false;
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao excluir categoria: " + e.getMessage());
        }
    }

    /**
     * Cancela a operação atual e fecha os diálogos.
     */
    public void cancelar() {
        categoria = new Categoria();
        categoriaSelecionada = null;
        exibirDialogCadastro = false;
        exibirDialogConfirmacao = false;
    }

    /**
     * Executa a pesquisa por nome.
     */
    public void pesquisar() {
        carregarCategorias();
    }

    /**
     * Limpa o filtro de pesquisa.
     */
    public void limparFiltro() {
        filtroNome = null;
        carregarCategorias();
    }

    // Métodos utilitários para mensagens
    private void adicionarMensagemSucesso(String mensagem) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", mensagem));
    }

    private void adicionarMensagemErro(String mensagem) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", mensagem));
    }

    private void adicionarMensagemAviso(String mensagem) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", mensagem));
    }

    // Getters e Setters
    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Categoria getCategoriaSelecionada() {
        return categoriaSelecionada;
    }

    public void setCategoriaSelecionada(Categoria categoriaSelecionada) {
        this.categoriaSelecionada = categoriaSelecionada;
    }

    public String getFiltroNome() {
        return filtroNome;
    }

    public void setFiltroNome(String filtroNome) {
        this.filtroNome = filtroNome;
    }

    public boolean isExibirDialogCadastro() {
        return exibirDialogCadastro;
    }

    public void setExibirDialogCadastro(boolean exibirDialogCadastro) {
        this.exibirDialogCadastro = exibirDialogCadastro;
    }

    public boolean isExibirDialogConfirmacao() {
        return exibirDialogConfirmacao;
    }

    public void setExibirDialogConfirmacao(boolean exibirDialogConfirmacao) {
        this.exibirDialogConfirmacao = exibirDialogConfirmacao;
    }
}

