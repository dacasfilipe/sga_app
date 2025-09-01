package com.seuteste.sga.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Configuração JSF para Spring Boot
 */
public class JsfConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        
        // Configurações JSF essenciais
        servletContext.setInitParameter("javax.faces.PROJECT_STAGE", "Production");
        servletContext.setInitParameter("javax.faces.STATE_SAVING_METHOD", "server");
        servletContext.setInitParameter("javax.faces.FACELETS_REFRESH_PERIOD", "-1");
        servletContext.setInitParameter("javax.faces.FACELETS_LIBRARIES", "/WEB-INF/faces-config.xml");
        
        // PrimeFaces
        servletContext.setInitParameter("primefaces.THEME", "nova-light");
        servletContext.setInitParameter("primefaces.FONT_AWESOME", "true");
        
        // Weld CDI - essencial para JSF
        servletContext.setInitParameter("org.jboss.weld.environment.servlet.EnhancedListener", "true");
        servletContext.setInitParameter("org.jboss.weld.environment.container.class", 
            "org.jboss.weld.environment.se.WeldSEContainer");
            
        System.out.println("JSF Context parameters configured");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup
    }
}
