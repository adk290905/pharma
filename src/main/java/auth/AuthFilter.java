package auth;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtre de sécurité assurant la protection des pages et le contrôle d'accès par rôle
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/faces/*", "*.xhtml"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String path = req.getRequestURI();
        String contextPath = req.getContextPath();

        // Ressources publiques autorisées
        boolean isLogin = path.contains("login.xhtml") || path.endsWith("/login");
        boolean isResource = path.contains("/resources/") || path.contains("jakarta.faces.resource") || path.contains("/css/");

        if (isLogin || isResource) {
            chain.doFilter(request, response);
            return;
        }

        // Vérification de la session utilisateur
        AuthBean authBean = (session != null) ? (AuthBean) session.getAttribute("authBean") : null;
        boolean loggedIn = (authBean != null && authBean.isLoggedIn())
                || (session != null && session.getAttribute("user") != null);

        if (!loggedIn) {
            res.sendRedirect(contextPath + "/faces/login.xhtml");
            return;
        }

        // Contrôle d'accès spécifique au rôle GERANT
        boolean isUserAdminPage = path.contains("/vues/utilisateur/") || path.contains("admin.xhtml");
        if (isUserAdminPage) {
            boolean isGerant = (authBean != null && authBean.isGerant())
                    || (session != null && "GERANT".equalsIgnoreCase((String) session.getAttribute("role")));
            if (!isGerant) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé uniquement aux gérants de la pharmacie.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
