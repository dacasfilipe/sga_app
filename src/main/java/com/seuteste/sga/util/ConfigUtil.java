package com.seuteste.sga.util;

/**
 * Utilitário para leitura de configurações do ambiente
 * Facilita a configuração da aplicação em diferentes ambientes (desenvolvimento, produção)
 */
public class ConfigUtil {
    
    /**
     * Obtém uma variável de ambiente ou retorna um valor padrão
     * @param envVar Nome da variável de ambiente
     * @param defaultValue Valor padrão se a variável não existir
     * @return Valor da variável de ambiente ou valor padrão
     */
    public static String getEnvVar(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Obtém uma propriedade do sistema ou retorna um valor padrão
     * @param property Nome da propriedade do sistema
     * @param defaultValue Valor padrão se a propriedade não existir
     * @return Valor da propriedade do sistema ou valor padrão
     */
    public static String getSystemProperty(String property, String defaultValue) {
        return System.getProperty(property, defaultValue);
    }
    
    /**
     * Verifica se a aplicação está rodando em ambiente de produção
     * @return true se estiver em produção, false caso contrário
     */
    public static boolean isProduction() {
        String env = getEnvVar("ENVIRONMENT", "development");
        return "production".equalsIgnoreCase(env) || "prod".equalsIgnoreCase(env);
    }
    
    /**
     * Obtém a URL do banco de dados
     * @return URL do banco de dados
     */
    public static String getDatabaseUrl() {
        return getEnvVar("DB_URL", "jdbc:postgresql://localhost:5432/sga_db");
    }
    
    /**
     * Obtém o usuário do banco de dados
     * @return Usuário do banco de dados
     */
    public static String getDatabaseUsername() {
        return getEnvVar("DB_USERNAME", "postgres");
    }
    
    /**
     * Obtém a senha do banco de dados
     * @return Senha do banco de dados
     */
    public static String getDatabasePassword() {
        return getEnvVar("DB_PASSWORD", "postgres");
    }
    
    /**
     * Obtém a porta da aplicação
     * @return Porta da aplicação
     */
    public static int getServerPort() {
        String port = getEnvVar("SERVER_PORT", "8080");
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            return 8080;
        }
    }
}

