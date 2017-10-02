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
public class CurriculumMappingEntityPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "CurId")
    private int curId;
    @Basic(optional = false)
    @Column(name = "SubId")
    private String subId;

    public CurriculumMappingEntityPK() {
    }

    public CurriculumMappingEntityPK(int curId, String subId) {
        this.curId = curId;
        this.subId = subId;
    }

    public int getCurId() {
        return curId;
    }

    public void setCurId(int curId) {
        this.curId = curId;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) curId;
        hash += (subId != null ? subId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CurriculumMappingEntityPK)) {
            return false;
        }
        CurriculumMappingEntityPK other = (CurriculumMappingEntityPK) object;
        if (this.curId != other.curId) {
            return false;
        }
        if ((this.subId == null && other.subId != null) || (this.subId != null && !this.subId.equals(other.subId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.CurriculumMappingEntityPK[ curId=" + curId + ", subId=" + subId + " ]";
    }
    
}
