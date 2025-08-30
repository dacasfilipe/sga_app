package com.seuteste.sga.service;

import com.seuteste.sga.dao.UsuarioDAO;
import com.seuteste.sga.dao.impl.UsuarioDAOImpl;
import com.seuteste.sga.model.Usuario;
import com.seuteste.sga.util.CriptografiaUtil;
import com.seuteste.sga.util.TestDataBuilder;
import com.seuteste.sga.util.TestJPAUtil;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes de integração para AutenticacaoService.
 * 
 * @author SGA Team
 * @version 1.0
 */
@DisplayName("Testes do AutenticacaoService")
@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    private AutenticacaoService autenticacaoService;
    
    @Mock
    private UsuarioDAO usuarioDAOMock;
    
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Configurar EntityManager para testes
        entityManager = TestJPAUtil.createEntityManagerH2();
        TestJPAUtil.setCurrentEntityManager(entityManager);
        
        // Criar serviço com DAO real para testes de integração
        autenticacaoService = new AutenticacaoService();
    }

    @AfterEach
    void tearDown() {
        TestJPAUtil.removeCurrentEntityManager();
    }

    @AfterAll
    static void tearDownAll() {
        TestJPAUtil.closeAll();
    }

    @Nested
    @DisplayName("Testes de registro de usuário")
    class RegistroUsuarioTests {

        @Test
        @DisplayName("Deve registrar novo usuário com sucesso")
        void deveRegistrarNovoUsuarioComSucesso() {
            // Given
            Usuario novoUsuario = TestDataBuilder.criarUsuarioAdminDefault();
            
            // When
            Usuario usuarioSalvo = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return autenticacaoService.registrarUsuario(novoUsuario);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            assertNotNull(usuarioSalvo, "Usuário salvo não deve ser nulo");
            assertNotNull(usuarioSalvo.getId(), "ID do usuário deve ser gerado");
            assertEquals(novoUsuario.getNome(), usuarioSalvo.getNome());
            assertEquals(novoUsuario.getEmail(), usuarioSalvo.getEmail());
            assertEquals(novoUsuario.getPerfil(), usuarioSalvo.getPerfil());
            
            // Verificar se a senha foi criptografada
            assertNotEquals(novoUsuario.getSenha(), usuarioSalvo.getSenha(), 
                "Senha deve ter sido criptografada");
            assertTrue(CriptografiaUtil.verificarSenha(novoUsuario.getSenha(), usuarioSalvo.getSenha()),
                "Senha criptografada deve ser verificável");
        }

        @Test
        @DisplayName("Deve falhar ao registrar usuário com email duplicado")
        void deveFalharAoRegistrarUsuarioComEmailDuplicado() {
            // Given
            Usuario usuario1 = TestDataBuilder.criarUsuarioAdminDefault();
            Usuario usuario2 = TestDataBuilder.criarUsuarioOperadorDefault();
            usuario2.setEmail(usuario1.getEmail()); // Mesmo email
            
            // When & Then
            TestJPAUtil.executeInTransaction(() -> {
                try {
                    autenticacaoService.registrarUsuario(usuario1);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            ServiceException exception = assertThrows(ServiceException.class, () -> {
                TestJPAUtil.executeInTransaction(() -> {
                    try {
                        autenticacaoService.registrarUsuario(usuario2);
                    } catch (ServiceException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            
            assertTrue(exception.getMessage().contains("já existe"), 
                "Mensagem deve indicar que email já existe");
        }

        @Test
        @DisplayName("Deve falhar ao registrar usuário com dados inválidos")
        void deveFalharAoRegistrarUsuarioComDadosInvalidos() {
            // Given
            Usuario usuarioInvalido = new Usuario();
            usuarioInvalido.setNome("A"); // Nome muito curto
            usuarioInvalido.setEmail("email-invalido"); // Email inválido
            usuarioInvalido.setSenha("123"); // Senha muito curta
            usuarioInvalido.setPerfil(Usuario.Perfil.OPERADOR);
            
            // When & Then
            ServiceException exception = assertThrows(ServiceException.class, () -> {
                autenticacaoService.registrarUsuario(usuarioInvalido);
            });
            
            assertNotNull(exception.getMessage(), "Deve ter mensagem de erro");
        }

        @Test
        @DisplayName("Deve falhar ao registrar usuário nulo")
        void deveFalharAoRegistrarUsuarioNulo() {
            // When & Then
            ServiceException exception = assertThrows(ServiceException.class, () -> {
                autenticacaoService.registrarUsuario(null);
            });
            
            assertEquals("Dados do usuário são obrigatórios", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes de autenticação")
    class AutenticacaoTests {

        @Test
        @DisplayName("Deve autenticar usuário com credenciais corretas")
        void deveAutenticarUsuarioComCredenciaisCorretas() {
            // Given
            Usuario usuario = TestDataBuilder.criarUsuarioAdminDefault();
            String senhaOriginal = usuario.getSenha();
            
            Usuario usuarioSalvo = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return autenticacaoService.registrarUsuario(usuario);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // When
            Usuario usuarioAutenticado = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return autenticacaoService.autenticar(usuarioSalvo.getEmail(), senhaOriginal);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            assertNotNull(usuarioAutenticado, "Usuário deve ser autenticado");
            assertEquals(usuarioSalvo.getId(), usuarioAutenticado.getId());
            assertEquals(usuarioSalvo.getEmail(), usuarioAutenticado.getEmail());
        }

        @Test
        @DisplayName("Deve falhar ao autenticar com email inexistente")
        void deveFalharAoAutenticarComEmailInexistente() {
            // When
            Usuario resultado = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return autenticacaoService.autenticar("email.inexistente@teste.com", "qualquersenha");
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            assertNull(resultado, "Deve retornar null para email inexistente");
        }

        @Test
        @DisplayName("Deve falhar ao autenticar com senha incorreta")
        void deveFalharAoAutenticarComSenhaIncorreta() {
            // Given
            Usuario usuario = TestDataBuilder.criarUsuarioAdminDefault();
            
            Usuario usuarioSalvo = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return autenticacaoService.registrarUsuario(usuario);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // When
            Usuario resultado = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return autenticacaoService.autenticar(usuarioSalvo.getEmail(), "senhaerrada");
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            assertNull(resultado, "Deve retornar null para senha incorreta");
        }

        @Test
        @DisplayName("Deve falhar ao autenticar com parâmetros nulos")
        void deveFalharAoAutenticarComParametrosNulos() {
            // When & Then
            ServiceException exception1 = assertThrows(ServiceException.class, () -> {
                autenticacaoService.autenticar(null, "senha");
            });
            
            ServiceException exception2 = assertThrows(ServiceException.class, () -> {
                autenticacaoService.autenticar("email@teste.com", null);
            });
            
            assertEquals("Email e senha são obrigatórios", exception1.getMessage());
            assertEquals("Email e senha são obrigatórios", exception2.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes de alteração de senha")
    class AlteracaoSenhaTests {

        @Test
        @DisplayName("Deve alterar senha com senha atual correta")
        void deveAlterarSenhaComSenhaAtualCorreta() {
            // Given
            Usuario usuario = TestDataBuilder.criarUsuarioAdminDefault();
            String senhaOriginal = usuario.getSenha();
            String novaSenha = "novasenha123";
            
            Usuario usuarioSalvo = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return autenticacaoService.registrarUsuario(usuario);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // When
            TestJPAUtil.executeInTransaction(() -> {
                try {
                    autenticacaoService.alterarSenha(usuarioSalvo.getId(), senhaOriginal, novaSenha);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then - Verificar se pode autenticar com nova senha
            Usuario usuarioAutenticado = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return autenticacaoService.autenticar(usuarioSalvo.getEmail(), novaSenha);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            assertNotNull(usuarioAutenticado, "Deve autenticar com nova senha");
            
            // Verificar se não autentica mais com senha antiga
            Usuario resultadoSenhaAntiga = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return autenticacaoService.autenticar(usuarioSalvo.getEmail(), senhaOriginal);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            assertNull(resultadoSenhaAntiga, "Não deve autenticar com senha antiga");
        }

        @Test
        @DisplayName("Deve falhar ao alterar senha com senha atual incorreta")
        void deveFalharAoAlterarSenhaComSenhaAtualIncorreta() {
            // Given
            Usuario usuario = TestDataBuilder.criarUsuarioAdminDefault();
            
            Usuario usuarioSalvo = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return autenticacaoService.registrarUsuario(usuario);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // When & Then
            ServiceException exception = assertThrows(ServiceException.class, () -> {
                TestJPAUtil.executeInTransaction(() -> {
                    try {
                        autenticacaoService.alterarSenha(usuarioSalvo.getId(), "senhaerrada", "novasenha");
                    } catch (ServiceException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            
            assertTrue(exception.getMessage().contains("incorreta"), 
                "Mensagem deve indicar senha atual incorreta");
        }

        @Test
        @DisplayName("Deve falhar ao alterar senha de usuário inexistente")
        void deveFalharAoAlterarSenhaDeUsuarioInexistente() {
            // When & Then
            ServiceException exception = assertThrows(ServiceException.class, () -> {
                autenticacaoService.alterarSenha(999L, "qualquersenha", "novasenha");
            });
            
            assertTrue(exception.getMessage().contains("não encontrado"), 
                "Mensagem deve indicar usuário não encontrado");
        }
    }

    @Nested
    @DisplayName("Testes de redefinição de senha")
    class RedefinicaoSenhaTests {

        @Test
        @DisplayName("Deve redefinir senha de usuário existente")
        void deveRedefinirSenhaDeUsuarioExistente() {
            // Given
            Usuario usuario = TestDataBuilder.criarUsuarioAdminDefault();
            String novaSenha = "senharedefinida123";
            
            Usuario usuarioSalvo = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return autenticacaoService.registrarUsuario(usuario);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // When
            TestJPAUtil.executeInTransaction(() -> {
                try {
                    autenticacaoService.redefinirSenha(usuarioSalvo.getId(), novaSenha);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then - Verificar se pode autenticar com nova senha
            Usuario usuarioAutenticado = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return autenticacaoService.autenticar(usuarioSalvo.getEmail(), novaSenha);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            assertNotNull(usuarioAutenticado, "Deve autenticar com senha redefinida");
        }

        @Test
        @DisplayName("Deve falhar ao redefinir senha de usuário inexistente")
        void deveFalharAoRedefinirSenhaDeUsuarioInexistente() {
            // When & Then
            ServiceException exception = assertThrows(ServiceException.class, () -> {
                autenticacaoService.redefinirSenha(999L, "novasenha");
            });
            
            assertTrue(exception.getMessage().contains("não encontrado"), 
                "Mensagem deve indicar usuário não encontrado");
        }
    }

    @Nested
    @DisplayName("Testes de verificação de email")
    class VerificacaoEmailTests {

        @Test
        @DisplayName("Deve retornar true para email existente")
        void deveRetornarTrueParaEmailExistente() {
            // Given
            Usuario usuario = TestDataBuilder.criarUsuarioAdminDefault();
            
            TestJPAUtil.executeInTransaction(() -> {
                try {
                    autenticacaoService.registrarUsuario(usuario);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // When
            boolean existe = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return autenticacaoService.emailJaExiste(usuario.getEmail());
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            assertTrue(existe, "Deve retornar true para email existente");
        }

        @Test
        @DisplayName("Deve retornar false para email inexistente")
        void deveRetornarFalseParaEmailInexistente() {
            // When
            boolean existe = TestJPAUtil.executeInTransaction(() -> {
                try {
                    return autenticacaoService.emailJaExiste("email.inexistente@teste.com");
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            });
            
            // Then
            assertFalse(existe, "Deve retornar false para email inexistente");
        }
    }
}

