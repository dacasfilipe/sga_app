# 🚀 SOLUÇÃO RÁPIDA - Deploy apenas com sga.war

## ❌ PROBLEMA
Seu log mostra: `nenhum atributo manifesto principal, em application.jar`
- EB está tentando executar WAR como JAR
- Precisa usar plataforma Tomcat, não Java SE

## ✅ SOLUÇÃO EM 3 PASSOS

### 1. No Console AWS Elastic Beanstalk

**CRIAR NOVO AMBIENTE** (recomendado):
- Platform: **"64bit Amazon Linux 2 running Tomcat 8.5 Corretto 11"**
- Application code: Upload `sga.war`

**OU modificar atual**:
- Configuration → Platform → Edit
- Trocar para: **"Tomcat 8.5 Corretto 11"**

### 2. Configurar Variáveis de Ambiente

Em Configuration → Software → Environment properties:
```
ENVIRONMENT = production
DB_HOST = seu-rds-endpoint.amazonaws.com
DB_PORT = 5432
DB_NAME = sga
DB_USER = postgres
DB_PASSWORD = sua-senha-rds
```

### 3. Upload apenas o WAR

- Upload: `target/sga.war` (apenas este arquivo)
- **NÃO incluir**: Procfile, .ebextensions, etc.

## 🎯 RESULTADO ESPERADO

- Tomcat vai descompactar o WAR automaticamente
- Aplicação vai rodar na porta 8080
- Nginx vai fazer proxy para Tomcat
- URL da aplicação vai carregar a página de login

## ⚠️ PONTOS CRÍTICOS

1. **Plataforma DEVE ser Tomcat** (não Java SE)
2. **Apenas arquivo WAR** (sem outras configurações)
3. **Corretto 11** (versão disponível na AWS)

Se der certo, você vai ver a página de login JSF da sua aplicação!
