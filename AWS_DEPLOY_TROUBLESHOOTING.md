# Deploy na AWS Elastic Beanstalk - Solução DEFINITIVA

## Problema Identificado

❌ **Erro**: `nenhum atributo manifesto principal, em application.jar`  
❌ **Causa**: EB está tentando executar WAR como JAR executável  
❌ **Resultado**: Nginx retorna 502 (conexão recusada na porta 5000)

## ✅ SOLUÇÃO SIMPLIFICADA (Apenas com sga.war)

### Passo 1: No Console AWS Elastic Beanstalk

1. **Criar NOVO ambiente** (recomendado):
   - Platform: **"Tomcat 8.5 with Corretto 11"** (NÃO Java SE)
   - Upload: Apenas o arquivo `target/sga.war`

2. **OU modificar ambiente atual**:
   - Configuration > Software
   - Platform: Trocar para **"Tomcat 8.5 with Corretto 11"**

### Passo 2: Configurações de Ambiente

No console do EB, adicionar estas variáveis:
```
ENVIRONMENT=production
DB_HOST=seu-rds-endpoint
DB_PORT=5432
DB_NAME=sga
DB_USER=seu-usuario
DB_PASSWORD=sua-senha
```

### Passo 3: Deploy

1. **Buildar aplicação**:
   ```bash
   # Usar Maven se disponível
   mvn clean package -f pom-aws.xml -Paws
   ```

2. **Upload apenas**: `target/sga.war`

## ⚠️ IMPORTANTE

- **NÃO usar** Java SE platform
- **NÃO incluir** Procfile (causa o erro)
- **Usar apenas** Tomcat platform
- **Upload apenas** o arquivo WAR

## Como testar se funcionou

Após deploy, verificar:
- Status do ambiente: "Ok" (verde)
- Health: "Ok" 
- URL da aplicação: deve carregar a página de login

## Se ainda der erro

**Opção 1 - Recriar ambiente**:
```bash
eb terminate
eb create --platform "64bit Amazon Linux 2 v4.x.x running Tomcat 8.5 Corretto 11"
```

**Opção 2 - Verificar logs**:
```bash
eb logs --all
```

## Configuração atual do projeto

✅ POM configurado para Java 11  
✅ Configuração EB simplificada  
✅ Sem Procfile (removido)  
✅ Sem configurações conflitantes
