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
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author adk
 */
@Entity
@Table(name = "utilisateur")
@NamedQueries({
    @NamedQuery(name = "Utilisateur.findAll", query = "SELECT u FROM Utilisateur u"),
    @NamedQuery(name = "Utilisateur.findByIdUtilisateur", query = "SELECT u FROM Utilisateur u WHERE u.idUtilisateur = :idUtilisateur"),
    @NamedQuery(name = "Utilisateur.findByNom", query = "SELECT u FROM Utilisateur u WHERE u.nom = :nom"),
    @NamedQuery(name = "Utilisateur.findByPrenom", query = "SELECT u FROM Utilisateur u WHERE u.prenom = :prenom"),
    @NamedQuery(name = "Utilisateur.findByMotDePasse", query = "SELECT u FROM Utilisateur u WHERE u.motDePasse = :motDePasse"),
    @NamedQuery(name = "Utilisateur.findByTypeUtilisateur", query = "SELECT u FROM Utilisateur u WHERE u.typeUtilisateur = :typeUtilisateur")})
public class Utilisateur implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "id_utilisateur")
    private String idUtilisateur;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "nom")
    private String nom;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "prenom")
    private String prenom;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "mot_de_passe")
    private String motDePasse;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "type_utilisateur")
    private String typeUtilisateur;
    @OneToMany(mappedBy = "idUtilisateurEnreg")
    private List<Produit> produitList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idGerantCreateur")
    private List<BonDeCommande> bonDeCommandeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "utilisateur")
    private List<ConsultationBonCommande> consultationBonCommandeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idGerant")
    private List<Echange> echangeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUtilisateur")
    private List<Vente> venteList;

    public Utilisateur() {
    }

    public Utilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public Utilisateur(String idUtilisateur, String nom, String prenom, String motDePasse, String typeUtilisateur) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.motDePasse = motDePasse;
        this.typeUtilisateur = typeUtilisateur;
    }

    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getTypeUtilisateur() {
        return typeUtilisateur;
    }

    public void setTypeUtilisateur(String typeUtilisateur) {
        this.typeUtilisateur = typeUtilisateur;
    }

    public List<Produit> getProduitList() {
        return produitList;
    }

    public void setProduitList(List<Produit> produitList) {
        this.produitList = produitList;
    }

    public List<BonDeCommande> getBonDeCommandeList() {
        return bonDeCommandeList;
    }

    public void setBonDeCommandeList(List<BonDeCommande> bonDeCommandeList) {
        this.bonDeCommandeList = bonDeCommandeList;
    }

    public List<ConsultationBonCommande> getConsultationBonCommandeList() {
        return consultationBonCommandeList;
    }

    public void setConsultationBonCommandeList(List<ConsultationBonCommande> consultationBonCommandeList) {
        this.consultationBonCommandeList = consultationBonCommandeList;
    }

    public List<Echange> getEchangeList() {
        return echangeList;
    }

    public void setEchangeList(List<Echange> echangeList) {
        this.echangeList = echangeList;
    }

    public List<Vente> getVenteList() {
        return venteList;
    }

    public void setVenteList(List<Vente> venteList) {
        this.venteList = venteList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUtilisateur != null ? idUtilisateur.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Utilisateur)) {
            return false;
        }
        Utilisateur other = (Utilisateur) object;
        if ((this.idUtilisateur == null && other.idUtilisateur != null) || (this.idUtilisateur != null && !this.idUtilisateur.equals(other.idUtilisateur))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Utilisateur[ idUtilisateur=" + idUtilisateur + " ]";
    }
    
}
