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
@Table(name = "vente")
@NamedQueries({
    @NamedQuery(name = "Vente.findAll", query = "SELECT v FROM Vente v"),
    @NamedQuery(name = "Vente.findByIdV", query = "SELECT v FROM Vente v WHERE v.idV = :idV"),
    @NamedQuery(name = "Vente.findByNomC", query = "SELECT v FROM Vente v WHERE v.nomC = :nomC"),
    @NamedQuery(name = "Vente.findByQte", query = "SELECT v FROM Vente v WHERE v.qte = :qte"),
    @NamedQuery(name = "Vente.findByPpa", query = "SELECT v FROM Vente v WHERE v.ppa = :ppa"),
    @NamedQuery(name = "Vente.findByTotal", query = "SELECT v FROM Vente v WHERE v.total = :total"),
    @NamedQuery(name = "Vente.findByDateVente", query = "SELECT v FROM Vente v WHERE v.dateVente = :dateVente")})
public class Vente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "id_v")
    private String idV;
    @Size(max = 150)
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
    @Column(name = "total")
    private BigDecimal total;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date_vente")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateVente;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vente")
    private List<LigneVente> ligneVenteList;
    @JoinColumn(name = "id_utilisateur", referencedColumnName = "id_utilisateur")
    @ManyToOne(optional = false)
    private Utilisateur idUtilisateur;

    public Vente() {
    }

    public Vente(String idV) {
        this.idV = idV;
    }

    public Vente(String idV, int qte, BigDecimal ppa, BigDecimal total, Date dateVente) {
        this.idV = idV;
        this.qte = qte;
        this.ppa = ppa;
        this.total = total;
        this.dateVente = dateVente;
    }

    public String getIdV() {
        return idV;
    }

    public void setIdV(String idV) {
        this.idV = idV;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Date getDateVente() {
        return dateVente;
    }

    public void setDateVente(Date dateVente) {
        this.dateVente = dateVente;
    }

    public List<LigneVente> getLigneVenteList() {
        return ligneVenteList;
    }

    public void setLigneVenteList(List<LigneVente> ligneVenteList) {
        this.ligneVenteList = ligneVenteList;
    }

    public Utilisateur getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Utilisateur idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idV != null ? idV.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Vente)) {
            return false;
        }
        Vente other = (Vente) object;
        if ((this.idV == null && other.idV != null) || (this.idV != null && !this.idV.equals(other.idV))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Vente[ idV=" + idV + " ]";
    }
    
}
