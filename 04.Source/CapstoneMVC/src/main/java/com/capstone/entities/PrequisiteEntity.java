/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Rem
 */
@Entity
@Table(name = "Prequisite")
@NamedQueries({
    @NamedQuery(name = "PrequisiteEntity.findAll", query = "SELECT p FROM PrequisiteEntity p")
    , @NamedQuery(name = "PrequisiteEntity.findBySubId", query = "SELECT p FROM PrequisiteEntity p WHERE p.prequisiteEntityPK.subId = :subId")
    , @NamedQuery(name = "PrequisiteEntity.findByPrequisiteSubId", query = "SELECT p FROM PrequisiteEntity p WHERE p.prequisiteEntityPK.prequisiteSubId = :prequisiteSubId")
    , @NamedQuery(name = "PrequisiteEntity.findByFailMark", query = "SELECT p FROM PrequisiteEntity p WHERE p.failMark = :failMark")})
public class PrequisiteEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PrequisiteEntityPK prequisiteEntityPK;
    @Column(name = "FailMark")
    private Integer failMark;
    @JoinColumn(name = "SubId", referencedColumnName = "Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private SubjectEntity subjectEntity;
    @JoinColumn(name = "PrequisiteSubId", referencedColumnName = "Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private SubjectEntity prequisiteSubjectEntity;

    public PrequisiteEntity() {
    }

    public PrequisiteEntity(PrequisiteEntityPK prequisiteEntityPK) {
        this.prequisiteEntityPK = prequisiteEntityPK;
    }

    public PrequisiteEntity(String subId, String prequisiteSubId) {
        this.prequisiteEntityPK = new PrequisiteEntityPK(subId, prequisiteSubId);
    }

    public PrequisiteEntityPK getPrequisiteEntityPK() {
        return prequisiteEntityPK;
    }

    public void setPrequisiteEntityPK(PrequisiteEntityPK prequisiteEntityPK) {
        this.prequisiteEntityPK = prequisiteEntityPK;
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

    public SubjectEntity getPrequisiteSubjectEntity() {
        return prequisiteSubjectEntity;
    }

    public void setPrequisiteSubjectEntity(SubjectEntity prequisiteSubjectEntity) {
        this.prequisiteSubjectEntity = prequisiteSubjectEntity;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (prequisiteEntityPK != null ? prequisiteEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PrequisiteEntity)) {
            return false;
        }
        PrequisiteEntity other = (PrequisiteEntity) object;
        if ((this.prequisiteEntityPK == null && other.prequisiteEntityPK != null) || (this.prequisiteEntityPK != null && !this.prequisiteEntityPK.equals(other.prequisiteEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.PrequisiteEntity[ prequisiteEntityPK=" + prequisiteEntityPK + " ]";
    }
    
}
