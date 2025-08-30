package com.seuteste.sga.filter;

import com.seuteste.sga.util.SessaoUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtro de autenticação para proteger páginas que requerem login.
 * 
 * @author SGA Team
 * @version 1.0
 */
@WebFilter(filterName = "AutenticacaoFilter", urlPatterns = {"*.xhtml"})
public class AutenticacaoFilter implements Filter {

    /**
     * Lista de páginas que não requerem autenticação.
     */
    private static final List<String> PAGINAS_PUBLICAS = Arrays.asList(
        "/login.xhtml",
        "/javax.faces.resource/"
    );

    /**
     * Lista de recursos que não requerem autenticação.
     */
    private static final List<String> RECURSOS_PUBLICOS = Arrays.asList(
        "/resources/",
        "/css/",
        "/js/",
        "/images/",
        "/api/swagger",
        "/api/docs"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização do filtro (se necessário)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Verificar se é uma página ou recurso público
        if (isPaginaPublica(path) || isRecursoPublico(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Verificar se o usuário está logado
        if (!SessaoUtil.isUsuarioLogado()) {
            // Usuário não está logado, redirecionar para login
            String loginURL = contextPath + "/login.xhtml";
            
            // Se for uma requisição AJAX, retornar status 401
            if (isRequisicaoAjax(httpRequest)) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Sessão expirada. Faça login novamente.");
                return;
            }
            
            // Redirecionar para página de login
            httpResponse.sendRedirect(loginURL);
            return;
        }

        // Usuário está logado, continuar com a requisição
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Limpeza do filtro (se necessário)
    }

    /**
     * Verifica se a página é pública (não requer autenticação).
     * 
     * @param path Caminho da página
     * @return true se for pública, false caso contrário
     */
    private boolean isPaginaPublica(String path) {
        return PAGINAS_PUBLICAS.stream().anyMatch(path::contains);
    }

    /**
     * Verifica se o recurso é público (não requer autenticação).
     * 
     * @param path Caminho do recurso
     * @return true se for público, false caso contrário
     */
    private boolean isRecursoPublico(String path) {
        return RECURSOS_PUBLICOS.stream().anyMatch(path::startsWith);
    }

    /**
     * Verifica se a requisição é AJAX.
     * 
     * @param request Requisição HTTP
     * @return true se for AJAX, false caso contrário
     */
    private boolean isRequisicaoAjax(HttpServletRequest request) {
        String facesRequest = request.getHeader("Faces-Request");
        String xmlHttpRequest = request.getHeader("X-Requested-With");
        
        return "partial/ajax".equals(facesRequest) || "XMLHttpRequest".equals(xmlHttpRequest);
    }
}

