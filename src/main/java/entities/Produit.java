/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author adk
 */
@Entity
@Table(name = "produit")
@NamedQueries({
    @NamedQuery(name = "Produit.findAll", query = "SELECT p FROM Produit p"),
    @NamedQuery(name = "Produit.findByNLot", query = "SELECT p FROM Produit p WHERE p.nLot = :nLot"),
    @NamedQuery(name = "Produit.findByNomC", query = "SELECT p FROM Produit p WHERE p.nomC = :nomC"),
    @NamedQuery(name = "Produit.findByQte", query = "SELECT p FROM Produit p WHERE p.qte = :qte"),
    @NamedQuery(name = "Produit.findByPpa", query = "SELECT p FROM Produit p WHERE p.ppa = :ppa"),
    @NamedQuery(name = "Produit.findByDateExp", query = "SELECT p FROM Produit p WHERE p.dateExp = :dateExp"),
    @NamedQuery(name = "Produit.findByDosage", query = "SELECT p FROM Produit p WHERE p.dosage = :dosage"),
    @NamedQuery(name = "Produit.findByConditionnement", query = "SELECT p FROM Produit p WHERE p.conditionnement = :conditionnement")})
public class Produit implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "n_lot")
    private String nLot;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "nom_c")
    private String nomC;
    @Basic(optional = false)
    @NotNull
    @Column(name = "qte")
    private int qte;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "ppa")
    private BigDecimal ppa;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date_exp")
    @Temporal(TemporalType.DATE)
    private Date dateExp;
    @Size(max = 100)
    @Column(name = "dosage")
    private String dosage;
    @Size(max = 100)
    @Column(name = "conditionnement")
    private String conditionnement;
    @JoinColumn(name = "id_utilisateur_enreg", referencedColumnName = "id_utilisateur")
    @ManyToOne
    private Utilisateur idUtilisateurEnreg;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "produit")
    private List<LigneEchange> ligneEchangeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "produit")
    private List<LigneVente> ligneVenteList;

    public Produit() {
    }

    public Produit(String nLot) {
        this.nLot = nLot;
    }

    public Produit(String nLot, String nomC, int qte, BigDecimal ppa, Date dateExp) {
        this.nLot = nLot;
        this.nomC = nomC;
        this.qte = qte;
        this.ppa = ppa;
        this.dateExp = dateExp;
    }

    public String getNLot() {
        return nLot;
    }

    public void setNLot(String nLot) {
        this.nLot = nLot;
    }

    public String getNomC() {
        return nomC;
    }

    public void setNomC(String nomC) {
        this.nomC = nomC;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public BigDecimal getPpa() {
        return ppa;
    }

    public void setPpa(BigDecimal ppa) {
        this.ppa = ppa;
    }

    public Date getDateExp() {
        return dateExp;
    }

    public void setDateExp(Date dateExp) {
        this.dateExp = dateExp;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getConditionnement() {
        return conditionnement;
    }

    public void setConditionnement(String conditionnement) {
        this.conditionnement = conditionnement;
    }

    public Utilisateur getIdUtilisateurEnreg() {
        return idUtilisateurEnreg;
    }

    public void setIdUtilisateurEnreg(Utilisateur idUtilisateurEnreg) {
        this.idUtilisateurEnreg = idUtilisateurEnreg;
    }

    public List<LigneEchange> getLigneEchangeList() {
        return ligneEchangeList;
    }

    public void setLigneEchangeList(List<LigneEchange> ligneEchangeList) {
        this.ligneEchangeList = ligneEchangeList;
    }

    public List<LigneVente> getLigneVenteList() {
        return ligneVenteList;
    }

    public void setLigneVenteList(List<LigneVente> ligneVenteList) {
        this.ligneVenteList = ligneVenteList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nLot != null ? nLot.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Produit)) {
            return false;
        }
        Produit other = (Produit) object;
        if ((this.nLot == null && other.nLot != null) || (this.nLot != null && !this.nLot.equals(other.nLot))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Produit[ nLot=" + nLot + " ]";
    }
    
}
