package com.seuteste.sga.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade que representa um usuário do sistema.
 * 
 * @author SGA Team
 * @version 2.0
 */
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Enum para representar os perfis de usuário do sistema.
     */
    public enum Perfil {
        ADMIN("Administrador"),
        OPERADOR("Operador");

        private final String descricao;

        Perfil(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 255, message = "O nome deve ter entre 2 e 255 caracteres")
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email deve ter um formato válido")
    @Size(max = 255, message = "O email não pode exceder 255 caracteres")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 255, message = "A senha deve ter entre 6 e 255 caracteres")
    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @NotNull(message = "O perfil é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false)
    private Perfil perfil;

    // Construtores
    public Usuario() {
        this.dataCadastro = LocalDate.now();
        this.perfil = Perfil.OPERADOR; // Perfil padrão
    }

    public Usuario(String nome, String email, String senha) {
        this();
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Usuario(String nome, String email, String senha, Perfil perfil) {
        this(nome, email, senha);
        this.perfil = perfil;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    // Métodos de negócio
    public String getPrimeiroNome() {
        if (nome != null && !nome.trim().isEmpty()) {
            String[] partes = nome.trim().split("\\s+");
            return partes[0];
        }
        return "";
    }

    public String getIniciais() {
        if (nome != null && !nome.trim().isEmpty()) {
            String[] partes = nome.trim().split("\\s+");
            StringBuilder iniciais = new StringBuilder();
            for (String parte : partes) {
                if (!parte.isEmpty()) {
                    iniciais.append(parte.charAt(0));
                }
            }
            return iniciais.toString().toUpperCase();
        }
        return "";
    }

    /**
     * Verifica se o usuário é administrador.
     * @return true se for administrador, false caso contrário
     */
    public boolean isAdmin() {
        return Perfil.ADMIN.equals(this.perfil);
    }

    /**
     * Verifica se o usuário é operador.
     * @return true se for operador, false caso contrário
     */
    public boolean isOperador() {
        return Perfil.OPERADOR.equals(this.perfil);
    }

    // Métodos equals, hashCode e toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", perfil=" + perfil +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}

