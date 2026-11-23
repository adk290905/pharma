package controllers;

import auth.AuthBean;
import dao.UtilisateurDAO;
import entities.Utilisateur;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Contrôleur CDI d'administration des utilisateurs (réservé au rôle GERANT)
 */
@Named("adminBean")
@ViewScoped
public class AdminBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UtilisateurDAO utilisateurDAO;

    @Inject
    private AuthBean authBean;

    private Utilisateur nouvelUtilisateur = new Utilisateur();

    @PostConstruct
    public void init() {
        nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setTypeUtilisateur("EMPLOYE");
    }

    public List<Utilisateur> getUtilisateurs() {
        return utilisateurDAO.findAll();
    }

    public String enregistrerUtilisateur() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (utilisateurDAO.find(nouvelUtilisateur.getIdUtilisateur()) != null) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Erreur", "L'identifiant " + nouvelUtilisateur.getIdUtilisateur() + " est déjà utilisé."));
                return null;
            }

            utilisateurDAO.create(nouvelUtilisateur);

            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Succès", "Utilisateur " + nouvelUtilisateur.getPrenom() + " " + nouvelUtilisateur.getNom() + " créé."));

            nouvelUtilisateur = new Utilisateur();
            nouvelUtilisateur.setTypeUtilisateur("EMPLOYE");
        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage()));
        }
        return null;
    }

    public void supprimer(Utilisateur u) {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (authBean.getUtilisateurConnecte() != null &&
                    authBean.getUtilisateurConnecte().getIdUtilisateur().equals(u.getIdUtilisateur())) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Action refusée", "Vous ne pouvez pas supprimer votre propre compte connecté."));
                return;
            }
            utilisateurDAO.remove(u);
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Utilisateur supprimé", "Le compte " + u.getIdUtilisateur() + " a été supprimé."));
        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Suppression impossible", "Cet utilisateur a des opérations associées (ventes/échanges/bons)."));
        }
    }

    public Utilisateur getNouvelUtilisateur() { return nouvelUtilisateur; }
    public void setNouvelUtilisateur(Utilisateur nouvelUtilisateur) { this.nouvelUtilisateur = nouvelUtilisateur; }
}
