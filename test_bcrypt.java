import at.favre.lib.crypto.bcrypt.BCrypt;

public class test_bcrypt {
    public static void main(String[] args) {
        String senha = "123456";
        String hash = BCrypt.withDefaults().hashToString(12, senha.toCharArray());
        System.out.println("Senha: " + senha);
        System.out.println("Hash BCrypt: " + hash);
        
        // Teste de verificação
        BCrypt.Result result = BCrypt.verifyer().verify(senha.toCharArray(), hash);
        System.out.println("Verificação: " + result.verified);
    }
}
