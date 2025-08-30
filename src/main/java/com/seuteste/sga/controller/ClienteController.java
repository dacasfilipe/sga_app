package com.seuteste.sga.controller;

import com.seuteste.sga.model.Cliente;
import com.seuteste.sga.service.ClienteService;
import com.seuteste.sga.service.ServiceException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Managed Bean para controlar as operações relacionadas à entidade Cliente.
 * 
 * @author SGA Team
 * @version 1.0
 */
@ManagedBean(name = "clienteController")
@ViewScoped
public class ClienteController implements Serializable {

    private static final long serialVersionUID = 1L;

    private ClienteService clienteService;
    private List<Cliente> clientes;
    private Cliente cliente;
    private Cliente clienteSelecionado;
    private String filtroNome;
    private boolean exibirDialogCadastro;
    private boolean exibirDialogConfirmacao;

    @PostConstruct
    public void init() {
        clienteService = new ClienteService();
        cliente = new Cliente();
        clientes = new ArrayList<>();
        carregarClientes();
    }

    public void carregarClientes() {
        try {
            if (filtroNome != null && !filtroNome.trim().isEmpty()) {
                clientes = clienteService.buscarPorNome(filtroNome);
            } else {
                clientes = clienteService.listarTodos();
            }
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao carregar clientes: " + e.getMessage());
            clientes = new ArrayList<>();
        }
    }

    public void prepararNovo() {
        cliente = new Cliente();
        cliente.setDataCadastro(LocalDate.now());
        exibirDialogCadastro = true;
    }

    public void prepararEdicao() {
        if (clienteSelecionado != null) {
            cliente = new Cliente();
            cliente.setId(clienteSelecionado.getId());
            cliente.setNome(clienteSelecionado.getNome());
            cliente.setEmail(clienteSelecionado.getEmail());
            cliente.setTelefone(clienteSelecionado.getTelefone());
            cliente.setEndereco(clienteSelecionado.getEndereco());
            cliente.setDataCadastro(clienteSelecionado.getDataCadastro());
            exibirDialogCadastro = true;
        }
    }

    public void salvar() {
        try {
            if (cliente.getId() == null) {
                clienteService.salvar(cliente);
                adicionarMensagemSucesso("Cliente cadastrado com sucesso!");
            } else {
                clienteService.atualizar(cliente);
                adicionarMensagemSucesso("Cliente atualizado com sucesso!");
            }
            
            exibirDialogCadastro = false;
            carregarClientes();
            cliente = new Cliente();
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao salvar cliente: " + e.getMessage());
        }
    }

    public void prepararExclusao() {
        if (clienteSelecionado != null) {
            exibirDialogConfirmacao = true;
        }
    }

    public void confirmarExclusao() {
        try {
            if (clienteSelecionado != null) {
                clienteService.remover(clienteSelecionado.getId());
                adicionarMensagemSucesso("Cliente excluído com sucesso!");
                carregarClientes();
                clienteSelecionado = null;
            }
            exibirDialogConfirmacao = false;
        } catch (ServiceException e) {
            adicionarMensagemErro("Erro ao excluir cliente: " + e.getMessage());
        }
    }

    public void cancelar() {
        cliente = new Cliente();
        clienteSelecionado = null;
        exibirDialogCadastro = false;
        exibirDialogConfirmacao = false;
    }

    public void pesquisar() {
        carregarClientes();
    }

    public void limparFiltro() {
        filtroNome = null;
        carregarClientes();
    }

    public long getTotalClientes() {
        try {
            return clienteService.contarTodos();
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
    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Cliente getClienteSelecionado() {
        return clienteSelecionado;
    }

    public void setClienteSelecionado(Cliente clienteSelecionado) {
        this.clienteSelecionado = clienteSelecionado;
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

