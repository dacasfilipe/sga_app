package com.seuteste.sga.api.resource;

import com.seuteste.sga.model.Usuario;
import com.seuteste.sga.service.AutenticacaoService;
import com.seuteste.sga.service.ServiceException;
import com.seuteste.sga.util.TestDataBuilder;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para UsuarioResource.
 * 
 * @author SGA Team
 * @version 1.0
 */
@DisplayName("Testes da API UsuarioResource")
@ExtendWith(MockitoExtension.class)
class UsuarioResourceTest {

    @Mock
    private AutenticacaoService autenticacaoServiceMock;

    @InjectMocks
    private UsuarioResource usuarioResource;

    @Nested
    @DisplayName("Testes de registro de usuário")
    class RegistroUsuarioTests {

        @Test
        @DisplayName("Deve registrar usuário com sucesso")
        void deveRegistrarUsuarioComSucesso() throws ServiceException {
            // Given
            Usuario novoUsuario = TestDataBuilder.criarUsuarioAdminDefault();
            Usuario usuarioSalvo = TestDataBuilder.criarUsuarioAdminDefault();
            usuarioSalvo.setId(1L);
            usuarioSalvo.setSenha(null); // Senha removida por segurança
            
            when(autenticacaoServiceMock.registrarUsuario(any(Usuario.class)))
                .thenReturn(usuarioSalvo);
            
            // When
            Response response = usuarioResource.registrar(novoUsuario);
            
            // Then
            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            
            Usuario usuarioResposta = (Usuario) response.getEntity();
            assertNotNull(usuarioResposta);
            assertEquals(usuarioSalvo.getId(), usuarioResposta.getId());
            assertEquals(usuarioSalvo.getNome(), usuarioResposta.getNome());
            assertEquals(usuarioSalvo.getEmail(), usuarioResposta.getEmail());
            assertNull(usuarioResposta.getSenha(), "Senha não deve ser retornada");
            
            verify(autenticacaoServiceMock).registrarUsuario(novoUsuario);
        }

        @Test
        @DisplayName("Deve retornar erro 400 para usuário nulo")
        void deveRetornarErro400ParaUsuarioNulo() {
            // When
            Response response = usuarioResource.registrar(null);
            
            // Then
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertEquals("Dados do usuário são obrigatórios", response.getEntity());
            
            verifyNoInteractions(autenticacaoServiceMock);
        }

        @Test
        @DisplayName("Deve retornar erro 409 para email duplicado")
        void deveRetornarErro409ParaEmailDuplicado() throws ServiceException {
            // Given
            Usuario usuario = TestDataBuilder.criarUsuarioAdminDefault();
            
            when(autenticacaoServiceMock.registrarUsuario(any(Usuario.class)))
                .thenThrow(new ServiceException("Já existe um usuário com este email"));
            
            // When
            Response response = usuarioResource.registrar(usuario);
            
            // Then
            assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
            assertTrue(response.getEntity().toString().contains("já existe"));
        }
    }

    @Nested
    @DisplayName("Testes de autenticação")
    class AutenticacaoTests {

        @Test
        @DisplayName("Deve autenticar usuário com credenciais válidas")
        void deveAutenticarUsuarioComCredenciaisValidas() throws ServiceException {
            // Given
            Map<String, String> credenciais = new HashMap<>();
            credenciais.put("email", "admin@teste.com");
            credenciais.put("senha", "senha123");
            
            Usuario usuarioAutenticado = TestDataBuilder.criarUsuarioAdminDefault();
            usuarioAutenticado.setId(1L);
            usuarioAutenticado.setSenha(null); // Removida por segurança
            
            when(autenticacaoServiceMock.autenticar("admin@teste.com", "senha123"))
                .thenReturn(usuarioAutenticado);
            
            // When
            Response response = usuarioResource.autenticar(credenciais);
            
            // Then
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            
            @SuppressWarnings("unchecked")
            Map<String, Object> resposta = (Map<String, Object>) response.getEntity();
            assertNotNull(resposta);
            assertTrue(resposta.containsKey("usuario"));
            assertTrue(resposta.containsKey("mensagem"));
            assertTrue(resposta.containsKey("timestamp"));
            
            Usuario usuarioResposta = (Usuario) resposta.get("usuario");
            assertEquals(usuarioAutenticado.getId(), usuarioResposta.getId());
            assertNull(usuarioResposta.getSenha(), "Senha não deve ser retornada");
        }

        @Test
        @DisplayName("Deve retornar erro 401 para credenciais inválidas")
        void deveRetornarErro401ParaCredenciaisInvalidas() throws ServiceException {
            // Given
            Map<String, String> credenciais = new HashMap<>();
            credenciais.put("email", "admin@teste.com");
            credenciais.put("senha", "senhaerrada");
            
            when(autenticacaoServiceMock.autenticar("admin@teste.com", "senhaerrada"))
                .thenReturn(null);
            
            // When
            Response response = usuarioResource.autenticar(credenciais);
            
            // Then
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
            assertEquals("Email ou senha incorretos", response.getEntity());
        }

        @Test
        @DisplayName("Deve retornar erro 400 para credenciais nulas")
        void deveRetornarErro400ParaCredenciaisNulas() {
            // When
            Response response = usuarioResource.autenticar(null);
            
            // Then
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertEquals("Credenciais são obrigatórias", response.getEntity());
            
            verifyNoInteractions(autenticacaoServiceMock);
        }

        @Test
        @DisplayName("Deve retornar erro 400 para email vazio")
        void deveRetornarErro400ParaEmailVazio() {
            // Given
            Map<String, String> credenciais = new HashMap<>();
            credenciais.put("email", "");
            credenciais.put("senha", "senha123");
            
            // When
            Response response = usuarioResource.autenticar(credenciais);
            
            // Then
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertEquals("Email é obrigatório", response.getEntity());
            
            verifyNoInteractions(autenticacaoServiceMock);
        }

        @Test
        @DisplayName("Deve retornar erro 400 para senha vazia")
        void deveRetornarErro400ParaSenhaVazia() {
            // Given
            Map<String, String> credenciais = new HashMap<>();
            credenciais.put("email", "admin@teste.com");
            credenciais.put("senha", "");
            
            // When
            Response response = usuarioResource.autenticar(credenciais);
            
            // Then
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertEquals("Senha é obrigatória", response.getEntity());
            
            verifyNoInteractions(autenticacaoServiceMock);
        }
    }

    @Nested
    @DisplayName("Testes de alteração de senha")
    class AlteracaoSenhaTests {

        @Test
        @DisplayName("Deve alterar senha com sucesso")
        void deveAlterarSenhaComSucesso() throws ServiceException {
            // Given
            Long usuarioId = 1L;
            Map<String, String> dadosSenha = new HashMap<>();
            dadosSenha.put("senhaAtual", "senhaantiga");
            dadosSenha.put("novaSenha", "senhanova");
            
            doNothing().when(autenticacaoServiceMock)
                .alterarSenha(usuarioId, "senhaantiga", "senhanova");
            
            // When
            Response response = usuarioResource.alterarSenha(usuarioId, dadosSenha);
            
            // Then
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            
            @SuppressWarnings("unchecked")
            Map<String, Object> resposta = (Map<String, Object>) response.getEntity();
            assertEquals("Senha alterada com sucesso", resposta.get("mensagem"));
            
            verify(autenticacaoServiceMock).alterarSenha(usuarioId, "senhaantiga", "senhanova");
        }

        @Test
        @DisplayName("Deve retornar erro 400 para ID inválido")
        void deveRetornarErro400ParaIdInvalido() {
            // Given
            Map<String, String> dadosSenha = new HashMap<>();
            dadosSenha.put("senhaAtual", "senhaantiga");
            dadosSenha.put("novaSenha", "senhanova");
            
            // When
            Response response = usuarioResource.alterarSenha(0L, dadosSenha);
            
            // Then
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertEquals("ID deve ser um número positivo", response.getEntity());
            
            verifyNoInteractions(autenticacaoServiceMock);
        }

        @Test
        @DisplayName("Deve retornar erro 401 para senha atual incorreta")
        void deveRetornarErro401ParaSenhaAtualIncorreta() throws ServiceException {
            // Given
            Long usuarioId = 1L;
            Map<String, String> dadosSenha = new HashMap<>();
            dadosSenha.put("senhaAtual", "senhaerrada");
            dadosSenha.put("novaSenha", "senhanova");
            
            doThrow(new ServiceException("Senha atual incorreta"))
                .when(autenticacaoServiceMock)
                .alterarSenha(usuarioId, "senhaerrada", "senhanova");
            
            // When
            Response response = usuarioResource.alterarSenha(usuarioId, dadosSenha);
            
            // Then
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
            assertTrue(response.getEntity().toString().contains("incorreta"));
        }

        @Test
        @DisplayName("Deve retornar erro 404 para usuário não encontrado")
        void deveRetornarErro404ParaUsuarioNaoEncontrado() throws ServiceException {
            // Given
            Long usuarioId = 999L;
            Map<String, String> dadosSenha = new HashMap<>();
            dadosSenha.put("senhaAtual", "senhaantiga");
            dadosSenha.put("novaSenha", "senhanova");
            
            doThrow(new ServiceException("Usuário não encontrado"))
                .when(autenticacaoServiceMock)
                .alterarSenha(usuarioId, "senhaantiga", "senhanova");
            
            // When
            Response response = usuarioResource.alterarSenha(usuarioId, dadosSenha);
            
            // Then
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
            assertTrue(response.getEntity().toString().contains("não encontrado"));
        }
    }

    @Nested
    @DisplayName("Testes de redefinição de senha")
    class RedefinicaoSenhaTests {

        @Test
        @DisplayName("Deve redefinir senha com sucesso")
        void deveRedefinirSenhaComSucesso() throws ServiceException {
            // Given
            Long usuarioId = 1L;
            Map<String, String> dadosSenha = new HashMap<>();
            dadosSenha.put("novaSenha", "senhanova123");
            
            doNothing().when(autenticacaoServiceMock)
                .redefinirSenha(usuarioId, "senhanova123");
            
            // When
            Response response = usuarioResource.redefinirSenha(usuarioId, dadosSenha);
            
            // Then
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            
            @SuppressWarnings("unchecked")
            Map<String, Object> resposta = (Map<String, Object>) response.getEntity();
            assertEquals("Senha redefinida com sucesso", resposta.get("mensagem"));
            
            verify(autenticacaoServiceMock).redefinirSenha(usuarioId, "senhanova123");
        }

        @Test
        @DisplayName("Deve retornar erro 400 para dados nulos")
        void deveRetornarErro400ParaDadosNulos() {
            // When
            Response response = usuarioResource.redefinirSenha(1L, null);
            
            // Then
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertEquals("Dados para redefinição de senha são obrigatórios", response.getEntity());
            
            verifyNoInteractions(autenticacaoServiceMock);
        }

        @Test
        @DisplayName("Deve retornar erro 400 para nova senha vazia")
        void deveRetornarErro400ParaNovaSenhaVazia() {
            // Given
            Map<String, String> dadosSenha = new HashMap<>();
            dadosSenha.put("novaSenha", "");
            
            // When
            Response response = usuarioResource.redefinirSenha(1L, dadosSenha);
            
            // Then
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertEquals("Nova senha é obrigatória", response.getEntity());
            
            verifyNoInteractions(autenticacaoServiceMock);
        }
    }

    @Nested
    @DisplayName("Testes de verificação de email")
    class VerificacaoEmailTests {

        @Test
        @DisplayName("Deve verificar email existente")
        void deveVerificarEmailExistente() throws ServiceException {
            // Given
            String email = "usuario@teste.com";
            
            when(autenticacaoServiceMock.emailJaExiste(email))
                .thenReturn(true);
            
            // When
            Response response = usuarioResource.verificarEmail(email);
            
            // Then
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            
            @SuppressWarnings("unchecked")
            Map<String, Object> resposta = (Map<String, Object>) response.getEntity();
            assertEquals(email, resposta.get("email"));
            assertEquals(true, resposta.get("existe"));
            assertEquals(false, resposta.get("disponivel"));
            
            verify(autenticacaoServiceMock).emailJaExiste(email);
        }

        @Test
        @DisplayName("Deve verificar email disponível")
        void deveVerificarEmailDisponivel() throws ServiceException {
            // Given
            String email = "novo@teste.com";
            
            when(autenticacaoServiceMock.emailJaExiste(email))
                .thenReturn(false);
            
            // When
            Response response = usuarioResource.verificarEmail(email);
            
            // Then
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            
            @SuppressWarnings("unchecked")
            Map<String, Object> resposta = (Map<String, Object>) response.getEntity();
            assertEquals(email, resposta.get("email"));
            assertEquals(false, resposta.get("existe"));
            assertEquals(true, resposta.get("disponivel"));
        }

        @Test
        @DisplayName("Deve retornar erro 400 para email vazio")
        void deveRetornarErro400ParaEmailVazio() {
            // When
            Response response = usuarioResource.verificarEmail("");
            
            // Then
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertEquals("Email é obrigatório", response.getEntity());
            
            verifyNoInteractions(autenticacaoServiceMock);
        }

        @Test
        @DisplayName("Deve retornar erro 400 para email nulo")
        void deveRetornarErro400ParaEmailNulo() {
            // When
            Response response = usuarioResource.verificarEmail(null);
            
            // Then
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertEquals("Email é obrigatório", response.getEntity());
            
            verifyNoInteractions(autenticacaoServiceMock);
        }
    }
}

