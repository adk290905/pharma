package auth;

import dao.UtilisateurDAO;
import entities.Utilisateur;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;

/**
 * Bean de session CDI gérant l'authentification JAAS et l'état de session utilisateur
 */
@Named("authBean")
@SessionScoped
public class AuthBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UtilisateurDAO utilisateurDAO;

    private String username;
    private String password;
    private Utilisateur utilisateurConnecte;

    public String login() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        HttpServletRequest req = (HttpServletRequest) ec.getRequest();

        // 1. Authentification au niveau de la base de données
        Utilisateur user = utilisateurDAO.authentifier(username, password);

        if (user == null) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Échec de connexion", "Identifiant ou mot de passe incorrect."));
            return null;
        }

        // 2. Intégration JAAS conteneur (si configuré dans WildFly/Payara)
        try {
            if (req.getUserPrincipal() == null) {
                req.login(username, password);
            }
        } catch (Exception e) {
            // Tolérance si le realm JAAS serveur n'est pas encore provisionné en environnement local
            System.out.println("Info: Authentification conteneur JAAS non bloquante : " + e.getMessage());
        }

        this.utilisateurConnecte = user;
        ec.getSessionMap().put("user", user);
        ec.getSessionMap().put("role", user.getTypeUtilisateur());
        ec.getSessionMap().put("nomComplet", user.getPrenom() + " " + user.getNom());

        return "/index.xhtml?faces-redirect=true";
    }

    public String logout() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        HttpServletRequest req = (HttpServletRequest) ec.getRequest();

        try {
            req.logout();
        } catch (Exception ignored) {
        }

        ec.invalidateSession();
        this.utilisateurConnecte = null;
        this.username = null;
        this.password = null;

        return "/login.xhtml?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return utilisateurConnecte != null;
    }

    public boolean isGerant() {
        return utilisateurConnecte != null && "GERANT".equalsIgnoreCase(utilisateurConnecte.getTypeUtilisateur());
    }

    public boolean isEmploye() {
        return utilisateurConnecte != null && "EMPLOYE".equalsIgnoreCase(utilisateurConnecte.getTypeUtilisateur());
    }

    public String getNomComplet() {
        if (utilisateurConnecte != null) {
            return utilisateurConnecte.getPrenom() + " " + utilisateurConnecte.getNom();
        }
        return "Invité";
    }

    public String getRole() {
        if (utilisateurConnecte != null) {
            return utilisateurConnecte.getTypeUtilisateur();
        }
        return "";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public void setUtilisateurConnecte(Utilisateur utilisateurConnecte) {
        this.utilisateurConnecte = utilisateurConnecte;
    }
}
