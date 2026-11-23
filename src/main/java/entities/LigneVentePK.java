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
public class LigneVentePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "n_lot")
    private String nLot;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "id_v")
    private String idV;

    public LigneVentePK() {
    }

    public LigneVentePK(String nLot, String idV) {
        this.nLot = nLot;
        this.idV = idV;
    }

    public String getNLot() {
        return nLot;
    }

    public void setNLot(String nLot) {
        this.nLot = nLot;
    }

    public String getIdV() {
        return idV;
    }

    public void setIdV(String idV) {
        this.idV = idV;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nLot != null ? nLot.hashCode() : 0);
        hash += (idV != null ? idV.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LigneVentePK)) {
            return false;
        }
        LigneVentePK other = (LigneVentePK) object;
        if ((this.nLot == null && other.nLot != null) || (this.nLot != null && !this.nLot.equals(other.nLot))) {
            return false;
        }
        if ((this.idV == null && other.idV != null) || (this.idV != null && !this.idV.equals(other.idV))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.LigneVentePK[ nLot=" + nLot + ", idV=" + idV + " ]";
    }
    
}
