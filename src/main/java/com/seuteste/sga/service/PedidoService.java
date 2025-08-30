package com.seuteste.sga.service;

import com.seuteste.sga.dao.DAOException;
import com.seuteste.sga.dao.PedidoDAO;
import com.seuteste.sga.dao.ProdutoDAO;
import com.seuteste.sga.dao.impl.PedidoDAOImpl;
import com.seuteste.sga.dao.impl.ProdutoDAOImpl;
import com.seuteste.sga.model.ItemPedido;
import com.seuteste.sga.model.Pedido;
import com.seuteste.sga.model.Produto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe de serviço para operações relacionadas à entidade Pedido.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class PedidoService {

    private PedidoDAO pedidoDAO;
    private ProdutoDAO produtoDAO;

    public PedidoService() {
        this.pedidoDAO = new PedidoDAOImpl();
        this.produtoDAO = new ProdutoDAOImpl();
    }

    public Pedido salvar(Pedido pedido) throws ServiceException {
        try {
            validarPedido(pedido);
            
            // Verificar estoque e calcular valores
            for (ItemPedido item : pedido.getItens()) {
                Produto produto = item.getProduto();
                
                // Verificar se há estoque suficiente
                if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                    throw new ServiceException("Estoque insuficiente para o produto: " + produto.getNome() + 
                                             ". Disponível: " + produto.getQuantidadeEstoque() + 
                                             ", Solicitado: " + item.getQuantidade());
                }
                
                // Definir preço unitário como o preço atual do produto
                item.setPrecoUnitario(produto.getPreco());
                item.calcularSubtotal();
            }
            
            // Calcular valor total do pedido
            pedido.calcularValorTotal();
            
            // Salvar o pedido
            Pedido pedidoSalvo = pedidoDAO.save(pedido);
            
            // Atualizar estoque dos produtos
            atualizarEstoque(pedidoSalvo, false);
            
            return pedidoSalvo;
        } catch (DAOException e) {
            throw new ServiceException("Erro ao salvar pedido: " + e.getMessage(), e);
        }
    }

    public Pedido atualizar(Pedido pedido) throws ServiceException {
        try {
            validarPedido(pedido);
            
            if (pedido.getId() == null) {
                throw new ServiceException("ID do pedido é obrigatório para atualização.");
            }
            
            // Buscar pedido original para reverter estoque
            Pedido pedidoOriginal = pedidoDAO.findById(pedido.getId());
            if (pedidoOriginal == null) {
                throw new ServiceException("Pedido não encontrado.");
            }
            
            // Reverter estoque do pedido original
            atualizarEstoque(pedidoOriginal, true);
            
            // Verificar estoque para o pedido atualizado
            for (ItemPedido item : pedido.getItens()) {
                Produto produto = item.getProduto();
                
                if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                    // Reverter a reversão do estoque em caso de erro
                    atualizarEstoque(pedidoOriginal, false);
                    throw new ServiceException("Estoque insuficiente para o produto: " + produto.getNome() + 
                                             ". Disponível: " + produto.getQuantidadeEstoque() + 
                                             ", Solicitado: " + item.getQuantidade());
                }
                
                item.setPrecoUnitario(produto.getPreco());
                item.calcularSubtotal();
            }
            
            pedido.calcularValorTotal();
            
            Pedido pedidoAtualizado = pedidoDAO.update(pedido);
            
            // Aplicar novo estoque
            atualizarEstoque(pedidoAtualizado, false);
            
            return pedidoAtualizado;
        } catch (DAOException e) {
            throw new ServiceException("Erro ao atualizar pedido: " + e.getMessage(), e);
        }
    }

    public void remover(Long id) throws ServiceException {
        try {
            if (id == null) {
                throw new ServiceException("ID do pedido é obrigatório.");
            }
            
            Pedido pedido = pedidoDAO.findById(id);
            if (pedido == null) {
                throw new ServiceException("Pedido não encontrado.");
            }
            
            // Reverter estoque antes de excluir
            atualizarEstoque(pedido, true);
            
            pedidoDAO.delete(id);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao remover pedido: " + e.getMessage(), e);
        }
    }

    public Pedido buscarPorId(Long id) throws ServiceException {
        try {
            if (id == null) {
                return null;
            }
            return pedidoDAO.findById(id);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao buscar pedido por ID: " + e.getMessage(), e);
        }
    }

    public List<Pedido> listarTodos() throws ServiceException {
        try {
            return pedidoDAO.findAllOrderByDataPedidoDesc();
        } catch (DAOException e) {
            throw new ServiceException("Erro ao listar pedidos: " + e.getMessage(), e);
        }
    }

    public List<Pedido> buscarPorStatus(String status) throws ServiceException {
        try {
            if (status == null || status.trim().isEmpty()) {
                return listarTodos();
            }
            return pedidoDAO.findByStatus(status.trim());
        } catch (DAOException e) {
            throw new ServiceException("Erro ao buscar pedidos por status: " + e.getMessage(), e);
        }
    }

    public List<Pedido> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws ServiceException {
        try {
            if (dataInicio == null || dataFim == null) {
                return listarTodos();
            }
            return pedidoDAO.findByDataPedidoBetween(dataInicio, dataFim);
        } catch (DAOException e) {
            throw new ServiceException("Erro ao buscar pedidos por período: " + e.getMessage(), e);
        }
    }

    public long contarTodos() throws ServiceException {
        try {
            return pedidoDAO.count();
        } catch (DAOException e) {
            throw new ServiceException("Erro ao contar pedidos: " + e.getMessage(), e);
        }
    }

    private void atualizarEstoque(Pedido pedido, boolean reverter) throws ServiceException {
        try {
            for (ItemPedido item : pedido.getItens()) {
                Produto produto = produtoDAO.findById(item.getProduto().getId());
                
                if (reverter) {
                    // Reverter: adicionar quantidade de volta ao estoque
                    produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
                } else {
                    // Aplicar: subtrair quantidade do estoque
                    produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
                }
                
                produtoDAO.update(produto);
            }
        } catch (DAOException e) {
            throw new ServiceException("Erro ao atualizar estoque: " + e.getMessage(), e);
        }
    }

    private void validarPedido(Pedido pedido) throws ServiceException {
        if (pedido == null) {
            throw new ServiceException("Pedido não pode ser nulo.");
        }
        
        if (pedido.getCliente() == null) {
            throw new ServiceException("Cliente do pedido é obrigatório.");
        }
        
        if (pedido.getStatus() == null || pedido.getStatus().trim().isEmpty()) {
            throw new ServiceException("Status do pedido é obrigatório.");
        }
        
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new ServiceException("Pedido deve ter pelo menos um item.");
        }
        
        for (ItemPedido item : pedido.getItens()) {
            if (item.getProduto() == null) {
                throw new ServiceException("Produto do item é obrigatório.");
            }
            
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new ServiceException("Quantidade do item deve ser maior que zero.");
            }
        }
    }
}

