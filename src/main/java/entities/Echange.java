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
@Table(name = "echange")
@NamedQueries({
    @NamedQuery(name = "Echange.findAll", query = "SELECT e FROM Echange e"),
    @NamedQuery(name = "Echange.findByIdEchange", query = "SELECT e FROM Echange e WHERE e.idEchange = :idEchange"),
    @NamedQuery(name = "Echange.findByIdPh", query = "SELECT e FROM Echange e WHERE e.idPh = :idPh"),
    @NamedQuery(name = "Echange.findByNomPh", query = "SELECT e FROM Echange e WHERE e.nomPh = :nomPh"),
    @NamedQuery(name = "Echange.findByTypeEchange", query = "SELECT e FROM Echange e WHERE e.typeEchange = :typeEchange"),
    @NamedQuery(name = "Echange.findByDateEchange", query = "SELECT e FROM Echange e WHERE e.dateEchange = :dateEchange")})
public class Echange implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "id_echange")
    private String idEchange;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "id_ph")
    private String idPh;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "nom_ph")
    private String nomPh;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "type_echange")
    private String typeEchange;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date_echange")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEchange;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "echange")
    private List<LigneEchange> ligneEchangeList;
    @JoinColumn(name = "id_gerant", referencedColumnName = "id_utilisateur")
    @ManyToOne(optional = false)
    private Utilisateur idGerant;

    public Echange() {
    }

    public Echange(String idEchange) {
        this.idEchange = idEchange;
    }

    public Echange(String idEchange, String idPh, String nomPh, String typeEchange, Date dateEchange) {
        this.idEchange = idEchange;
        this.idPh = idPh;
        this.nomPh = nomPh;
        this.typeEchange = typeEchange;
        this.dateEchange = dateEchange;
    }

    public String getIdEchange() {
        return idEchange;
    }

    public void setIdEchange(String idEchange) {
        this.idEchange = idEchange;
    }

    public String getIdPh() {
        return idPh;
    }

    public void setIdPh(String idPh) {
        this.idPh = idPh;
    }

    public String getNomPh() {
        return nomPh;
    }

    public void setNomPh(String nomPh) {
        this.nomPh = nomPh;
    }

    public String getTypeEchange() {
        return typeEchange;
    }

    public void setTypeEchange(String typeEchange) {
        this.typeEchange = typeEchange;
    }

    public Date getDateEchange() {
        return dateEchange;
    }

    public void setDateEchange(Date dateEchange) {
        this.dateEchange = dateEchange;
    }

    public List<LigneEchange> getLigneEchangeList() {
        return ligneEchangeList;
    }

    public void setLigneEchangeList(List<LigneEchange> ligneEchangeList) {
        this.ligneEchangeList = ligneEchangeList;
    }

    public Utilisateur getIdGerant() {
        return idGerant;
    }

    public void setIdGerant(Utilisateur idGerant) {
        this.idGerant = idGerant;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEchange != null ? idEchange.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Echange)) {
            return false;
        }
        Echange other = (Echange) object;
        if ((this.idEchange == null && other.idEchange != null) || (this.idEchange != null && !this.idEchange.equals(other.idEchange))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Echange[ idEchange=" + idEchange + " ]";
    }
    
}
