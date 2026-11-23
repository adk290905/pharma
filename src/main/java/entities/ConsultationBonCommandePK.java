/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author adk
 */
@Embeddable
public class ConsultationBonCommandePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "id_utilisateur")
    private String idUtilisateur;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "id_bon")
    private String idBon;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date_consultation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateConsultation;

    public ConsultationBonCommandePK() {
    }

    public ConsultationBonCommandePK(String idUtilisateur, String idBon, Date dateConsultation) {
        this.idUtilisateur = idUtilisateur;
        this.idBon = idBon;
        this.dateConsultation = dateConsultation;
    }

    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getIdBon() {
        return idBon;
    }

    public void setIdBon(String idBon) {
        this.idBon = idBon;
    }

    public Date getDateConsultation() {
        return dateConsultation;
    }

    public void setDateConsultation(Date dateConsultation) {
        this.dateConsultation = dateConsultation;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUtilisateur != null ? idUtilisateur.hashCode() : 0);
        hash += (idBon != null ? idBon.hashCode() : 0);
        hash += (dateConsultation != null ? dateConsultation.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConsultationBonCommandePK)) {
            return false;
        }
        ConsultationBonCommandePK other = (ConsultationBonCommandePK) object;
        if ((this.idUtilisateur == null && other.idUtilisateur != null) || (this.idUtilisateur != null && !this.idUtilisateur.equals(other.idUtilisateur))) {
            return false;
        }
        if ((this.idBon == null && other.idBon != null) || (this.idBon != null && !this.idBon.equals(other.idBon))) {
            return false;
        }
        if ((this.dateConsultation == null && other.dateConsultation != null) || (this.dateConsultation != null && !this.dateConsultation.equals(other.dateConsultation))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ConsultationBonCommandePK[ idUtilisateur=" + idUtilisateur + ", idBon=" + idBon + ", dateConsultation=" + dateConsultation + " ]";
    }
    
}
