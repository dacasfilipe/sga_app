package com.seuteste.sga.service;

import com.seuteste.sga.dao.ClienteDAO;
import com.seuteste.sga.dao.DAOException;
import com.seuteste.sga.dao.impl.ClienteDAOImpl;
import com.seuteste.sga.model.Cliente;

import java.util.List;

/**
 * Classe de serviço para operações relacionadas à entidade Cliente.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class ClienteService {

    private ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAOImpl();
    }

    public Cliente salvar(Cliente cliente) throws ServiceException {
        try {
            validarCliente(cliente);
            
            if (clienteDAO.existsByEmail(cliente.getEmail())) {
                throw new ServiceException("Já existe um cliente com este email.");
            }
            
            return clienteDAO.save(cliente);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao salvar cliente: " + e.getMessage(), e);
        }
    }

    public Cliente atualizar(Cliente cliente) throws ServiceException {
        try {
            validarCliente(cliente);
            
            if (cliente.getId() == null) {
                throw new ServiceException("ID do cliente é obrigatório para atualização.");
            }
            
            // Verificar se o email já existe para outro cliente
            Cliente clienteExistente = clienteDAO.findByEmail(cliente.getEmail());
            if (clienteExistente != null && !clienteExistente.getId().equals(cliente.getId())) {
                throw new ServiceException("Já existe outro cliente com este email.");
            }
            
            return clienteDAO.update(cliente);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    public void remover(Long id) throws ServiceException {
        try {
            if (id == null) {
                throw new ServiceException("ID do cliente é obrigatório.");
            }
            
            Cliente cliente = clienteDAO.findById(id);
            if (cliente == null) {
                throw new ServiceException("Cliente não encontrado.");
            }
            
            clienteDAO.delete(id);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao remover cliente: " + e.getMessage(), e);
        }
    }

    public Cliente buscarPorId(Long id) throws ServiceException {
        try {
            if (id == null) {
                return null;
            }
            return clienteDAO.findById(id);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao buscar cliente por ID: " + e.getMessage(), e);
        }
    }

    public Cliente buscarPorEmail(String email) throws ServiceException {
        try {
            if (email == null || email.trim().isEmpty()) {
                return null;
            }
            return clienteDAO.findByEmail(email.trim());
        } catch (DAOException e) {
            throw new ServiceException("Erro ao buscar cliente por email: " + e.getMessage(), e);
        }
    }

    public List<Cliente> listarTodos() throws ServiceException {
        try {
            return clienteDAO.findAllOrderByNome();
        } catch (DAOException e) {
            throw new ServiceException("Erro ao listar clientes: " + e.getMessage(), e);
        }
    }

    public List<Cliente> buscarPorNome(String nome) throws ServiceException {
        try {
            if (nome == null || nome.trim().isEmpty()) {
                return listarTodos();
            }
            return clienteDAO.findByNomeContaining(nome.trim());
        } catch (DAOException e) {
            throw new ServiceException("Erro ao buscar clientes por nome: " + e.getMessage(), e);
        }
    }

    public long contarTodos() throws ServiceException {
        try {
            return clienteDAO.count();
        } catch (DAOException e) {
            throw new ServiceException("Erro ao contar clientes: " + e.getMessage(), e);
        }
    }

    private void validarCliente(Cliente cliente) throws ServiceException {
        if (cliente == null) {
            throw new ServiceException("Cliente não pode ser nulo.");
        }
        
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new ServiceException("Nome do cliente é obrigatório.");
        }
        
        if (cliente.getNome().trim().length() < 2) {
            throw new ServiceException("Nome do cliente deve ter pelo menos 2 caracteres.");
        }
        
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new ServiceException("Email do cliente é obrigatório.");
        }
        
        if (!isValidEmail(cliente.getEmail())) {
            throw new ServiceException("Email deve ter um formato válido.");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}

