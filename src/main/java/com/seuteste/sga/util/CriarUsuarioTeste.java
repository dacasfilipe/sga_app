package com.seuteste.sga.util;

import com.seuteste.sga.model.Usuario;
import com.seuteste.sga.dao.impl.UsuarioDAOImpl;
import com.seuteste.sga.dao.UsuarioDAO;

/**
 * Utilitário para criar usuário de teste
 */
public class CriarUsuarioTeste {
    
    public static void main(String[] args) {
        try {
            // Criar usuário de teste
            Usuario usuario = new Usuario();
            usuario.setNome("Admin SGA");
            usuario.setEmail("admin@sga.com");
            usuario.setSenha(CriptografiaUtil.criptografarSenha("123456"));
            usuario.setPerfil(Usuario.Perfil.ADMIN);
            usuario.setAtivo(true);
            
            // Salvar no banco
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
            Usuario usuarioSalvo = usuarioDAO.save(usuario);
            
            System.out.println("Usuário de teste criado com sucesso!");
            System.out.println("ID: " + usuarioSalvo.getId());
            System.out.println("Nome: " + usuarioSalvo.getNome());
            System.out.println("Email: " + usuarioSalvo.getEmail());
            System.out.println("Perfil: " + usuarioSalvo.getPerfil());
            
            // Criar outro usuário operador
            Usuario operador = new Usuario();
            operador.setNome("Operador Teste");
            operador.setEmail("operador@sga.com");
            operador.setSenha(CriptografiaUtil.criptografarSenha("operador123"));
            operador.setPerfil(Usuario.Perfil.OPERADOR);
            operador.setAtivo(true);
            
            Usuario operadorSalvo = usuarioDAO.save(operador);
            
            System.out.println("\nOperador de teste criado com sucesso!");
            System.out.println("ID: " + operadorSalvo.getId());
            System.out.println("Nome: " + operadorSalvo.getNome());
            System.out.println("Email: " + operadorSalvo.getEmail());
            System.out.println("Perfil: " + operadorSalvo.getPerfil());
            
        } catch (Exception e) {
            System.err.println("Erro ao criar usuário de teste: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
