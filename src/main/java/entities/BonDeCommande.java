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
import java.util.Date;
import java.util.List;

/**
 *
 * @author adk
 */
@Entity
@Table(name = "bon_de_commande")
@NamedQueries({
    @NamedQuery(name = "BonDeCommande.findAll", query = "SELECT b FROM BonDeCommande b"),
    @NamedQuery(name = "BonDeCommande.findByIdBon", query = "SELECT b FROM BonDeCommande b WHERE b.idBon = :idBon"),
    @NamedQuery(name = "BonDeCommande.findByDateBon", query = "SELECT b FROM BonDeCommande b WHERE b.dateBon = :dateBon")})
public class BonDeCommande implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "id_bon")
    private String idBon;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date_bon")
    @Temporal(TemporalType.DATE)
    private Date dateBon;
    @JoinColumn(name = "id_gerant_createur", referencedColumnName = "id_utilisateur")
    @ManyToOne(optional = false)
    private Utilisateur idGerantCreateur;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bonDeCommande")
    private List<ConsultationBonCommande> consultationBonCommandeList;

    public BonDeCommande() {
    }

    public BonDeCommande(String idBon) {
        this.idBon = idBon;
    }

    public BonDeCommande(String idBon, Date dateBon) {
        this.idBon = idBon;
        this.dateBon = dateBon;
    }

    public String getIdBon() {
        return idBon;
    }

    public void setIdBon(String idBon) {
        this.idBon = idBon;
    }

    public Date getDateBon() {
        return dateBon;
    }

    public void setDateBon(Date dateBon) {
        this.dateBon = dateBon;
    }

    public Utilisateur getIdGerantCreateur() {
        return idGerantCreateur;
    }

    public void setIdGerantCreateur(Utilisateur idGerantCreateur) {
        this.idGerantCreateur = idGerantCreateur;
    }

    public List<ConsultationBonCommande> getConsultationBonCommandeList() {
        return consultationBonCommandeList;
    }

    public void setConsultationBonCommandeList(List<ConsultationBonCommande> consultationBonCommandeList) {
        this.consultationBonCommandeList = consultationBonCommandeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idBon != null ? idBon.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BonDeCommande)) {
            return false;
        }
        BonDeCommande other = (BonDeCommande) object;
        if ((this.idBon == null && other.idBon != null) || (this.idBon != null && !this.idBon.equals(other.idBon))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.BonDeCommande[ idBon=" + idBon + " ]";
    }
    
}
