package controllers;

import auth.AuthBean;
import dao.ProduitDAO;
import dao.VenteDAO;
import entities.LigneVente;
import entities.Produit;
import entities.Vente;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import services.FacturePdfService;

/**
 * Contrôleur CDI pour le comptoir de vente POS (panier, validation transactionnelle, décrémentation stock, facture PDF)
 */
@Named("ventePOSController")
@SessionScoped
public class VentePOSController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private VenteDAO venteDAO;

    @EJB
    private ProduitDAO produitDAO;

    @Inject
    private AuthBean authBean;

    @Inject
    private FacturePdfService facturePdfService;

    private String searchKeyword;
    private List<Produit> catalogueResultats = new ArrayList<>();

    // Panier en cours
    private List<LignePanier> panier = new ArrayList<>();
    private int quantiteSaisie = 1;

    // Dernière vente finalisée pour impression PDF
    private Vente derniereVente = null;
    private List<LigneVente> dernieresLignes = new ArrayList<>();

    /**
     * Classe interne représentant une ligne d'article dans le panier
     */
    public static class LignePanier implements Serializable {
        private Produit produit;
        private int quantite;
        private BigDecimal prixUnitaire;

        public LignePanier(Produit produit, int quantite) {
            this.produit = produit;
            this.quantite = quantite;
            this.prixUnitaire = produit.getPpa() != null ? produit.getPpa() : BigDecimal.ZERO;
        }

        public BigDecimal getSousTotal() {
            return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
        }

        public Produit getProduit() { return produit; }
        public void setProduit(Produit produit) { this.produit = produit; }
        public int getQuantite() { return quantite; }
        public void setQuantite(int quantite) { this.quantite = quantite; }
        public BigDecimal getPrixUnitaire() { return prixUnitaire; }
        public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    }

    public void initCatalogue() {
        if (catalogueResultats.isEmpty()) {
            catalogueResultats = produitDAO.findAll();
        }
    }

    public void rechercherProduits() {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            catalogueResultats = produitDAO.findAll();
        } else {
            catalogueResultats = produitDAO.search(searchKeyword);
        }
    }

    public void ajouterAuPanier(Produit p) {
        if (p == null) return;
        if (p.getQte() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Stock épuisé", "Le médicament " + p.getNomC() + " est en rupture de stock."));
            return;
        }

        // Vérifier si déjà dans le panier
        for (LignePanier item : panier) {
            if (item.getProduit().getNLot().equals(p.getNLot())) {
                if (item.getQuantite() + 1 > p.getQte()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Stock insuffisant", "Stock maximal atteint pour " + p.getNomC()));
                    return;
                }
                item.setQuantite(item.getQuantite() + 1);
                return;
            }
        }

        panier.add(new LignePanier(p, 1));
    }

    public void retirerDuPanier(int index) {
        if (index >= 0 && index < panier.size()) {
            panier.remove(index);
        }
    }

    public void viderPanier() {
        panier.clear();
    }

    public BigDecimal getTotalPanier() {
        BigDecimal total = BigDecimal.ZERO;
        for (LignePanier item : panier) {
            total = total.add(item.getSousTotal());
        }
        return total;
    }

    public int getNombreArticles() {
        int nb = 0;
        for (LignePanier item : panier) {
            nb += item.getQuantite();
        }
        return nb;
    }

    public String validerVente() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (panier.isEmpty()) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Panier vide", "Veuillez ajouter des médicaments au panier."));
            return null;
        }

        try {
            // Création de l'entité Vente
            Vente vente = new Vente();
            String idV = "VNT-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            vente.setIdV(idV);
            vente.setDateVente(new Date());
            vente.setIdUtilisateur(authBean.getUtilisateurConnecte());

            // Création des LigneVente
            List<LigneVente> lignes = new ArrayList<>();
            for (LignePanier item : panier) {
                LigneVente lv = new LigneVente();
                lv.setProduit(item.getProduit());
                lv.setQuantite(item.getQuantite());
                lv.setPrixUnitaire(item.getPrixUnitaire());
                lignes.add(lv);
            }

            // Enregistrement atomique via l'EJB (décrémentation des stocks incluse)
            derniereVente = venteDAO.finaliserVente(vente, lignes);
            dernieresLignes = new ArrayList<>(lignes);

            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Vente validée", "Vente n° " + idV + " enregistrée avec succès (Total : " + derniereVente.getTotal() + " FCFA)."));

            // Vider le panier et rafraîchir le catalogue
            panier.clear();
            rechercherProduits();

        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erreur lors de la vente", e.getMessage()));
        }

        return null;
    }

    public void imprimerDerniereFacture() {
        if (derniereVente != null) {
            facturePdfService.diffuserFacturePdf(derniereVente, dernieresLignes);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Aucune vente", "Aucune vente récente à imprimer."));
        }
    }

    public void imprimerFacture(Vente v) {
        if (v != null) {
            List<LigneVente> lignes = venteDAO.findLignesByVente(v.getIdV());
            facturePdfService.diffuserFacturePdf(v, lignes);
        }
    }

    // Getters et Setters
    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }
    public List<Produit> getCatalogueResultats() {
        if (catalogueResultats == null || catalogueResultats.isEmpty()) {
            initCatalogue();
        }
        return catalogueResultats;
    }
    public List<LignePanier> getPanier() { return panier; }
    public Vente getDerniereVente() { return derniereVente; }
    public int getQuantiteSaisie() { return quantiteSaisie; }
    public void setQuantiteSaisie(int quantiteSaisie) { this.quantiteSaisie = quantiteSaisie; }
}
