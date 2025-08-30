package com.seuteste.sga.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Classe utilitária para criptografia de senhas usando BCrypt.
 * 
 * @author SGA Team
 * @version 1.0
 */
public class CriptografiaUtil {

    /**
     * Número de rounds para o algoritmo BCrypt.
     * Valor padrão: 12 (oferece boa segurança e performance).
     */
    private static final int ROUNDS = 12;

    /**
     * Construtor privado para evitar instanciação da classe utilitária.
     */
    private CriptografiaUtil() {
        throw new UnsupportedOperationException("Esta é uma classe utilitária e não deve ser instanciada");
    }

    /**
     * Criptografa uma senha usando o algoritmo BCrypt.
     * 
     * @param senhaPlana A senha em texto plano a ser criptografada
     * @return A senha criptografada (hash)
     * @throws IllegalArgumentException se a senha for nula ou vazia
     */
    public static String criptografarSenha(String senhaPlana) {
        if (senhaPlana == null || senhaPlana.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser nula ou vazia");
        }
        
        return BCrypt.withDefaults().hashToString(ROUNDS, senhaPlana.toCharArray());
    }

    /**
     * Verifica se uma senha em texto plano corresponde ao hash armazenado.
     * 
     * @param senhaPlana A senha em texto plano a ser verificada
     * @param hashArmazenado O hash da senha armazenado no banco de dados
     * @return true se a senha corresponder ao hash, false caso contrário
     * @throws IllegalArgumentException se algum dos parâmetros for nulo ou vazio
     */
    public static boolean verificarSenha(String senhaPlana, String hashArmazenado) {
        if (senhaPlana == null || senhaPlana.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser nula ou vazia");
        }
        
        if (hashArmazenado == null || hashArmazenado.trim().isEmpty()) {
            throw new IllegalArgumentException("O hash armazenado não pode ser nulo ou vazio");
        }
        
        try {
            BCrypt.Result result = BCrypt.verifyer().verify(senhaPlana.toCharArray(), hashArmazenado);
            return result.verified;
        } catch (Exception e) {
            // Log do erro (em produção, usar um logger apropriado)
            System.err.println("Erro ao verificar senha: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se um hash é válido (formato BCrypt).
     * 
     * @param hash O hash a ser verificado
     * @return true se o hash for válido, false caso contrário
     */
    public static boolean isHashValido(String hash) {
        if (hash == null || hash.trim().isEmpty()) {
            return false;
        }
        
        // Hash BCrypt deve começar com $2a$, $2b$, $2x$ ou $2y$ e ter pelo menos 60 caracteres
        return hash.matches("^\\$2[abxy]\\$\\d{2}\\$.{53}$");
    }

    /**
     * Gera um salt aleatório para uso com BCrypt.
     * 
     * @return Um salt aleatório
     */
    public static String gerarSalt() {
        return BCrypt.withDefaults().hashToString(ROUNDS, "".toCharArray()).substring(0, 29);
    }

    /**
     * Obtém o número de rounds usado em um hash BCrypt.
     * 
     * @param hash O hash BCrypt
     * @return O número de rounds, ou -1 se o hash for inválido
     */
    public static int obterRounds(String hash) {
        if (!isHashValido(hash)) {
            return -1;
        }
        
        try {
            // Extrai os rounds do hash (posições 4-5)
            String roundsStr = hash.substring(4, 6);
            return Integer.parseInt(roundsStr);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Verifica se uma senha atende aos critérios mínimos de segurança.
     * 
     * @param senha A senha a ser verificada
     * @return true se a senha for segura, false caso contrário
     */
    public static boolean isSenhaSegura(String senha) {
        if (senha == null || senha.length() < 8) {
            return false;
        }
        
        boolean temMinuscula = senha.matches(".*[a-z].*");
        boolean temMaiuscula = senha.matches(".*[A-Z].*");
        boolean temNumero = senha.matches(".*\\d.*");
        boolean temEspecial = senha.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        
        return temMinuscula && temMaiuscula && temNumero && temEspecial;
    }
}

