package com.seuteste.sga.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe CriptografiaUtil.
 * 
 * @author SGA Team
 * @version 1.0
 */
@DisplayName("Testes da CriptografiaUtil")
class CriptografiaUtilTest {

    @Nested
    @DisplayName("Testes de criptografia de senha")
    class CriptografiaSenhaTests {

        @Test
        @DisplayName("Deve criptografar senha válida")
        void deveCriptografarSenhaValida() {
            // Given
            String senhaPlana = "minhasenha123";
            
            // When
            String senhaCriptografada = CriptografiaUtil.criptografarSenha(senhaPlana);
            
            // Then
            assertNotNull(senhaCriptografada, "Senha criptografada não deve ser nula");
            assertNotEquals(senhaPlana, senhaCriptografada, "Senha criptografada deve ser diferente da original");
            assertTrue(senhaCriptografada.startsWith("$2a$"), "Senha deve usar algoritmo BCrypt");
            assertTrue(senhaCriptografada.length() >= 60, "Hash BCrypt deve ter pelo menos 60 caracteres");
        }

        @Test
        @DisplayName("Deve gerar hashes diferentes para a mesma senha")
        void deveGerarHashesDiferentesParaMesmaSenha() {
            // Given
            String senhaPlana = "senhaigual";
            
            // When
            String hash1 = CriptografiaUtil.criptografarSenha(senhaPlana);
            String hash2 = CriptografiaUtil.criptografarSenha(senhaPlana);
            
            // Then
            assertNotEquals(hash1, hash2, "Hashes devem ser diferentes devido ao salt aleatório");
        }

        @Test
        @DisplayName("Deve lançar exceção para senha nula")
        void deveLancarExcecaoParaSenhaNula() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CriptografiaUtil.criptografarSenha(null),
                "Deve lançar IllegalArgumentException para senha nula"
            );
            
            assertEquals("A senha não pode ser nula ou vazia", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção para senha vazia")
        void deveLancarExcecaoParaSenhaVazia() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CriptografiaUtil.criptografarSenha(""),
                "Deve lançar IllegalArgumentException para senha vazia"
            );
            
            assertEquals("A senha não pode ser nula ou vazia", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção para senha apenas com espaços")
        void deveLancarExcecaoParaSenhaApenasEspacos() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CriptografiaUtil.criptografarSenha("   "),
                "Deve lançar IllegalArgumentException para senha apenas com espaços"
            );
            
            assertEquals("A senha não pode ser nula ou vazia", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes de verificação de senha")
    class VerificacaoSenhaTests {

        @Test
        @DisplayName("Deve verificar senha correta")
        void deveVerificarSenhaCorreta() {
            // Given
            String senhaPlana = "senhaCorreta123";
            String hashArmazenado = CriptografiaUtil.criptografarSenha(senhaPlana);
            
            // When
            boolean resultado = CriptografiaUtil.verificarSenha(senhaPlana, hashArmazenado);
            
            // Then
            assertTrue(resultado, "Deve retornar true para senha correta");
        }

        @Test
        @DisplayName("Deve rejeitar senha incorreta")
        void deveRejeitarSenhaIncorreta() {
            // Given
            String senhaCorreta = "senhaCorreta123";
            String senhaIncorreta = "senhaErrada456";
            String hashArmazenado = CriptografiaUtil.criptografarSenha(senhaCorreta);
            
            // When
            boolean resultado = CriptografiaUtil.verificarSenha(senhaIncorreta, hashArmazenado);
            
            // Then
            assertFalse(resultado, "Deve retornar false para senha incorreta");
        }

        @Test
        @DisplayName("Deve retornar false para senha nula")
        void deveRetornarFalseParaSenhaNula() {
            // Given
            String hashValido = CriptografiaUtil.criptografarSenha("qualquersenha");
            
            // When
            boolean resultado = CriptografiaUtil.verificarSenha(null, hashValido);
            
            // Then
            assertFalse(resultado, "Deve retornar false para senha nula");
        }

        @Test
        @DisplayName("Deve retornar false para hash nulo")
        void deveRetornarFalseParaHashNulo() {
            // When
            boolean resultado = CriptografiaUtil.verificarSenha("qualquersenha", null);
            
            // Then
            assertFalse(resultado, "Deve retornar false para hash nulo");
        }

        @Test
        @DisplayName("Deve retornar false para hash inválido")
        void deveRetornarFalseParaHashInvalido() {
            // Given
            String senhaPlana = "senhaqualquer";
            String hashInvalido = "hashinvalido123";
            
            // When
            boolean resultado = CriptografiaUtil.verificarSenha(senhaPlana, hashInvalido);
            
            // Then
            assertFalse(resultado, "Deve retornar false para hash inválido");
        }

        @Test
        @DisplayName("Deve ser case-sensitive")
        void deveSerCaseSensitive() {
            // Given
            String senhaOriginal = "SenhaComMaiusculas";
            String senhaMinuscula = "senhacommaiusculas";
            String hashArmazenado = CriptografiaUtil.criptografarSenha(senhaOriginal);
            
            // When
            boolean resultadoCorreto = CriptografiaUtil.verificarSenha(senhaOriginal, hashArmazenado);
            boolean resultadoIncorreto = CriptografiaUtil.verificarSenha(senhaMinuscula, hashArmazenado);
            
            // Then
            assertTrue(resultadoCorreto, "Deve aceitar senha com case correto");
            assertFalse(resultadoIncorreto, "Deve rejeitar senha com case diferente");
        }
    }

    @Nested
    @DisplayName("Testes de segurança")
    class SegurancaTests {

        @Test
        @DisplayName("Deve usar salt aleatório")
        void deveUsarSaltAleatorio() {
            // Given
            String senha = "mesmasenha";
            
            // When
            String hash1 = CriptografiaUtil.criptografarSenha(senha);
            String hash2 = CriptografiaUtil.criptografarSenha(senha);
            
            // Then
            assertNotEquals(hash1, hash2, "Deve usar salt aleatório gerando hashes diferentes");
            
            // Verificar se ambos os hashes são válidos para a mesma senha
            assertTrue(CriptografiaUtil.verificarSenha(senha, hash1), "Primeiro hash deve ser válido");
            assertTrue(CriptografiaUtil.verificarSenha(senha, hash2), "Segundo hash deve ser válido");
        }

        @Test
        @DisplayName("Deve usar custo adequado (12 rounds)")
        void deveUsarCustoAdequado() {
            // Given
            String senha = "senhaparateste";
            
            // When
            String hash = CriptografiaUtil.criptografarSenha(senha);
            
            // Then
            // BCrypt hash format: $2a$rounds$salt+hash
            String[] partes = hash.split("\\$");
            assertEquals("2a", partes[1], "Deve usar versão 2a do BCrypt");
            assertEquals("12", partes[2], "Deve usar 12 rounds para segurança adequada");
        }

        @Test
        @DisplayName("Deve ser resistente a timing attacks")
        void deveSerResistenteATimingAttacks() {
            // Given
            String senhaCorreta = "senhaCorreta123";
            String hashValido = CriptografiaUtil.criptografarSenha(senhaCorreta);
            String senhaIncorreta = "senhaIncorreta456";
            
            // When - Medir tempo de verificação
            long inicioCorreto = System.nanoTime();
            CriptografiaUtil.verificarSenha(senhaCorreta, hashValido);
            long tempoCorreto = System.nanoTime() - inicioCorreto;
            
            long inicioIncorreto = System.nanoTime();
            CriptografiaUtil.verificarSenha(senhaIncorreta, hashValido);
            long tempoIncorreto = System.nanoTime() - inicioIncorreto;
            
            // Then - Tempos devem ser similares (diferença menor que 50%)
            double diferenca = Math.abs(tempoCorreto - tempoIncorreto) / (double) Math.max(tempoCorreto, tempoIncorreto);
            assertTrue(diferenca < 0.5, "Diferença de tempo deve ser menor que 50% para resistir a timing attacks");
        }
    }

    @Nested
    @DisplayName("Testes de performance")
    class PerformanceTests {

        @Test
        @DisplayName("Deve criptografar senha em tempo razoável")
        void deveCriptografarSenhaEmTempoRazoavel() {
            // Given
            String senha = "senhaparatestevelocidade";
            
            // When
            long inicio = System.currentTimeMillis();
            CriptografiaUtil.criptografarSenha(senha);
            long tempo = System.currentTimeMillis() - inicio;
            
            // Then - Deve levar menos de 1 segundo (BCrypt com 12 rounds)
            assertTrue(tempo < 1000, "Criptografia deve levar menos de 1 segundo");
        }

        @Test
        @DisplayName("Deve verificar senha em tempo razoável")
        void deveVerificarSenhaEmTempoRazoavel() {
            // Given
            String senha = "senhaparatestevelocidade";
            String hash = CriptografiaUtil.criptografarSenha(senha);
            
            // When
            long inicio = System.currentTimeMillis();
            CriptografiaUtil.verificarSenha(senha, hash);
            long tempo = System.currentTimeMillis() - inicio;
            
            // Then - Deve levar menos de 1 segundo
            assertTrue(tempo < 1000, "Verificação deve levar menos de 1 segundo");
        }
    }
}

