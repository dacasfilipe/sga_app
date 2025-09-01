package com.seuteste.sga.service;

import com.seuteste.sga.dao.ProdutoDAO;
import com.seuteste.sga.dao.DAOException;
import com.seuteste.sga.dao.impl.ProdutoDAOImpl;
import com.seuteste.sga.model.Produto;
import com.seuteste.sga.model.Categoria;

import java.math.BigDecimal;
import java.util.List;

/**
 * Classe de serviço para operações relacionadas à entidade Produto.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class ProdutoService {

    private ProdutoDAO produtoDAO;

    public ProdutoService() {
        this.produtoDAO = new ProdutoDAOImpl();
    }

    public Produto salvar(Produto produto) throws ServiceException {
        try {
            validarProduto(produto);
            return produtoDAO.save(produto);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao salvar produto: " + e.getMessage(), e);
        }
    }

    public Produto atualizar(Produto produto) throws ServiceException {
        try {
            validarProduto(produto);
            
            if (produto.getId() == null) {
                throw new ServiceException("ID do produto é obrigatório para atualização.");
            }
            
            return produtoDAO.update(produto);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    /**
     * Inativa um produto pelo ID.
     * 
     * @param id ID do produto a ser inativado
     * @throws ServiceException em caso de erro na operação
     */
    public void inativarProduto(Long id) throws ServiceException {
        try {
            Produto produto = produtoDAO.findById(id);
            if (produto == null) {
                throw new ServiceException("Produto não encontrado.");
            }
            
            produtoDAO.deactivate(id);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao inativar produto: " + e.getMessage(), e);
        }
    }

    /**
     * Ativa um produto pelo ID.
     * 
     * @param id ID do produto a ser ativado
     * @throws ServiceException em caso de erro na operação
     */
    public void ativarProduto(Long id) throws ServiceException {
        try {
            Produto produto = produtoDAO.findById(id);
            if (produto == null) {
                throw new ServiceException("Produto não encontrado.");
            }
            
            produtoDAO.activate(id);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao ativar produto: " + e.getMessage(), e);
        }
    }
    public Produto buscarPorId(Long id) throws ServiceException {
        try {
            if (id == null) {
                return null;
            }
            return produtoDAO.findById(id);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao buscar produto por ID: " + e.getMessage(), e);
        }
    }

    public List<Produto> listarTodos() throws ServiceException {
        try {
            return produtoDAO.findAllOrderByNome();
        } catch (DAOException e) {
            throw new ServiceException("Erro ao listar produtos: " + e.getMessage(), e);
        }
    }

    public List<Produto> buscarPorNome(String nome) throws ServiceException {
        try {
            if (nome == null || nome.trim().isEmpty()) {
                return listarTodos();
            }
            return produtoDAO.findByNomeContaining(nome.trim());
        } catch (DAOException e) {
            throw new ServiceException("Erro ao buscar produtos por nome: " + e.getMessage(), e);
        }
    }

    public List<Produto> buscarPorCategoria(Long categoriaId) throws ServiceException {
        try {
            if (categoriaId == null) {
                return listarTodos();
            }
            return produtoDAO.findByCategoriaId(categoriaId);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao buscar produtos por categoria: " + e.getMessage(), e);
        }
    }

    public long contarTodos() throws ServiceException {
        try {
            return produtoDAO.count();
        } catch (DAOException e) {
            throw new ServiceException("Erro ao contar produtos: " + e.getMessage(), e);
        }
    }

    private void validarProduto(Produto produto) throws ServiceException {
        if (produto == null) {
            throw new ServiceException("Produto não pode ser nulo.");
        }
        
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new ServiceException("Nome do produto é obrigatório.");
        }
        
        if (produto.getNome().trim().length() < 2) {
            throw new ServiceException("Nome do produto deve ter pelo menos 2 caracteres.");
        }
        
        if (produto.getPreco() == null) {
            throw new ServiceException("Preço do produto é obrigatório.");
        }
        
        if (produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("Preço do produto deve ser maior que zero.");
        }
        
        if (produto.getQuantidadeEstoque() == null) {
            throw new ServiceException("Quantidade em estoque é obrigatória.");
        }
        
        if (produto.getQuantidadeEstoque() < 0) {
            throw new ServiceException("Quantidade em estoque não pode ser negativa.");
        }
        
        if (produto.getCategoria() == null) {
            throw new ServiceException("Categoria do produto é obrigatória.");
        }
    }
}

