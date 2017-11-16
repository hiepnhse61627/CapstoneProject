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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Subject_Curriculum", catalog = "CapstoneProject", schema = "dbo")
@NamedQueries({
    @NamedQuery(name = "SubjectCurriculumEntity.findAll", query = "SELECT s FROM SubjectCurriculumEntity s")})
public class SubjectCurriculumEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "OrdinalNumber")
    private Integer ordinalNumber;
    @Column(name = "TermNumber")
    private Integer termNumber;
    @Column(name = "SubjectCredits")
    private Integer subjectCredits;
    @Column(name = "Required")
    private boolean required;
    @JoinColumn(name = "CurriculumId", referencedColumnName = "Id")
    @ManyToOne
    private CurriculumEntity curriculumId;
    @JoinColumn(name = "SubjectId", referencedColumnName = "Id")
    @ManyToOne
    private SubjectEntity subjectId;

    public SubjectCurriculumEntity() {
    }

    public SubjectCurriculumEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrdinalNumber() {
        return ordinalNumber;
    }

    public Integer getSubjectCredits() {
        return subjectCredits;
    }

    public void setSubjectCredits(Integer subjectCredits) {
        this.subjectCredits = subjectCredits;
    }

    public void setOrdinalNumber(Integer ordinalNumber) {
        this.ordinalNumber = ordinalNumber;
    }

    public Integer getTermNumber() {
        return termNumber;
    }

    public void setTermNumber(Integer termNumber) {
        this.termNumber = termNumber;
    }

    public CurriculumEntity getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(CurriculumEntity curriculumId) {
        this.curriculumId = curriculumId;
    }

    public SubjectEntity getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(SubjectEntity subjectId) {
        this.subjectId = subjectId;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SubjectCurriculumEntity)) {
            return false;
        }
        SubjectCurriculumEntity other = (SubjectCurriculumEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.capstone.entities.SubjectCurriculumEntity[ id=" + id + " ]";
    }
    
}
