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
@Table(name = "Curriculum_Mapping")
@NamedQueries({
    @NamedQuery(name = "CurriculumMappingEntity.findAll", query = "SELECT c FROM CurriculumMappingEntity c")
    , @NamedQuery(name = "CurriculumMappingEntity.findByCurId", query = "SELECT c FROM CurriculumMappingEntity c WHERE c.curriculumMappingEntityPK.curId = :curId")
    , @NamedQuery(name = "CurriculumMappingEntity.findBySubId", query = "SELECT c FROM CurriculumMappingEntity c WHERE c.curriculumMappingEntityPK.subId = :subId")
    , @NamedQuery(name = "CurriculumMappingEntity.findByTerm", query = "SELECT c FROM CurriculumMappingEntity c WHERE c.term = :term")
    , @NamedQuery(name = "CurriculumMappingEntity.findByOrdering", query = "SELECT c FROM CurriculumMappingEntity c WHERE c.ordering = :ordering")})
public class CurriculumMappingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CurriculumMappingEntityPK curriculumMappingEntityPK;
    @Column(name = "Term")
    private String term;
    @Column(name = "Ordering")
    private Integer ordering;
    @JoinColumn(name = "SubId", referencedColumnName = "Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private SubjectEntity subjectEntity;
    @JoinColumn(name = "CurId", referencedColumnName = "Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private SubjectCurriculumEntity subjectCurriculumEntity;

    public CurriculumMappingEntity() {
    }

    public CurriculumMappingEntity(CurriculumMappingEntityPK curriculumMappingEntityPK) {
        this.curriculumMappingEntityPK = curriculumMappingEntityPK;
    }

    public CurriculumMappingEntity(int curId, String subId) {
        this.curriculumMappingEntityPK = new CurriculumMappingEntityPK(curId, subId);
    }

    public CurriculumMappingEntityPK getCurriculumMappingEntityPK() {
        return curriculumMappingEntityPK;
    }

    public void setCurriculumMappingEntityPK(CurriculumMappingEntityPK curriculumMappingEntityPK) {
        this.curriculumMappingEntityPK = curriculumMappingEntityPK;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Integer getOrdering() {
        return ordering;
    }

    public void setOrdering(Integer ordering) {
        this.ordering = ordering;
    }

    public SubjectEntity getSubjectEntity() {
        return subjectEntity;
    }

    public void setSubjectEntity(SubjectEntity subjectEntity) {
        this.subjectEntity = subjectEntity;
    }

    public SubjectCurriculumEntity getSubjectCurriculumEntity() {
        return subjectCurriculumEntity;
    }

    public void setSubjectCurriculumEntity(SubjectCurriculumEntity subjectCurriculumEntity) {
        this.subjectCurriculumEntity = subjectCurriculumEntity;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (curriculumMappingEntityPK != null ? curriculumMappingEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CurriculumMappingEntity)) {
            return false;
        }
        CurriculumMappingEntity other = (CurriculumMappingEntity) object;
        if ((this.curriculumMappingEntityPK == null && other.curriculumMappingEntityPK != null) || (this.curriculumMappingEntityPK != null && !this.curriculumMappingEntityPK.equals(other.curriculumMappingEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.CurriculumMappingEntity[ curriculumMappingEntityPK=" + curriculumMappingEntityPK + " ]";
    }
    
}
