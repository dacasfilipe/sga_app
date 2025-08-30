package com.seuteste.sga.service;

import com.seuteste.sga.model.*;
import com.seuteste.sga.util.TestDataBuilder;
import com.seuteste.sga.util.TestJPAUtil;

import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para PedidoService.
 * 
 * @author SGA Team
 * @version 1.0
 */
@DisplayName("Testes do PedidoService")
class PedidoServiceTest {

    private PedidoService pedidoService;
    private CategoriaService categoriaService;
    private ProdutoService produtoService;
    private ClienteService clienteService;
    
    private EntityManager entityManager;
    private Categoria categoria;
    private Produto produto1;
    private Produto produto2;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        // Configurar EntityManager para testes
        entityManager = TestJPAUtil.createEntityManagerH2();
        TestJPAUtil.setCurrentEntityManager(entityManager);
        
        // Criar serviços
        pedidoService = new PedidoService();
        categoriaService = new CategoriaService();
        produtoService = new ProdutoService();
        clienteService = new ClienteService();
        
        // Criar dados de teste
        criarDadosBasicos();
    }

    @AfterEach
    void tearDown() {
        TestJPAUtil.removeCurrentEntityManager();
    }

    @AfterAll
    static void tearDownAll() {
        TestJPAUtil.closeAll();
    }

    private void criarDadosBasicos() {
        TestJPAUtil.executeInTransaction(() -> {
            try {
                // Criar categoria
                categoria = TestDataBuilder.criarCategoriaDefault();
                categoria = categoriaService.salvar(categoria);
                
                // Criar produtos
                produto1 = TestDataBuilder.criarProduto("Produto 1", new BigDecimal("50.00"), 10, categoria);
                produto1 = produtoService.salvar(produto1);
                
                produto2 = TestDataBuilder.criarProduto("Produto 2", new BigDecimal("30.00"), 5, categoria);
                produto2 = produtoService.salvar(produto2);
                
                // Criar cliente
                cliente = TestDataBuilder.criarClienteDefault();
                cliente = clienteService.salvar(cliente);
                
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Nested
    @DisplayName("Testes de criação de pedido")
    class CriacaoPedidoTests {

        @Test
        @DisplayName("Deve criar pedido com um item")
        void deveCriarPedidoComUmItem() {
            // Given
            Pedido pedido = TestDataBuilder.criarPedidoDefault(cliente);
            ItemPedido item = TestDataBuilder.criarItemPedido(pedido, produto1, 2, produto1.getPreco());
            pedido.getItens().add(item);
            
            Integer estoqueAnterior = produto1.getEstoque();
            
            // When
            Pedido pedidoSalvo = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return pedidoService.salvar(pedido);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            assertNotNull(pedidoSalvo, "Pedido deve ser salvo");
            assertNotNull(pedidoSalvo.getId(), "ID do pedido deve ser gerado");
            assertEquals(1, pedidoSalvo.getItens().size(), "Deve ter 1 item");
            
            // Verificar cálculo do valor total
            BigDecimal valorEsperado = produto1.getPreco().multiply(new BigDecimal("2"));
            assertEquals(0, valorEsperado.compareTo(pedidoSalvo.getValorTotal()), 
                "Valor total deve ser calculado corretamente");
            
            // Verificar redução do estoque
            Produto produtoAtualizado = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return produtoService.buscarPorId(produto1.getId());
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            assertEquals(estoqueAnterior - 2, produtoAtualizado.getEstoque(), 
                "Estoque deve ser reduzido");
        }

        @Test
        @DisplayName("Deve criar pedido com múltiplos itens")
        void deveCriarPedidoComMultiplosItens() {
            // Given
            Pedido pedido = TestDataBuilder.criarPedidoDefault(cliente);
            
            ItemPedido item1 = TestDataBuilder.criarItemPedido(pedido, produto1, 1, produto1.getPreco());
            ItemPedido item2 = TestDataBuilder.criarItemPedido(pedido, produto2, 2, produto2.getPreco());
            
            pedido.getItens().addAll(Arrays.asList(item1, item2));
            
            // When
            Pedido pedidoSalvo = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return pedidoService.salvar(pedido);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            assertEquals(2, pedidoSalvo.getItens().size(), "Deve ter 2 itens");
            
            // Verificar cálculo do valor total
            BigDecimal valorEsperado = produto1.getPreco().multiply(new BigDecimal("1"))
                .add(produto2.getPreco().multiply(new BigDecimal("2")));
            assertEquals(0, valorEsperado.compareTo(pedidoSalvo.getValorTotal()), 
                "Valor total deve ser soma dos subtotais");
        }

        @Test
        @DisplayName("Deve falhar ao criar pedido com estoque insuficiente")
        void deveFalharAoCriarPedidoComEstoqueInsuficiente() {
            // Given
            Pedido pedido = TestDataBuilder.criarPedidoDefault(cliente);
            ItemPedido item = TestDataBuilder.criarItemPedido(pedido, produto1, 15, produto1.getPreco()); // Mais que o estoque
            pedido.getItens().add(item);
            
            // When & Then
            ServiceException exception = assertThrows(ServiceException.class, () -> {
                TestJPAUtil.executeInTransaction(() -> {
                    try {
                        pedidoService.salvar(pedido);
                    } catch (ServiceException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            
            assertTrue(exception.getMessage().contains("estoque"), 
                "Mensagem deve indicar problema de estoque");
        }

        @Test
        @DisplayName("Deve falhar ao criar pedido sem itens")
        void deveFalharAoCriarPedidoSemItens() {
            // Given
            Pedido pedido = TestDataBuilder.criarPedidoDefault(cliente);
            // Não adicionar itens
            
            // When & Then
            ServiceException exception = assertThrows(ServiceException.class, () -> {
                TestJPAUtil.executeInTransaction(() -> {
                    try {
                        pedidoService.salvar(pedido);
                    } catch (ServiceException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            
            assertTrue(exception.getMessage().contains("itens"), 
                "Mensagem deve indicar que pedido precisa ter itens");
        }

        @Test
        @DisplayName("Deve falhar ao criar pedido com cliente nulo")
        void deveFalharAoCriarPedidoComClienteNulo() {
            // Given
            Pedido pedido = TestDataBuilder.criarPedidoDefault(null); // Cliente nulo
            ItemPedido item = TestDataBuilder.criarItemPedido(pedido, produto1, 1, produto1.getPreco());
            pedido.getItens().add(item);
            
            // When & Then
            ServiceException exception = assertThrows(ServiceException.class, () -> {
                TestJPAUtil.executeInTransaction(() -> {
                    try {
                        pedidoService.salvar(pedido);
                    } catch (ServiceException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            
            assertTrue(exception.getMessage().contains("cliente"), 
                "Mensagem deve indicar que cliente é obrigatório");
        }
    }

    @Nested
    @DisplayName("Testes de atualização de pedido")
    class AtualizacaoPedidoTests {

        @Test
        @DisplayName("Deve atualizar pedido adicionando item")
        void deveAtualizarPedidoAdicionandoItem() {
            // Given - Criar pedido inicial
            Pedido pedido = TestDataBuilder.criarPedidoDefault(cliente);
            ItemPedido item1 = TestDataBuilder.criarItemPedido(pedido, produto1, 1, produto1.getPreco());
            pedido.getItens().add(item1);
            
            Pedido pedidoSalvo = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return pedidoService.salvar(pedido);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // When - Adicionar novo item
            ItemPedido item2 = TestDataBuilder.criarItemPedido(pedidoSalvo, produto2, 1, produto2.getPreco());
            pedidoSalvo.getItens().add(item2);
            
            Pedido pedidoAtualizado = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return pedidoService.atualizar(pedidoSalvo);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            assertEquals(2, pedidoAtualizado.getItens().size(), "Deve ter 2 itens após atualização");
            
            // Verificar novo valor total
            BigDecimal valorEsperado = produto1.getPreco().add(produto2.getPreco());
            assertEquals(0, valorEsperado.compareTo(pedidoAtualizado.getValorTotal()), 
                "Valor total deve ser recalculado");
        }

        @Test
        @DisplayName("Deve atualizar pedido alterando quantidade de item")
        void deveAtualizarPedidoAlterandoQuantidadeItem() {
            // Given - Criar pedido inicial
            Pedido pedido = TestDataBuilder.criarPedidoDefault(cliente);
            ItemPedido item = TestDataBuilder.criarItemPedido(pedido, produto1, 2, produto1.getPreco());
            pedido.getItens().add(item);
            
            Pedido pedidoSalvo = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return pedidoService.salvar(pedido);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            Integer estoqueAposCreacao = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return produtoService.buscarPorId(produto1.getId()).getEstoque();
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // When - Alterar quantidade do item
            pedidoSalvo.getItens().get(0).setQuantidade(3); // Era 2, agora 3
            
            Pedido pedidoAtualizado = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return pedidoService.atualizar(pedidoSalvo);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            assertEquals(3, pedidoAtualizado.getItens().get(0).getQuantidade(), 
                "Quantidade deve ser atualizada");
            
            // Verificar ajuste do estoque (deve reduzir mais 1 unidade)
            Integer estoqueAposAtualizacao = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return produtoService.buscarPorId(produto1.getId()).getEstoque();
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            assertEquals(estoqueAposCreacao - 1, estoqueAposAtualizacao, 
                "Estoque deve ser ajustado conforme diferença");
        }

        @Test
        @DisplayName("Deve atualizar status do pedido")
        void deveAtualizarStatusDoPedido() {
            // Given
            Pedido pedido = TestDataBuilder.criarPedidoDefault(cliente);
            ItemPedido item = TestDataBuilder.criarItemPedido(pedido, produto1, 1, produto1.getPreco());
            pedido.getItens().add(item);
            
            Pedido pedidoSalvo = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return pedidoService.salvar(pedido);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // When
            Pedido pedidoAtualizado = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return pedidoService.atualizarStatus(pedidoSalvo.getId(), "Enviado");
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            assertEquals("Enviado", pedidoAtualizado.getStatus(), "Status deve ser atualizado");
        }
    }

    @Nested
    @DisplayName("Testes de exclusão de pedido")
    class ExclusaoPedidoTests {

        @Test
        @DisplayName("Deve excluir pedido e reverter estoque")
        void deveExcluirPedidoEReverterEstoque() {
            // Given
            Pedido pedido = TestDataBuilder.criarPedidoDefault(cliente);
            ItemPedido item = TestDataBuilder.criarItemPedido(pedido, produto1, 3, produto1.getPreco());
            pedido.getItens().add(item);
            
            Integer estoqueAnterior = produto1.getEstoque();
            
            Pedido pedidoSalvo = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return pedidoService.salvar(pedido);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // When
            TestJPAUtil.executeInTransaction(() -> {
                try {
                    pedidoService.excluir(pedidoSalvo.getId());
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            // Verificar se pedido foi excluído
            Pedido pedidoExcluido = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return pedidoService.buscarPorId(pedidoSalvo.getId());
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            assertNull(pedidoExcluido, "Pedido deve ter sido excluído");
            
            // Verificar se estoque foi revertido
            Produto produtoAtualizado = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return produtoService.buscarPorId(produto1.getId());
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            assertEquals(estoqueAnterior, produtoAtualizado.getEstoque(), 
                "Estoque deve ser revertido após exclusão");
        }

        @Test
        @DisplayName("Deve falhar ao excluir pedido inexistente")
        void deveFalharAoExcluirPedidoInexistente() {
            // When & Then
            ServiceException exception = assertThrows(ServiceException.class, () -> {
                pedidoService.excluir(999L);
            });
            
            assertTrue(exception.getMessage().contains("não encontrado"), 
                "Mensagem deve indicar que pedido não foi encontrado");
        }
    }

    @Nested
    @DisplayName("Testes de consulta de pedidos")
    class ConsultaPedidosTests {

        @Test
        @DisplayName("Deve listar pedidos por cliente")
        void deveListarPedidosPorCliente() {
            // Given
            Pedido pedido1 = TestDataBuilder.criarPedidoDefault(cliente);
            ItemPedido item1 = TestDataBuilder.criarItemPedido(pedido1, produto1, 1, produto1.getPreco());
            pedido1.getItens().add(item1);
            
            Pedido pedido2 = TestDataBuilder.criarPedidoDefault(cliente);
            ItemPedido item2 = TestDataBuilder.criarItemPedido(pedido2, produto2, 1, produto2.getPreco());
            pedido2.getItens().add(item2);
            
            TestJPAUtil.executeInTransaction(() -> {
                try {
                    pedidoService.salvar(pedido1);
                    pedidoService.salvar(pedido2);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // When
            List<Pedido> pedidos = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return pedidoService.listarPorCliente(cliente.getId());
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            assertEquals(2, pedidos.size(), "Deve retornar 2 pedidos do cliente");
        }

        @Test
        @DisplayName("Deve listar pedidos por status")
        void deveListarPedidosPorStatus() {
            // Given
            Pedido pedido1 = TestDataBuilder.criarPedido(cliente, "Pendente");
            ItemPedido item1 = TestDataBuilder.criarItemPedido(pedido1, produto1, 1, produto1.getPreco());
            pedido1.getItens().add(item1);
            
            Pedido pedido2 = TestDataBuilder.criarPedido(cliente, "Enviado");
            ItemPedido item2 = TestDataBuilder.criarItemPedido(pedido2, produto2, 1, produto2.getPreco());
            pedido2.getItens().add(item2);
            
            TestJPAUtil.executeInTransaction(() -> {
                try {
                    pedidoService.salvar(pedido1);
                    pedidoService.salvar(pedido2);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // When
            List<Pedido> pedidosPendentes = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return pedidoService.listarPorStatus("Pendente");
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            assertEquals(1, pedidosPendentes.size(), "Deve retornar 1 pedido pendente");
            assertEquals("Pendente", pedidosPendentes.get(0).getStatus());
        }
    }
}

