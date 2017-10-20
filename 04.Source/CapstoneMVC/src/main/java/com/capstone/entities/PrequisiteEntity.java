/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Rem
 */
@Entity
@Table(name = "Prequisite")
@NamedQueries({
    @NamedQuery(name = "PrequisiteEntity.findAll", query = "SELECT p FROM PrequisiteEntity p")})
public class PrequisiteEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "SubId")
    private String subId;
    @Column(name = "PrequisiteSubs")
    private String prequisiteSubs;
    @Column(name = "FailMark")
    private Integer failMark;
    @JoinColumn(name = "SubId", referencedColumnName = "Id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private SubjectEntity subjectEntity;

    public PrequisiteEntity() {
    }

    public PrequisiteEntity(String subId) {
        this.subId = subId;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getPrequisiteSubs() {
        return prequisiteSubs;
    }

    public void setPrequisiteSubs(String prequisiteSubs) {
        this.prequisiteSubs = prequisiteSubs;
    }

    public Integer getFailMark() {
        return failMark;
    }

    public void setFailMark(Integer failMark) {
        this.failMark = failMark;
    }

    public SubjectEntity getSubjectEntity() {
        return subjectEntity;
    }

    public void setSubjectEntity(SubjectEntity subjectEntity) {
        this.subjectEntity = subjectEntity;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (subId != null ? subId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PrequisiteEntity)) {
            return false;
        }
        PrequisiteEntity other = (PrequisiteEntity) object;
        if ((this.subId == null && other.subId != null) || (this.subId != null && !this.subId.equals(other.subId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.capstone.entities.PrequisiteEntity[ subId=" + subId + " ]";
    }
    
}
