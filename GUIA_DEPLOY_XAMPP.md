# 🚀 GUIA DE DEPLOY NO XAMPP

## 📋 PASSOS PARA EXECUTAR O SISTEMA

### 1️⃣ **Após Instalar o XAMPP:**

1. **Inicie o XAMPP Control Panel**
2. **Start Apache** (para servir arquivos web)
3. **Start MySQL** (se quiser usar banco local, ou mantenha PostgreSQL AWS)

### 2️⃣ **Deploy do WAR:**

#### **Opção A - Tomcat no XAMPP (Recomendado):**
```bash
# Copiar WAR para pasta do Tomcat
copy "C:\Users\filip\OneDrive\Documentos\sga-app\sga_app\target\sga.war" "C:\xampp\tomcat\webapps\sga.war"

# Iniciar Tomcat
cd C:\xampp\tomcat\bin
startup.bat
```

#### **Opção B - Apache + mod_jk:**
```bash
# Extrair WAR manualmente
cd "C:\Users\filip\OneDrive\Documentos\sga-app\sga_app\target"
jar -xf sga.war -C C:\xampp\htdocs\sga\
```

### 3️⃣ **Acessar o Sistema:**

🌐 **URLs de Acesso:**
- **Tomcat**: `http://localhost:8080/sga/`
- **Apache**: `http://localhost/sga/`

### 4️⃣ **Testar Login:**

#### 🧪 **Contas de Teste (Use os botões na tela de login):**

**👨‍💼 ADMINISTRADOR:**
- Email: `admin@sga.com`
- Senha: `123456`
- Acesso: Todas as áreas

**👨‍🔧 OPERADOR:**
- Email: `operador@sga.com`
- Senha: `operador123`  
- Acesso: Produtos e Clientes apenas

### 5️⃣ **Se Precisar Criar Usuários no Banco:**

#### **SQL para PostgreSQL:**
```sql
INSERT INTO Usuario (id, nome, email, senha, perfil, data_criacao) VALUES 
(1, 'Administrador do Sistema', 'admin@sga.com', '$2a$10$EFz6Sy2JOFq/75jmJb4XD..l7gYAgVyDCSVv/I3ONzzYMGSPY8wBS', 'ADMIN', NOW()),
(2, 'Operador do Sistema', 'operador@sga.com', '$2a$10$7eySVM12URobw2hyRFsnEeXx9j6vKLqQbB7nC5yWTMNXaSBUJOga.', 'OPERADOR', NOW());
```

#### **Para MySQL Local (se quiser usar):**
```sql
CREATE DATABASE sga;
USE sga;

-- Criar enum para perfil
CREATE TABLE Usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    perfil ENUM('ADMIN', 'OPERADOR') NOT NULL,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Inserir usuários de teste
INSERT INTO Usuario (nome, email, senha, perfil) VALUES 
('Administrador do Sistema', 'admin@sga.com', '$2a$10$EFz6Sy2JOFq/75jmJb4XD..l7gYAgVyDCSVv/I3ONzzYMGSPY8wBS', 'ADMIN'),
('Operador do Sistema', 'operador@sga.com', '$2a$10$7eySVM12URobw2hyRFsnEeXx9j6vKLqQbB7nC5yWTMNXaSBUJOga.', 'OPERADOR');
```

---

## 🔧 TROUBLESHOOTING

### ❗ **Se der erro de porta:**
- Tomcat padrão: porta 8080
- Apache padrão: porta 80
- Verificar se não há conflitos

### ❗ **Se der erro de banco:**
- Verificar se PostgreSQL AWS está acessível
- Ou configurar MySQL local
- Verificar configurações em `persistence.xml`

### ❗ **Se JSF não carregar:**
- Verificar se todas as libs estão no WAR
- Confirmar que JSF factories estão configuradas
- Verificar logs do Tomcat

---

## 🎯 **RESULTADO ESPERADO:**

1. **Login Screen** com botões de teste
2. **Dashboard** personalizado por perfil
3. **Menu** dinâmico baseado em permissões
4. **Acesso controlado** por área
5. **Interface responsiva** e profissional

**🚀 Pronto para testar após instalar o XAMPP!**
