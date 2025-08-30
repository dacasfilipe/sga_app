package com.seuteste.sga.util;

import com.seuteste.sga.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder para criação de dados de teste.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class TestDataBuilder {

    /**
     * Cria uma categoria de teste.
     */
    public static Categoria criarCategoria(String nome) {
        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        return categoria;
    }

    /**
     * Cria uma categoria padrão para testes.
     */
    public static Categoria criarCategoriaDefault() {
        return criarCategoria("Categoria Teste");
    }

    /**
     * Cria um produto de teste.
     */
    public static Produto criarProduto(String nome, BigDecimal preco, Integer estoque, Categoria categoria) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao("Descrição do " + nome);
        produto.setPreco(preco);
        produto.setEstoque(estoque);
        produto.setCategoria(categoria);
        return produto;
    }

    /**
     * Cria um produto padrão para testes.
     */
    public static Produto criarProdutoDefault(Categoria categoria) {
        return criarProduto("Produto Teste", new BigDecimal("99.99"), 10, categoria);
    }

    /**
     * Cria um cliente de teste.
     */
    public static Cliente criarCliente(String nome, String email, String telefone, String endereco) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setEmail(email);
        cliente.setTelefone(telefone);
        cliente.setEndereco(endereco);
        return cliente;
    }

    /**
     * Cria um cliente padrão para testes.
     */
    public static Cliente criarClienteDefault() {
        return criarCliente("Cliente Teste", "cliente.teste@email.com", "(11) 99999-9999", "Rua Teste, 123");
    }

    /**
     * Cria um usuário de teste.
     */
    public static Usuario criarUsuario(String nome, String email, String senha, Usuario.Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha); // Será criptografada pelo serviço
        usuario.setPerfil(perfil);
        return usuario;
    }

    /**
     * Cria um usuário administrador padrão para testes.
     */
    public static Usuario criarUsuarioAdminDefault() {
        return criarUsuario("Admin Teste", "admin.teste@email.com", "senha123", Usuario.Perfil.ADMIN);
    }

    /**
     * Cria um usuário operador padrão para testes.
     */
    public static Usuario criarUsuarioOperadorDefault() {
        return criarUsuario("Operador Teste", "operador.teste@email.com", "senha123", Usuario.Perfil.OPERADOR);
    }

    /**
     * Cria um pedido de teste.
     */
    public static Pedido criarPedido(Cliente cliente, String status) {
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(status);
        pedido.setItens(new ArrayList<>());
        return pedido;
    }

    /**
     * Cria um pedido padrão para testes.
     */
    public static Pedido criarPedidoDefault(Cliente cliente) {
        return criarPedido(cliente, "Pendente");
    }

    /**
     * Cria um item de pedido de teste.
     */
    public static ItemPedido criarItemPedido(Pedido pedido, Produto produto, Integer quantidade, BigDecimal precoUnitario) {
        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(precoUnitario);
        return item;
    }

    /**
     * Cria um item de pedido padrão para testes.
     */
    public static ItemPedido criarItemPedidoDefault(Pedido pedido, Produto produto) {
        return criarItemPedido(pedido, produto, 2, produto.getPreco());
    }

    /**
     * Cria um pedido completo com itens para testes.
     */
    public static Pedido criarPedidoComItens(Cliente cliente, List<Produto> produtos) {
        Pedido pedido = criarPedidoDefault(cliente);
        
        for (Produto produto : produtos) {
            ItemPedido item = criarItemPedidoDefault(pedido, produto);
            pedido.getItens().add(item);
        }
        
        return pedido;
    }

    /**
     * Cria múltiplas categorias para testes.
     */
    public static List<Categoria> criarCategorias(String... nomes) {
        List<Categoria> categorias = new ArrayList<>();
        for (String nome : nomes) {
            categorias.add(criarCategoria(nome));
        }
        return categorias;
    }

    /**
     * Cria múltiplos produtos para testes.
     */
    public static List<Produto> criarProdutos(Categoria categoria, int quantidade) {
        List<Produto> produtos = new ArrayList<>();
        for (int i = 1; i <= quantidade; i++) {
            produtos.add(criarProduto(
                "Produto " + i,
                new BigDecimal(String.valueOf(10.00 * i)),
                5 + i,
                categoria
            ));
        }
        return produtos;
    }

    /**
     * Cria múltiplos clientes para testes.
     */
    public static List<Cliente> criarClientes(int quantidade) {
        List<Cliente> clientes = new ArrayList<>();
        for (int i = 1; i <= quantidade; i++) {
            clientes.add(criarCliente(
                "Cliente " + i,
                "cliente" + i + "@teste.com",
                "(11) 9999-999" + i,
                "Rua " + i + ", " + (100 + i)
            ));
        }
        return clientes;
    }

    /**
     * Cria múltiplos usuários para testes.
     */
    public static List<Usuario> criarUsuarios(int quantidade) {
        List<Usuario> usuarios = new ArrayList<>();
        for (int i = 1; i <= quantidade; i++) {
            Usuario.Perfil perfil = (i % 2 == 0) ? Usuario.Perfil.ADMIN : Usuario.Perfil.OPERADOR;
            usuarios.add(criarUsuario(
                "Usuario " + i,
                "usuario" + i + "@teste.com",
                "senha" + i,
                perfil
            ));
        }
        return usuarios;
    }
}

