package controllers;

import auth.AuthBean;
import dao.ProduitDAO;
import entities.Produit;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Contrôleur CDI pour la gestion intégrale des stocks de médicaments
 */
@Named("stockController")
@SessionScoped
public class StockController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProduitDAO produitDAO;

    @Inject
    private AuthBean authBean;

    private String searchFilter;
    private Produit nouveauProduit = new Produit();
    private Produit produitSelectionne;
    private int quantiteReapprovisionnement = 10;

    @PostConstruct
    public void init() {
        nouveauProduit = new Produit();
        nouveauProduit.setDateExp(new Date());
    }

    public List<Produit> getProduits() {
        if (searchFilter != null && !searchFilter.trim().isEmpty()) {
            return produitDAO.search(searchFilter);
        }
        return produitDAO.findAll();
    }

    public int getTotalMedicaments() {
        return produitDAO.count();
    }

    public int getStockFaible() {
        return produitDAO.countLowStock(5);
    }

    public int getRupture() {
        return produitDAO.countOutOfStock();
    }

    public String enregistrerProduit() {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            if (produitDAO.find(nouveauProduit.getNLot()) != null) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Erreur", "Un produit avec le numéro de lot " + nouveauProduit.getNLot() + " existe déjà."));
                return null;
            }

            nouveauProduit.setIdUtilisateurEnreg(authBean.getUtilisateurConnecte());
            produitDAO.create(nouveauProduit);

            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Succès", "Médicament " + nouveauProduit.getNomC() + " ajouté avec succès au stock."));

            nouveauProduit = new Produit();
            nouveauProduit.setDateExp(new Date());
        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erreur d'enregistrement", e.getMessage()));
        }
        return null;
    }

    public void reapprovisionner(Produit p) {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            produitDAO.adjustStock(p.getNLot(), quantiteReapprovisionnement);
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Stock mis à jour", "+" + quantiteReapprovisionnement + " unités ajoutées pour " + p.getNomC()));
        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage()));
        }
    }

    public void supprimer(Produit p) {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            produitDAO.remove(p);
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Produit supprimé", "Le produit " + p.getNomC() + " a été retiré."));
        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Suppression impossible", "Le produit ne peut pas être supprimé (peut-être lié à des ventes ou échanges)."));
        }
    }

    // Getters et Setters
    public String getSearchFilter() { return searchFilter; }
    public void setSearchFilter(String searchFilter) { this.searchFilter = searchFilter; }
    public Produit getNouveauProduit() { return nouveauProduit; }
    public void setNouveauProduit(Produit nouveauProduit) { this.nouveauProduit = nouveauProduit; }
    public Produit getProduitSelectionne() { return produitSelectionne; }
    public void setProduitSelectionne(Produit produitSelectionne) { this.produitSelectionne = produitSelectionne; }
    public int getQuantiteReapprovisionnement() { return quantiteReapprovisionnement; }
    public void setQuantiteReapprovisionnement(int quantiteReapprovisionnement) { this.quantiteReapprovisionnement = quantiteReapprovisionnement; }
}
