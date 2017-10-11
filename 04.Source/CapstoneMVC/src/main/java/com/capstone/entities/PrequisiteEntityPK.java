/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Rem
 */
@Embeddable
public class PrequisiteEntityPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "SubId")
    private String subId;
    @Basic(optional = false)
    @Column(name = "PrequisiteSubId")
    private String prequisiteSubId;

    public PrequisiteEntityPK() {
    }

    public PrequisiteEntityPK(String subId, String prequisiteSubId) {
        this.subId = subId;
        this.prequisiteSubId = prequisiteSubId;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getPrequisiteSubId() {
        return prequisiteSubId;
    }

    public void setPrequisiteSubId(String prequisiteSubId) {
        this.prequisiteSubId = prequisiteSubId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (subId != null ? subId.hashCode() : 0);
        hash += (prequisiteSubId != null ? prequisiteSubId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PrequisiteEntityPK)) {
            return false;
        }
        PrequisiteEntityPK other = (PrequisiteEntityPK) object;
        if ((this.subId == null && other.subId != null) || (this.subId != null && !this.subId.equals(other.subId))) {
            return false;
        }
        if ((this.prequisiteSubId == null && other.prequisiteSubId != null) || (this.prequisiteSubId != null && !this.prequisiteSubId.equals(other.prequisiteSubId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.PrequisiteEntityPK[ subId=" + subId + ", prequisiteSubId=" + prequisiteSubId + " ]";
    }
    
}
