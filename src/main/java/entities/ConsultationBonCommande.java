/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author adk
 */
@Entity
@Table(name = "consultation_bon_commande")
@NamedQueries({
    @NamedQuery(name = "ConsultationBonCommande.findAll", query = "SELECT c FROM ConsultationBonCommande c"),
    @NamedQuery(name = "ConsultationBonCommande.findByIdUtilisateur", query = "SELECT c FROM ConsultationBonCommande c WHERE c.consultationBonCommandePK.idUtilisateur = :idUtilisateur"),
    @NamedQuery(name = "ConsultationBonCommande.findByIdBon", query = "SELECT c FROM ConsultationBonCommande c WHERE c.consultationBonCommandePK.idBon = :idBon"),
    @NamedQuery(name = "ConsultationBonCommande.findByDateConsultation", query = "SELECT c FROM ConsultationBonCommande c WHERE c.consultationBonCommandePK.dateConsultation = :dateConsultation")})
public class ConsultationBonCommande implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConsultationBonCommandePK consultationBonCommandePK;
    @JoinColumn(name = "id_bon", referencedColumnName = "id_bon", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private BonDeCommande bonDeCommande;
    @JoinColumn(name = "id_utilisateur", referencedColumnName = "id_utilisateur", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Utilisateur utilisateur;

    public ConsultationBonCommande() {
    }

    public ConsultationBonCommande(ConsultationBonCommandePK consultationBonCommandePK) {
        this.consultationBonCommandePK = consultationBonCommandePK;
    }

    public ConsultationBonCommande(String idUtilisateur, String idBon, Date dateConsultation) {
        this.consultationBonCommandePK = new ConsultationBonCommandePK(idUtilisateur, idBon, dateConsultation);
    }

    public ConsultationBonCommandePK getConsultationBonCommandePK() {
        return consultationBonCommandePK;
    }

    public void setConsultationBonCommandePK(ConsultationBonCommandePK consultationBonCommandePK) {
        this.consultationBonCommandePK = consultationBonCommandePK;
    }

    public BonDeCommande getBonDeCommande() {
        return bonDeCommande;
    }

    public void setBonDeCommande(BonDeCommande bonDeCommande) {
        this.bonDeCommande = bonDeCommande;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (consultationBonCommandePK != null ? consultationBonCommandePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConsultationBonCommande)) {
            return false;
        }
        ConsultationBonCommande other = (ConsultationBonCommande) object;
        if ((this.consultationBonCommandePK == null && other.consultationBonCommandePK != null) || (this.consultationBonCommandePK != null && !this.consultationBonCommandePK.equals(other.consultationBonCommandePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ConsultationBonCommande[ consultationBonCommandePK=" + consultationBonCommandePK + " ]";
    }
    
}
