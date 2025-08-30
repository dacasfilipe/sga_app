package com.seuteste.sga.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidade que representa um pedido no sistema.
 * 
 * @author SGA Team
 * @version 1.0
 */
@Entity
@Table(name = "pedido")
public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O cliente é obrigatório")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "data_pedido", nullable = false)
    private LocalDate dataPedido;

    @NotBlank(message = "O status do pedido é obrigatório")
    @Size(max = 50, message = "O status não pode exceder 50 caracteres")
    @Column(name = "status", nullable = false)
    private String status;

    @NotNull(message = "O valor total é obrigatório")
    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemPedido> itens;

    // Construtores
    public Pedido() {
        this.dataPedido = LocalDate.now();
        this.status = "Pendente";
        this.valorTotal = BigDecimal.ZERO;
        this.itens = new ArrayList<>();
    }

    public Pedido(Cliente cliente) {
        this();
        this.cliente = cliente;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDate dataPedido) {
        this.dataPedido = dataPedido;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    // Métodos de negócio
    public void adicionarItem(ItemPedido item) {
        if (item != null) {
            item.setPedido(this);
            this.itens.add(item);
            calcularValorTotal();
        }
    }

    public void removerItem(ItemPedido item) {
        if (item != null && this.itens.remove(item)) {
            item.setPedido(null);
            calcularValorTotal();
        }
    }

    public void calcularValorTotal() {
        this.valorTotal = this.itens.stream()
                .map(ItemPedido::getSubtotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Métodos equals, hashCode e toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(id, pedido.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", cliente=" + (cliente != null ? cliente.getNome() : "null") +
                ", dataPedido=" + dataPedido +
                ", status=\'" + status + '\'' +
                ", valorTotal=" + valorTotal +
                '}';
    }
}

