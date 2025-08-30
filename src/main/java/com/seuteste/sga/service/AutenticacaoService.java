package com.seuteste.sga.service;

import com.seuteste.sga.dao.DAOException;
import com.seuteste.sga.dao.UsuarioDAO;
import com.seuteste.sga.dao.impl.UsuarioDAOImpl;
import com.seuteste.sga.model.Usuario;
import com.seuteste.sga.util.CriptografiaUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Serviço responsável pela autenticação de usuários.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class AutenticacaoService {

    private static final Logger LOGGER = Logger.getLogger(AutenticacaoService.class.getName());

    private UsuarioDAO usuarioDAO;

    public AutenticacaoService() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    /**
     * Autentica um usuário com email e senha.
     * 
     * @param email Email do usuário
     * @param senha Senha em texto plano
     * @return O usuário autenticado ou null se as credenciais forem inválidas
     * @throws ServiceException Em caso de erro no processo de autenticação
     */
    public Usuario autenticar(String email, String senha) throws ServiceException {
        try {
            LOGGER.info("Tentativa de autenticação para email: " + email);

            // Validar parâmetros
            if (email == null || email.trim().isEmpty()) {
                throw new ServiceException("Email é obrigatório para autenticação.");
            }

            if (senha == null || senha.trim().isEmpty()) {
                throw new ServiceException("Senha é obrigatória para autenticação.");
            }

            // Buscar usuário por email
            Usuario usuario = usuarioDAO.findByEmail(email.trim());
            
            if (usuario == null) {
                LOGGER.warning("Usuário não encontrado para email: " + email);
                return null; // Usuário não existe
            }

            // Verificar senha
            boolean senhaCorreta = CriptografiaUtil.verificarSenha(senha, usuario.getSenha());
            
            if (senhaCorreta) {
                LOGGER.info("Autenticação bem-sucedida para usuário: " + usuario.getNome());
                return usuario;
            } else {
                LOGGER.warning("Senha incorreta para email: " + email);
                return null; // Senha incorreta
            }

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao acessar dados durante autenticação", e);
            throw new ServiceException("Erro interno durante autenticação: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado durante autenticação", e);
            throw new ServiceException("Erro inesperado durante autenticação: " + e.getMessage(), e);
        }
    }

    /**
     * Registra um novo usuário no sistema.
     * 
     * @param usuario Usuário a ser registrado
     * @return O usuário registrado com senha criptografada
     * @throws ServiceException Em caso de erro no registro
     */
    public Usuario registrarUsuario(Usuario usuario) throws ServiceException {
        try {
            LOGGER.info("Iniciando registro de novo usuário: " + usuario.getEmail());

            // Validar dados do usuário
            validarDadosUsuario(usuario);

            // Verificar se email já existe
            Usuario usuarioExistente = usuarioDAO.findByEmail(usuario.getEmail());
            if (usuarioExistente != null) {
                throw new ServiceException("Já existe um usuário cadastrado com este email.");
            }

            // Criptografar senha
            String senhaCriptografada = CriptografiaUtil.criptografarSenha(usuario.getSenha());
            usuario.setSenha(senhaCriptografada);

            // Salvar usuário
            Usuario usuarioSalvo = usuarioDAO.save(usuario);
            
            LOGGER.info("Usuário registrado com sucesso: " + usuarioSalvo.getEmail());
            return usuarioSalvo;

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao salvar usuário", e);
            throw new ServiceException("Erro ao registrar usuário: " + e.getMessage(), e);
        }
    }

    /**
     * Altera a senha de um usuário.
     * 
     * @param usuarioId ID do usuário
     * @param senhaAtual Senha atual em texto plano
     * @param novaSenha Nova senha em texto plano
     * @throws ServiceException Em caso de erro na alteração
     */
    public void alterarSenha(Long usuarioId, String senhaAtual, String novaSenha) throws ServiceException {
        try {
            LOGGER.info("Iniciando alteração de senha para usuário ID: " + usuarioId);

            // Validar parâmetros
            if (usuarioId == null) {
                throw new ServiceException("ID do usuário é obrigatório.");
            }

            if (senhaAtual == null || senhaAtual.trim().isEmpty()) {
                throw new ServiceException("Senha atual é obrigatória.");
            }

            if (novaSenha == null || novaSenha.trim().isEmpty()) {
                throw new ServiceException("Nova senha é obrigatória.");
            }

            if (novaSenha.length() < 6) {
                throw new ServiceException("Nova senha deve ter pelo menos 6 caracteres.");
            }

            // Buscar usuário
            Usuario usuario = usuarioDAO.findById(usuarioId);
            if (usuario == null) {
                throw new ServiceException("Usuário não encontrado.");
            }

            // Verificar senha atual
            boolean senhaAtualCorreta = CriptografiaUtil.verificarSenha(senhaAtual, usuario.getSenha());
            if (!senhaAtualCorreta) {
                throw new ServiceException("Senha atual incorreta.");
            }

            // Criptografar nova senha
            String novaSenhaCriptografada = CriptografiaUtil.criptografarSenha(novaSenha);
            usuario.setSenha(novaSenhaCriptografada);

            // Atualizar usuário
            usuarioDAO.update(usuario);
            
            LOGGER.info("Senha alterada com sucesso para usuário: " + usuario.getEmail());

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao alterar senha", e);
            throw new ServiceException("Erro ao alterar senha: " + e.getMessage(), e);
        }
    }

    /**
     * Redefine a senha de um usuário (para uso administrativo).
     * 
     * @param usuarioId ID do usuário
     * @param novaSenha Nova senha em texto plano
     * @throws ServiceException Em caso de erro na redefinição
     */
    public void redefinirSenha(Long usuarioId, String novaSenha) throws ServiceException {
        try {
            LOGGER.info("Iniciando redefinição de senha para usuário ID: " + usuarioId);

            // Validar parâmetros
            if (usuarioId == null) {
                throw new ServiceException("ID do usuário é obrigatório.");
            }

            if (novaSenha == null || novaSenha.trim().isEmpty()) {
                throw new ServiceException("Nova senha é obrigatória.");
            }

            if (novaSenha.length() < 6) {
                throw new ServiceException("Nova senha deve ter pelo menos 6 caracteres.");
            }

            // Buscar usuário
            Usuario usuario = usuarioDAO.findById(usuarioId);
            if (usuario == null) {
                throw new ServiceException("Usuário não encontrado.");
            }

            // Criptografar nova senha
            String novaSenhaCriptografada = CriptografiaUtil.criptografarSenha(novaSenha);
            usuario.setSenha(novaSenhaCriptografada);

            // Atualizar usuário
            usuarioDAO.update(usuario);
            
            LOGGER.info("Senha redefinida com sucesso para usuário: " + usuario.getEmail());

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao redefinir senha", e);
            throw new ServiceException("Erro ao redefinir senha: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se um email já está em uso.
     * 
     * @param email Email a ser verificado
     * @return true se o email já estiver em uso, false caso contrário
     * @throws ServiceException Em caso de erro na verificação
     */
    public boolean emailJaExiste(String email) throws ServiceException {
        try {
            if (email == null || email.trim().isEmpty()) {
                return false;
            }

            Usuario usuario = usuarioDAO.findByEmail(email.trim());
            return usuario != null;

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao verificar email", e);
            throw new ServiceException("Erro ao verificar email: " + e.getMessage(), e);
        }
    }

    /**
     * Valida os dados de um usuário.
     * 
     * @param usuario Usuário a ser validado
     * @throws ServiceException Se os dados forem inválidos
     */
    private void validarDadosUsuario(Usuario usuario) throws ServiceException {
        if (usuario == null) {
            throw new ServiceException("Dados do usuário são obrigatórios.");
        }

        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new ServiceException("Nome é obrigatório.");
        }

        if (usuario.getNome().trim().length() < 2) {
            throw new ServiceException("Nome deve ter pelo menos 2 caracteres.");
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new ServiceException("Email é obrigatório.");
        }

        if (!isEmailValido(usuario.getEmail())) {
            throw new ServiceException("Email deve ter um formato válido.");
        }

        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new ServiceException("Senha é obrigatória.");
        }

        if (usuario.getSenha().length() < 6) {
            throw new ServiceException("Senha deve ter pelo menos 6 caracteres.");
        }

        if (usuario.getPerfil() == null) {
            throw new ServiceException("Perfil é obrigatório.");
        }
    }

    /**
     * Valida o formato de um email.
     * 
     * @param email Email a ser validado
     * @return true se o email for válido, false caso contrário
     */
    private boolean isEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Regex simples para validação de email
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
}

