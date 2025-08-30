package com.seuteste.sga.service;

import com.seuteste.sga.dao.CategoriaDAO;
import com.seuteste.sga.dao.DAOException;
import com.seuteste.sga.dao.impl.CategoriaDAOImpl;
import com.seuteste.sga.model.Categoria;

import java.util.List;

/**
 * Classe de serviço para operações relacionadas à entidade Categoria.
 * Contém a lógica de negócio e coordena as operações entre a camada de apresentação e a camada de dados.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class CategoriaService {

    private CategoriaDAO categoriaDAO;

    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAOImpl();
    }

    /**
     * Salva uma nova categoria.
     * 
     * @param categoria Categoria a ser salva
     * @return Categoria salva
     * @throws ServiceException em caso de erro na operação
     */
    public Categoria salvar(Categoria categoria) throws ServiceException {
        try {
            validarCategoria(categoria);
            
            // Verifica se já existe uma categoria com o mesmo nome
            if (categoriaDAO.existsByNome(categoria.getNome())) {
                throw new ServiceException("Já existe uma categoria com este nome.");
            }
            
            return categoriaDAO.save(categoria);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao salvar categoria: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza uma categoria existente.
     * 
     * @param categoria Categoria a ser atualizada
     * @return Categoria atualizada
     * @throws ServiceException em caso de erro na operação
     */
    public Categoria atualizar(Categoria categoria) throws ServiceException {
        try {
            validarCategoria(categoria);
            
            if (categoria.getId() == null) {
                throw new ServiceException("ID da categoria é obrigatório para atualização.");
            }
            
            // Verifica se existe outra categoria com o mesmo nome
            Categoria existente = categoriaDAO.findByNome(categoria.getNome());
            if (existente != null && !existente.getId().equals(categoria.getId())) {
                throw new ServiceException("Já existe outra categoria com este nome.");
            }
            
            return categoriaDAO.update(categoria);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao atualizar categoria: " + e.getMessage(), e);
        }
    }

    /**
     * Remove uma categoria pelo ID.
     * 
     * @param id ID da categoria a ser removida
     * @throws ServiceException em caso de erro na operação
     */
    public void remover(Long id) throws ServiceException {
        try {
            if (id == null) {
                throw new ServiceException("ID da categoria é obrigatório.");
            }
            
            // Verifica se a categoria existe
            Categoria categoria = categoriaDAO.findById(id);
            if (categoria == null) {
                throw new ServiceException("Categoria não encontrada.");
            }
            
            categoriaDAO.delete(id);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao remover categoria: " + e.getMessage(), e);
        }
    }

    /**
     * Busca uma categoria pelo ID.
     * 
     * @param id ID da categoria
     * @return Categoria encontrada ou null se não existir
     * @throws ServiceException em caso de erro na operação
     */
    public Categoria buscarPorId(Long id) throws ServiceException {
        try {
            if (id == null) {
                return null;
            }
            return categoriaDAO.findById(id);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao buscar categoria por ID: " + e.getMessage(), e);
        }
    }

    /**
     * Lista todas as categorias ordenadas por nome.
     * 
     * @return Lista de categorias
     * @throws ServiceException em caso de erro na operação
     */
    public List<Categoria> listarTodas() throws ServiceException {
        try {
            return categoriaDAO.findAllOrderByNome();
        } catch (DAOException e) {
            throw new ServiceException("Erro ao listar categorias: " + e.getMessage(), e);
        }
    }

    /**
     * Busca categorias por nome (busca parcial).
     * 
     * @param nome Nome ou parte do nome da categoria
     * @return Lista de categorias encontradas
     * @throws ServiceException em caso de erro na operação
     */
    public List<Categoria> buscarPorNome(String nome) throws ServiceException {
        try {
            if (nome == null || nome.trim().isEmpty()) {
                return listarTodas();
            }
            return categoriaDAO.findByNomeContaining(nome.trim());
        } catch (DAOException e) {
            throw new ServiceException("Erro ao buscar categorias por nome: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se uma categoria possui produtos associados.
     * 
     * @param categoriaId ID da categoria
     * @return true se possui produtos, false caso contrário
     * @throws ServiceException em caso de erro na operação
     */
    public boolean possuiProdutos(Long categoriaId) throws ServiceException {
        try {
            if (categoriaId == null) {
                return false;
            }
            return categoriaDAO.hasProducts(categoriaId);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao verificar se categoria possui produtos: " + e.getMessage(), e);
        }
    }

    /**
     * Conta o número total de categorias.
     * 
     * @return Número total de categorias
     * @throws ServiceException em caso de erro na operação
     */
    public long contarTodas() throws ServiceException {
        try {
            return categoriaDAO.count();
        } catch (DAOException e) {
            throw new ServiceException("Erro ao contar categorias: " + e.getMessage(), e);
        }
    }

    /**
     * Valida os dados de uma categoria.
     * 
     * @param categoria Categoria a ser validada
     * @throws ServiceException se os dados forem inválidos
     */
    private void validarCategoria(Categoria categoria) throws ServiceException {
        if (categoria == null) {
            throw new ServiceException("Categoria não pode ser nula.");
        }
        
        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new ServiceException("Nome da categoria é obrigatório.");
        }
        
        if (categoria.getNome().trim().length() < 2) {
            throw new ServiceException("Nome da categoria deve ter pelo menos 2 caracteres.");
        }
        
        if (categoria.getNome().trim().length() > 255) {
            throw new ServiceException("Nome da categoria não pode exceder 255 caracteres.");
        }
    }
}

