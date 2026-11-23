package controllers;

import dao.VenteDAO;
import entities.LigneVente;
import entities.Vente;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import services.FacturePdfService;

/**
 * Contrôleur CDI pour le journal de caisse, les statistiques financières et l'impression des factures
 */
@Named("caisseBean")
@RequestScoped
public class CaisseBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private VenteDAO venteDAO;

    @Inject
    private FacturePdfService facturePdfService;

    public List<Vente> getVentes() {
        return venteDAO.findAllOrderByDateDesc();
    }

    public BigDecimal getTotalChiffreAffaires() {
        return venteDAO.calculerChiffreAffairesTotal();
    }

    public BigDecimal getVentesAujourdhui() {
        return venteDAO.calculerVentesAujourdhui();
    }

    public int getNombreVentes() {
        return venteDAO.count();
    }

    public void imprimerFacture(Vente v) {
        if (v != null) {
            List<LigneVente> lignes = venteDAO.findLignesByVente(v.getIdV());
            facturePdfService.diffuserFacturePdf(v, lignes);
        }
    }
}
