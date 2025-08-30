package com.seuteste.sga.controller;

import com.seuteste.sga.model.Produto;
import com.seuteste.sga.model.Categoria;
import com.seuteste.sga.service.ProdutoService;
import com.seuteste.sga.service.CategoriaService;
import com.seuteste.sga.service.ServiceException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Managed Bean para controlar as operações relacionadas à entidade Produto.
 * 
 * @author SGA Team
 * @version 1.0
 */
@ManagedBean(name = "produtoController")
@ViewScoped
public class ProdutoController implements Serializable {

    private static final long serialVersionUID = 1L;

    private ProdutoService produtoService;
    private CategoriaService categoriaService;
    private List<Produto> produtos;
    private Produto produto;
    private Produto produtoSelecionado;
    private String filtroNome;
    private Long filtroCategoria;
    private boolean exibirDialogCadastro;
    private boolean exibirDialogConfirmacao;
    private List<SelectItem> categoriasSelectItems;

    @PostConstruct
    public void init() {
        produtoService = new ProdutoService();
        categoriaService = new CategoriaService();
        produto = new Produto();
        produtos = new ArrayList<>();
        categoriasSelectItems = new ArrayList<>();
        carregarProdutos();
        carregarCategorias();
    }

    public void carregarProdutos() {
        try {
            if (filtroNome != null && !filtroNome.trim().isEmpty()) {
                produtos = produtoService.buscarPorNome(filtroNome);
            } else if (filtroCategoria != null) {
                produtos = produtoService.buscarPorCategoria(filtroCategoria);
            } else {
                produtos = produtoService.listarTodos();
            }
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao carregar produtos: " + e.getMessage());
            produtos = new ArrayList<>();
        }
    }

    public void carregarCategorias() {
        try {
            categoriasSelectItems.clear();
            categoriasSelectItems.add(new SelectItem(null, "Selecione uma categoria"));
            
            List<Categoria> categorias = categoriaService.listarTodas();
            for (Categoria categoria : categorias) {
                categoriasSelectItems.add(new SelectItem(categoria.getId(), categoria.getNome()));
            }
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao carregar categorias: " + e.getMessage());
        }
    }

    public void prepararNovo() {
        produto = new Produto();
        produto.setDataCadastro(LocalDate.now());
        produto.setQuantidadeEstoque(0);
        exibirDialogCadastro = true;
    }

    public void prepararEdicao() {
        if (produtoSelecionado != null) {
            produto = new Produto();
            produto.setId(produtoSelecionado.getId());
            produto.setNome(produtoSelecionado.getNome());
            produto.setDescricao(produtoSelecionado.getDescricao());
            produto.setPreco(produtoSelecionado.getPreco());
            produto.setQuantidadeEstoque(produtoSelecionado.getQuantidadeEstoque());
            produto.setCategoria(produtoSelecionado.getCategoria());
            produto.setDataCadastro(produtoSelecionado.getDataCadastro());
            exibirDialogCadastro = true;
        }
    }

    public void salvar() {
        try {
            if (produto.getId() == null) {
                produtoService.salvar(produto);
                adicionarMensagemSucesso("Produto cadastrado com sucesso!");
            } else {
                produtoService.atualizar(produto);
                adicionarMensagemSucesso("Produto atualizado com sucesso!");
            }
            
            exibirDialogCadastro = false;
            carregarProdutos();
            produto = new Produto();
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao salvar produto: " + e.getMessage());
        }
    }

    public void prepararExclusao() {
        if (produtoSelecionado != null) {
            exibirDialogConfirmacao = true;
        }
    }

    public void confirmarExclusao() {
        try {
            if (produtoSelecionado != null) {
                produtoService.remover(produtoSelecionado.getId());
                adicionarMensagemSucesso("Produto excluído com sucesso!");
                carregarProdutos();
                produtoSelecionado = null;
            }
            exibirDialogConfirmacao = false;
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao excluir produto: " + e.getMessage());
        }
    }

    public void cancelar() {
        produto = new Produto();
        produtoSelecionado = null;
        exibirDialogCadastro = false;
        exibirDialogConfirmacao = false;
    }

    public void pesquisar() {
        carregarProdutos();
    }

    public void limparFiltro() {
        filtroNome = null;
        filtroCategoria = null;
        carregarProdutos();
    }

    public long getTotalProdutos() {
        try {
            return produtoService.contarTodos();
        } catch (ServiceException e) {
            return 0;
        }
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

    // Getters e Setters
    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Produto getProdutoSelecionado() {
        return produtoSelecionado;
    }

    public void setProdutoSelecionado(Produto produtoSelecionado) {
        this.produtoSelecionado = produtoSelecionado;
    }

    public String getFiltroNome() {
        return filtroNome;
    }

    public void setFiltroNome(String filtroNome) {
        this.filtroNome = filtroNome;
    }

    public Long getFiltroCategoria() {
        return filtroCategoria;
    }

    public void setFiltroCategoria(Long filtroCategoria) {
        this.filtroCategoria = filtroCategoria;
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

    public List<SelectItem> getCategoriasSelectItems() {
        return categoriasSelectItems;
    }

    public void setCategoriasSelectItems(List<SelectItem> categoriasSelectItems) {
        this.categoriasSelectItems = categoriasSelectItems;
    }
}

