package com.seuteste.sga.controller;

import com.seuteste.sga.model.Cliente;
import com.seuteste.sga.model.ItemPedido;
import com.seuteste.sga.model.Pedido;
import com.seuteste.sga.model.Produto;
import com.seuteste.sga.service.ClienteService;
import com.seuteste.sga.service.PedidoService;
import com.seuteste.sga.service.ProdutoService;
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
import java.util.Arrays;
import java.util.List;

/**
 * Managed Bean para controlar as operações relacionadas à entidade Pedido.
 * 
 * @author SGA Team
 * @version 1.0
 */
@ManagedBean(name = "pedidoController")
@ViewScoped
public class PedidoController implements Serializable {

    private static final long serialVersionUID = 1L;

    private PedidoService pedidoService;
    private ClienteService clienteService;
    private ProdutoService produtoService;
    
    private List<Pedido> pedidos;
    private Pedido pedido;
    private Pedido pedidoSelecionado;
    private String filtroStatus;
    private boolean exibirDialogCadastro;
    private boolean exibirDialogConfirmacao;
    private boolean exibirDialogDetalhes;
    
    // Para gerenciamento de itens do pedido
    private ItemPedido novoItem;
    private List<SelectItem> clientesSelectItems;
    private List<SelectItem> produtosSelectItems;
    private List<SelectItem> statusSelectItems;

    @PostConstruct
    public void init() {
        pedidoService = new PedidoService();
        clienteService = new ClienteService();
        produtoService = new ProdutoService();
        
        pedido = new Pedido();
        novoItem = new ItemPedido();
        pedidos = new ArrayList<>();
        clientesSelectItems = new ArrayList<>();
        produtosSelectItems = new ArrayList<>();
        statusSelectItems = new ArrayList<>();
        
        carregarPedidos();
        carregarClientes();
        carregarProdutos();
        carregarStatus();
    }

    public void carregarPedidos() {
        try {
            if (filtroStatus != null && !filtroStatus.trim().isEmpty()) {
                pedidos = pedidoService.buscarPorStatus(filtroStatus);
            } else {
                pedidos = pedidoService.listarTodos();
            }
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao carregar pedidos: " + e.getMessage());
            pedidos = new ArrayList<>();
        }
    }

    public void carregarClientes() {
        try {
            clientesSelectItems.clear();
            clientesSelectItems.add(new SelectItem(null, "Selecione um cliente"));
            
            List<Cliente> clientes = clienteService.listarTodos();
            for (Cliente cliente : clientes) {
                clientesSelectItems.add(new SelectItem(cliente.getId(), cliente.getNome()));
            }
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao carregar clientes: " + e.getMessage());
        }
    }

    public void carregarProdutos() {
        try {
            produtosSelectItems.clear();
            produtosSelectItems.add(new SelectItem(null, "Selecione um produto"));
            
            List<Produto> produtos = produtoService.listarTodos();
            for (Produto produto : produtos) {
                if (produto.getQuantidadeEstoque() > 0) {
                    produtosSelectItems.add(new SelectItem(produto.getId(), 
                        produto.getNome() + " (Estoque: " + produto.getQuantidadeEstoque() + ")"));
                }
            }
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao carregar produtos: " + e.getMessage());
        }
    }

    public void carregarStatus() {
        statusSelectItems.clear();
        statusSelectItems.add(new SelectItem("", "Todos os status"));
        statusSelectItems.add(new SelectItem("Pendente", "Pendente"));
        statusSelectItems.add(new SelectItem("Processando", "Processando"));
        statusSelectItems.add(new SelectItem("Enviado", "Enviado"));
        statusSelectItems.add(new SelectItem("Entregue", "Entregue"));
        statusSelectItems.add(new SelectItem("Cancelado", "Cancelado"));
    }

    public void prepararNovo() {
        pedido = new Pedido();
        pedido.setDataPedido(LocalDate.now());
        pedido.setStatus("Pendente");
        novoItem = new ItemPedido();
        exibirDialogCadastro = true;
    }

    public void prepararEdicao() {
        if (pedidoSelecionado != null) {
            pedido = new Pedido();
            pedido.setId(pedidoSelecionado.getId());
            pedido.setCliente(pedidoSelecionado.getCliente());
            pedido.setDataPedido(pedidoSelecionado.getDataPedido());
            pedido.setStatus(pedidoSelecionado.getStatus());
            pedido.setValorTotal(pedidoSelecionado.getValorTotal());
            
            // Copiar itens
            pedido.setItens(new ArrayList<>());
            for (ItemPedido item : pedidoSelecionado.getItens()) {
                ItemPedido novoItemCopia = new ItemPedido();
                novoItemCopia.setId(item.getId());
                novoItemCopia.setProduto(item.getProduto());
                novoItemCopia.setQuantidade(item.getQuantidade());
                novoItemCopia.setPrecoUnitario(item.getPrecoUnitario());
                novoItemCopia.setSubtotal(item.getSubtotal());
                pedido.adicionarItem(novoItemCopia);
            }
            
            novoItem = new ItemPedido();
            exibirDialogCadastro = true;
        }
    }

    public void prepararDetalhes() {
        if (pedidoSelecionado != null) {
            exibirDialogDetalhes = true;
        }
    }

    public void adicionarItem() {
        try {
            if (novoItem.getProduto() == null) {
                adicionarMensagemErro("Selecione um produto para adicionar ao pedido.");
                return;
            }
            
            if (novoItem.getQuantidade() == null || novoItem.getQuantidade() <= 0) {
                adicionarMensagemErro("Informe uma quantidade válida.");
                return;
            }
            
            // Buscar produto completo
            Produto produto = produtoService.buscarPorId(novoItem.getProduto().getId());
            if (produto == null) {
                adicionarMensagemErro("Produto não encontrado.");
                return;
            }
            
            // Verificar se já existe item com este produto
            for (ItemPedido item : pedido.getItens()) {
                if (item.getProduto().getId().equals(produto.getId())) {
                    adicionarMensagemErro("Este produto já foi adicionado ao pedido.");
                    return;
                }
            }
            
            // Verificar estoque
            if (produto.getQuantidadeEstoque() < novoItem.getQuantidade()) {
                adicionarMensagemErro("Estoque insuficiente. Disponível: " + produto.getQuantidadeEstoque());
                return;
            }
            
            // Criar novo item
            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(novoItem.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            item.calcularSubtotal();
            
            pedido.adicionarItem(item);
            
            // Limpar formulário de novo item
            novoItem = new ItemPedido();
            
            adicionarMensagemSucesso("Item adicionado ao pedido!");
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao adicionar item: " + e.getMessage());
        }
    }

    public void removerItem(ItemPedido item) {
        pedido.removerItem(item);
        adicionarMensagemSucesso("Item removido do pedido!");
    }

    public void salvar() {
        try {
            if (pedido.getCliente() == null) {
                adicionarMensagemErro("Selecione um cliente para o pedido.");
                return;
            }
            
            if (pedido.getItens().isEmpty()) {
                adicionarMensagemErro("Adicione pelo menos um item ao pedido.");
                return;
            }
            
            // Buscar cliente completo
            Cliente cliente = clienteService.buscarPorId(pedido.getCliente().getId());
            pedido.setCliente(cliente);
            
            if (pedido.getId() == null) {
                pedidoService.salvar(pedido);
                adicionarMensagemSucesso("Pedido cadastrado com sucesso!");
            } else {
                pedidoService.atualizar(pedido);
                adicionarMensagemSucesso("Pedido atualizado com sucesso!");
            }
            
            exibirDialogCadastro = false;
            carregarPedidos();
            carregarProdutos(); // Recarregar para atualizar estoque
            pedido = new Pedido();
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao salvar pedido: " + e.getMessage());
        }
    }

    public void prepararExclusao() {
        if (pedidoSelecionado != null) {
            exibirDialogConfirmacao = true;
        }
    }

    public void confirmarExclusao() {
        try {
            if (pedidoSelecionado != null) {
                pedidoService.remover(pedidoSelecionado.getId());
                adicionarMensagemSucesso("Pedido excluído com sucesso!");
                carregarPedidos();
                carregarProdutos(); // Recarregar para atualizar estoque
                pedidoSelecionado = null;
            }
            exibirDialogConfirmacao = false;
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao excluir pedido: " + e.getMessage());
        }
    }

    public void cancelar() {
        pedido = new Pedido();
        novoItem = new ItemPedido();
        pedidoSelecionado = null;
        exibirDialogCadastro = false;
        exibirDialogConfirmacao = false;
        exibirDialogDetalhes = false;
    }

    public void pesquisar() {
        carregarPedidos();
    }

    public void limparFiltro() {
        filtroStatus = null;
        carregarPedidos();
    }

    public long getTotalPedidos() {
        try {
            return pedidoService.contarTodos();
        } catch (ServiceException e) {
            return 0;
        }
    }

    public BigDecimal getValorTotalPedido() {
        if (pedido != null && pedido.getItens() != null) {
            return pedido.getItens().stream()
                    .map(ItemPedido::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
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
    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Pedido getPedidoSelecionado() {
        return pedidoSelecionado;
    }

    public void setPedidoSelecionado(Pedido pedidoSelecionado) {
        this.pedidoSelecionado = pedidoSelecionado;
    }

    public String getFiltroStatus() {
        return filtroStatus;
    }

    public void setFiltroStatus(String filtroStatus) {
        this.filtroStatus = filtroStatus;
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

    public boolean isExibirDialogDetalhes() {
        return exibirDialogDetalhes;
    }

    public void setExibirDialogDetalhes(boolean exibirDialogDetalhes) {
        this.exibirDialogDetalhes = exibirDialogDetalhes;
    }

    public ItemPedido getNovoItem() {
        return novoItem;
    }

    public void setNovoItem(ItemPedido novoItem) {
        this.novoItem = novoItem;
    }

    public List<SelectItem> getClientesSelectItems() {
        return clientesSelectItems;
    }

    public void setClientesSelectItems(List<SelectItem> clientesSelectItems) {
        this.clientesSelectItems = clientesSelectItems;
    }

    public List<SelectItem> getProdutosSelectItems() {
        return produtosSelectItems;
    }

    public void setProdutosSelectItems(List<SelectItem> produtosSelectItems) {
        this.produtosSelectItems = produtosSelectItems;
    }

    public List<SelectItem> getStatusSelectItems() {
        return statusSelectItems;
    }

    public void setStatusSelectItems(List<SelectItem> statusSelectItems) {
        this.statusSelectItems = statusSelectItems;
    }
}

