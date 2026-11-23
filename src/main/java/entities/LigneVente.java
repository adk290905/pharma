/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author adk
 */
@Entity
@Table(name = "ligne_vente")
@NamedQueries({
    @NamedQuery(name = "LigneVente.findAll", query = "SELECT l FROM LigneVente l"),
    @NamedQuery(name = "LigneVente.findByNLot", query = "SELECT l FROM LigneVente l WHERE l.ligneVentePK.nLot = :nLot"),
    @NamedQuery(name = "LigneVente.findByIdV", query = "SELECT l FROM LigneVente l WHERE l.ligneVentePK.idV = :idV"),
    @NamedQuery(name = "LigneVente.findByQuantite", query = "SELECT l FROM LigneVente l WHERE l.quantite = :quantite"),
    @NamedQuery(name = "LigneVente.findByPrixUnitaire", query = "SELECT l FROM LigneVente l WHERE l.prixUnitaire = :prixUnitaire")})
public class LigneVente implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected LigneVentePK ligneVentePK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "quantite")
    private int quantite;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "prix_unitaire")
    private BigDecimal prixUnitaire;
    @JoinColumn(name = "n_lot", referencedColumnName = "n_lot", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Produit produit;
    @JoinColumn(name = "id_v", referencedColumnName = "id_v", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Vente vente;

    public LigneVente() {
    }

    public LigneVente(LigneVentePK ligneVentePK) {
        this.ligneVentePK = ligneVentePK;
    }

    public LigneVente(LigneVentePK ligneVentePK, int quantite, BigDecimal prixUnitaire) {
        this.ligneVentePK = ligneVentePK;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    public LigneVente(String nLot, String idV) {
        this.ligneVentePK = new LigneVentePK(nLot, idV);
    }

    public LigneVentePK getLigneVentePK() {
        return ligneVentePK;
    }

    public void setLigneVentePK(LigneVentePK ligneVentePK) {
        this.ligneVentePK = ligneVentePK;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Vente getVente() {
        return vente;
    }

    public void setVente(Vente vente) {
        this.vente = vente;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ligneVentePK != null ? ligneVentePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LigneVente)) {
            return false;
        }
        LigneVente other = (LigneVente) object;
        if ((this.ligneVentePK == null && other.ligneVentePK != null) || (this.ligneVentePK != null && !this.ligneVentePK.equals(other.ligneVentePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.LigneVente[ ligneVentePK=" + ligneVentePK + " ]";
    }
    
}
