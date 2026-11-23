package services;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Modèle de données pour les lignes du rapport JasperReports
 */
public class LigneFactureDTO implements Serializable {

    private String referenceLot;
    private String designation;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal totalLigne;

    public LigneFactureDTO() {
    }

    public LigneFactureDTO(String referenceLot, String designation, Integer quantite, BigDecimal prixUnitaire, BigDecimal totalLigne) {
        this.referenceLot = referenceLot;
        this.designation = designation;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.totalLigne = totalLigne;
    }

    public String getReferenceLot() {
        return referenceLot;
    }

    public void setReferenceLot(String referenceLot) {
        this.referenceLot = referenceLot;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getTotalLigne() {
        return totalLigne;
    }

    public void setTotalLigne(BigDecimal totalLigne) {
        this.totalLigne = totalLigne;
    }
}
