#!/bin/bash

# Hook de pós-deploy para configurar aplicação WAR no Tomcat
echo "Configurando aplicação WAR no Tomcat..."

# Parar serviços incorretos
systemctl stop web || true

# Garantir que o Tomcat8 está rodando
systemctl start tomcat8 || true
systemctl enable tomcat8 || true

# Verificar se o WAR foi copiado corretamente
if [ -f /var/app/current/sga.war ]; then
    echo "Copiando WAR para Tomcat..."
    cp /var/app/current/sga.war /var/lib/tomcat8/webapps/ROOT.war
    chown tomcat:tomcat /var/lib/tomcat8/webapps/ROOT.war
fi

# Reiniciar Tomcat
systemctl restart tomcat8

echo "Configuração WAR concluída."
