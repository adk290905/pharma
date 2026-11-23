package controllers;

import auth.AuthBean;
import dao.EchangeDAO;
import dao.ProduitDAO;
import entities.Echange;
import entities.LigneEchange;
import entities.Produit;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contrôleur CDI pour la gestion des échanges inter-pharmacies
 */
@Named("echangeBean")
@SessionScoped
public class EchangeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private EchangeDAO echangeDAO;

    @EJB
    private ProduitDAO produitDAO;

    @Inject
    private AuthBean authBean;

    private Echange nouvelEchange = new Echange();
    private String selectedLot;
    private int quantiteEchange = 1;

    @PostConstruct
    public void init() {
        resetForm();
    }

    public void resetForm() {
        nouvelEchange = new Echange();
        nouvelEchange.setTypeEchange("ENTRANT");
        nouvelEchange.setDateEchange(new Date());
        selectedLot = null;
        quantiteEchange = 1;
    }

    public List<Echange> getEchanges() {
        return echangeDAO.findAllOrderByDateDesc();
    }

    public List<LigneEchange> getLignesEchange(String idEchange) {
        return echangeDAO.findLignesByEchange(idEchange);
    }

    public String enregistrerEchange() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (selectedLot == null || selectedLot.trim().isEmpty()) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Veuillez sélectionner un médicament."));
                return null;
            }

            Produit produit = produitDAO.find(selectedLot);
            if (produit == null) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Médicament introuvable."));
                return null;
            }

            String idEch = "ECH-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            nouvelEchange.setIdEchange(idEch);
            nouvelEchange.setDateEchange(new Date());
            nouvelEchange.setIdGerant(authBean.getUtilisateurConnecte());

            LigneEchange ligne = new LigneEchange();
            ligne.setProduit(produit);
            ligne.setQuantite(quantiteEchange);

            List<LigneEchange> lignes = new ArrayList<>();
            lignes.add(ligne);

            echangeDAO.finaliserEchange(nouvelEchange, lignes);

            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Succès", "Échange " + idEch + " (" + nouvelEchange.getTypeEchange() + ") enregistré avec mise à jour du stock."));

            resetForm();
        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erreur", e.getMessage()));
        }
        return null;
    }

    // Getters et Setters
    public Echange getNouvelEchange() { return nouvelEchange; }
    public void setNouvelEchange(Echange nouvelEchange) { this.nouvelEchange = nouvelEchange; }
    public String getSelectedLot() { return selectedLot; }
    public void setSelectedLot(String selectedLot) { this.selectedLot = selectedLot; }
    public int getQuantiteEchange() { return quantiteEchange; }
    public void setQuantiteEchange(int quantiteEchange) { this.quantiteEchange = quantiteEchange; }
}
