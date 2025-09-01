package com.seuteste.sga.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Configuração das JSF Factories para resolver o erro FacesContextFactory
 */
@WebListener
public class JsfFactoryConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("JsfFactoryConfig: Inicializando JSF Factories...");
        
        // Configurar todas as JSF Factories via system properties
        System.setProperty("javax.faces.FACES_CONTEXT_FACTORY", 
            "com.sun.faces.context.FacesContextFactoryImpl");
        System.setProperty("javax.faces.APPLICATION_FACTORY", 
            "com.sun.faces.application.ApplicationFactoryImpl");
        System.setProperty("javax.faces.LIFECYCLE_FACTORY", 
            "com.sun.faces.lifecycle.LifecycleFactoryImpl");
        System.setProperty("javax.faces.RENDER_KIT_FACTORY", 
            "com.sun.faces.renderkit.RenderKitFactoryImpl");
        System.setProperty("javax.faces.EXCEPTION_HANDLER_FACTORY", 
            "com.sun.faces.context.ExceptionHandlerFactoryImpl");
        System.setProperty("javax.faces.PARTIAL_VIEW_CONTEXT_FACTORY", 
            "com.sun.faces.context.PartialViewContextFactoryImpl");
        System.setProperty("javax.faces.VIEW_DECLARATION_LANGUAGE_FACTORY", 
            "com.sun.faces.application.view.ViewDeclarationLanguageFactoryImpl");
        System.setProperty("javax.faces.EXTERNAL_CONTEXT_FACTORY", 
            "com.sun.faces.context.ExternalContextFactoryImpl");
            
        // Configurações adicionais JSF
        System.setProperty("javax.faces.PROJECT_STAGE", "Production");
        System.setProperty("javax.faces.STATE_SAVING_METHOD", "server");
        System.setProperty("javax.faces.FACELETS_REFRESH_PERIOD", "-1");
        
        System.out.println("JsfFactoryConfig: JSF Factories initialized successfully");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("JsfFactoryConfig: Context destroyed");
    }
}
