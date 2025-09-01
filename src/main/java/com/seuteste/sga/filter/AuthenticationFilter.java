package com.seuteste.sga.filter;

import com.seuteste.sga.model.Usuario;
import com.seuteste.sga.util.SessaoUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtro para controle de autenticação e autorização de usuários.
 * Verifica se o usuário está logado e tem permissão para acessar a página.
 */
@WebFilter(urlPatterns = {"*.xhtml"})
public class AuthenticationFilter implements Filter {

    // Páginas que não precisam de autenticação
    private static final List<String> PUBLIC_PAGES = Arrays.asList(
        "/login.xhtml"
    );

    // Páginas restritas apenas para ADMIN
    private static final List<String> ADMIN_ONLY_PAGES = Arrays.asList(
        "/categorias.xhtml",
        "/pedidos.xhtml"
    );

    // Páginas acessíveis para OPERADOR e ADMIN
    private static final List<String> OPERADOR_PAGES = Arrays.asList(
        "/produtos.xhtml",
        "/clientes.xhtml"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização do filtro
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String page = requestURI.substring(contextPath.length());

        // Permitir acesso a recursos estáticos
        if (page.contains("/resources/") || page.contains("/javax.faces.resource/")) {
            chain.doFilter(request, response);
            return;
        }

        // Permitir acesso a páginas públicas
        if (PUBLIC_PAGES.contains(page)) {
            chain.doFilter(request, response);
            return;
        }

        // Verificar se o usuário está logado
        HttpSession session = httpRequest.getSession(false);
        Usuario usuarioLogado = null;
        
        if (session != null) {
            usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        }

        // Se não estiver logado, redirecionar para login
        if (usuarioLogado == null) {
            httpResponse.sendRedirect(contextPath + "/login.xhtml?redirect=" + page);
            return;
        }

        // Verificar permissões baseadas no perfil
        if (ADMIN_ONLY_PAGES.contains(page)) {
            if (!usuarioLogado.isAdmin()) {
                // Usuário não é admin, redirecionar para página de acesso negado
                httpResponse.sendRedirect(contextPath + "/index.xhtml?error=access_denied");
                return;
            }
        } else if (OPERADOR_PAGES.contains(page)) {
            // Páginas acessíveis para operador e admin - permitir acesso
            if (!usuarioLogado.isOperador() && !usuarioLogado.isAdmin()) {
                httpResponse.sendRedirect(contextPath + "/index.xhtml?error=access_denied");
                return;
            }
        }

        // Usuário autenticado e autorizado, continuar com a requisição
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Limpeza do filtro
    }
}
