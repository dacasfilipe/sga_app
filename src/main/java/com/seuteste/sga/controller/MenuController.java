package com.seuteste.sga.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

@Named
@RequestScoped
public class MenuController {

    @Inject
    private SessaoController sessaoController;
    
    private MenuModel menuModel;

    @PostConstruct
    public void init() {
        menuModel = new DefaultMenuModel();
        
        if (sessaoController.isLogado()) {
            // Menu Início - sempre visível para usuários logados
            DefaultMenuItem inicio = DefaultMenuItem.builder()
                .value("🏠 Início")
                .outcome("/index.xhtml")
                .build();
            menuModel.getElements().add(inicio);
            
            // Menu Produtos - sempre visível (operador e admin)
            if (sessaoController.temAcessoProdutos()) {
                DefaultMenuItem produtos = DefaultMenuItem.builder()
                    .value("📦 Produtos")
                    .outcome("/produtos.xhtml")
                    .build();
                menuModel.getElements().add(produtos);
            }
            
            // Menu Clientes - sempre visível (operador e admin)
            if (sessaoController.temAcessoClientes()) {
                DefaultMenuItem clientes = DefaultMenuItem.builder()
                    .value("👥 Clientes")
                    .outcome("/clientes.xhtml")
                    .build();
                menuModel.getElements().add(clientes);
            }
            
            // Menu Categorias - apenas admin
            if (sessaoController.temAcessoCategorias()) {
                DefaultMenuItem categorias = DefaultMenuItem.builder()
                    .value("📂 Categorias")
                    .outcome("/categorias.xhtml")
                    .build();
                menuModel.getElements().add(categorias);
            }
            
            // Menu Pedidos - apenas admin
            if (sessaoController.temAcessoPedidos()) {
                DefaultMenuItem pedidos = DefaultMenuItem.builder()
                    .value("📋 Pedidos")
                    .outcome("/pedidos.xhtml")
                    .build();
                menuModel.getElements().add(pedidos);
            }
        }
    }

    public MenuModel getMenuModel() {
        return menuModel;
    }
}
