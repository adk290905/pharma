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

/**
 *
 * @author adk
 */
@Entity
@Table(name = "ligne_echange")
@NamedQueries({
    @NamedQuery(name = "LigneEchange.findAll", query = "SELECT l FROM LigneEchange l"),
    @NamedQuery(name = "LigneEchange.findByNLot", query = "SELECT l FROM LigneEchange l WHERE l.ligneEchangePK.nLot = :nLot"),
    @NamedQuery(name = "LigneEchange.findByIdEchange", query = "SELECT l FROM LigneEchange l WHERE l.ligneEchangePK.idEchange = :idEchange"),
    @NamedQuery(name = "LigneEchange.findByQuantite", query = "SELECT l FROM LigneEchange l WHERE l.quantite = :quantite")})
public class LigneEchange implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected LigneEchangePK ligneEchangePK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "quantite")
    private int quantite;
    @JoinColumn(name = "id_echange", referencedColumnName = "id_echange", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Echange echange;
    @JoinColumn(name = "n_lot", referencedColumnName = "n_lot", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Produit produit;

    public LigneEchange() {
    }

    public LigneEchange(LigneEchangePK ligneEchangePK) {
        this.ligneEchangePK = ligneEchangePK;
    }

    public LigneEchange(LigneEchangePK ligneEchangePK, int quantite) {
        this.ligneEchangePK = ligneEchangePK;
        this.quantite = quantite;
    }

    public LigneEchange(String nLot, String idEchange) {
        this.ligneEchangePK = new LigneEchangePK(nLot, idEchange);
    }

    public LigneEchangePK getLigneEchangePK() {
        return ligneEchangePK;
    }

    public void setLigneEchangePK(LigneEchangePK ligneEchangePK) {
        this.ligneEchangePK = ligneEchangePK;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Echange getEchange() {
        return echange;
    }

    public void setEchange(Echange echange) {
        this.echange = echange;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ligneEchangePK != null ? ligneEchangePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LigneEchange)) {
            return false;
        }
        LigneEchange other = (LigneEchange) object;
        if ((this.ligneEchangePK == null && other.ligneEchangePK != null) || (this.ligneEchangePK != null && !this.ligneEchangePK.equals(other.ligneEchangePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.LigneEchange[ ligneEchangePK=" + ligneEchangePK + " ]";
    }
    
}
