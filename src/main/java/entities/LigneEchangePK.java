/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 *
 * @author adk
 */
@Embeddable
public class LigneEchangePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "n_lot")
    private String nLot;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "id_echange")
    private String idEchange;

    public LigneEchangePK() {
    }

    public LigneEchangePK(String nLot, String idEchange) {
        this.nLot = nLot;
        this.idEchange = idEchange;
    }

    public String getNLot() {
        return nLot;
    }

    public void setNLot(String nLot) {
        this.nLot = nLot;
    }

    public String getIdEchange() {
        return idEchange;
    }

    public void setIdEchange(String idEchange) {
        this.idEchange = idEchange;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nLot != null ? nLot.hashCode() : 0);
        hash += (idEchange != null ? idEchange.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LigneEchangePK)) {
            return false;
        }
        LigneEchangePK other = (LigneEchangePK) object;
        if ((this.nLot == null && other.nLot != null) || (this.nLot != null && !this.nLot.equals(other.nLot))) {
            return false;
        }
        if ((this.idEchange == null && other.idEchange != null) || (this.idEchange != null && !this.idEchange.equals(other.idEchange))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.LigneEchangePK[ nLot=" + nLot + ", idEchange=" + idEchange + " ]";
    }
    
}
