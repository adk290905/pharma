package controllers;

import auth.AuthBean;
import dao.BonDeCommandeDAO;
import entities.BonDeCommande;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Contrôleur CDI pour la gestion des bons de commande et traçabilité des consultations
 */
@Named("bonDeCommandeBean")
@SessionScoped
public class BonDeCommandeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private BonDeCommandeDAO bonDeCommandeDAO;

    @Inject
    private AuthBean authBean;

    private BonDeCommande nouveauBon = new BonDeCommande();

    @PostConstruct
    public void init() {
        nouveauBon = new BonDeCommande();
        nouveauBon.setDateBon(new Date());
    }

    public List<BonDeCommande> getBons() {
        return bonDeCommandeDAO.findAllOrderByDateDesc();
    }

    public String creerBon() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            String idBon = "BC-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            nouveauBon.setIdBon(idBon);
            if (nouveauBon.getDateBon() == null) {
                nouveauBon.setDateBon(new Date());
            }
            nouveauBon.setIdGerantCreateur(authBean.getUtilisateurConnecte());

            bonDeCommandeDAO.create(nouveauBon);

            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Succès", "Bon de commande n° " + idBon + " créé avec succès."));

            nouveauBon = new BonDeCommande();
            nouveauBon.setDateBon(new Date());
        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage()));
        }
        return null;
    }

    public void consulterBon(BonDeCommande bon) {
        if (bon != null && authBean.getUtilisateurConnecte() != null) {
            bonDeCommandeDAO.enregistrerConsultation(
                    authBean.getUtilisateurConnecte().getIdUtilisateur(),
                    bon.getIdBon());
        }
    }

    public BonDeCommande getNouveauBon() { return nouveauBon; }
    public void setNouveauBon(BonDeCommande nouveauBon) { this.nouveauBon = nouveauBon; }
}
