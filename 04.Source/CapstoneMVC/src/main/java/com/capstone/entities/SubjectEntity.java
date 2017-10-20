/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Rem
 */
@Entity
@Table(name = "Subject")
@NamedQueries({
    @NamedQuery(name = "SubjectEntity.findAll", query = "SELECT s FROM SubjectEntity s")})
public class SubjectEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    private String id;
    @Column(name = "Name")
    private String name;
    @Column(name = "Abbreviation")
    private String abbreviation;
    @Column(name = "Credits")
    private Integer credits;
    @Column(name = "IsSpecialized")
    private Boolean isSpecialized;
    @JoinTable(name = "Replacement_Subject", joinColumns = {
        @JoinColumn(name = "SubId", referencedColumnName = "Id")}, inverseJoinColumns = {
        @JoinColumn(name = "ReplacementId", referencedColumnName = "Id")})
    @ManyToMany
    private List<SubjectEntity> replacementSubjectList;
    @ManyToMany(mappedBy = "replacementSubjectList")
    private List<SubjectEntity> subjectEntityList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "subjectEntity")
    private PrequisiteEntity prequisiteEntity;
    @JoinColumn(name = "Id", referencedColumnName = "SubjectId", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private SubjectMarkComponentEntity subjectMarkComponentEntity;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "subjectEntity")
    private List<CurriculumMappingEntity> curriculumMappingEntityList;

    public SubjectEntity() {
    }

    public SubjectEntity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Boolean getIsSpecialized() {
        return isSpecialized;
    }

    public void setIsSpecialized(Boolean isSpecialized) {
        this.isSpecialized = isSpecialized;
    }

    public List<SubjectEntity> getReplacementSubjectList() {
        return replacementSubjectList;
    }

    public void setReplacementSubjectList(List<SubjectEntity> subjectEntityList) {
        this.replacementSubjectList = subjectEntityList;
    }

    public List<SubjectEntity> getSubjectEntityList() {
        return subjectEntityList;
    }

    public void setSubjectEntityList(List<SubjectEntity> subjectEntityList1) {
        this.subjectEntityList = subjectEntityList1;
    }

    public PrequisiteEntity getPrequisiteEntity() {
        return prequisiteEntity;
    }

    public void setPrequisiteEntity(PrequisiteEntity prequisite) {
        this.prequisiteEntity = prequisite;
    }

    public SubjectMarkComponentEntity getSubjectMarkComponentEntity() {
        return subjectMarkComponentEntity;
    }

    public void setSubjectMarkComponentEntity(SubjectMarkComponentEntity subjectMarkComponentEntity) {
        this.subjectMarkComponentEntity = subjectMarkComponentEntity;
    }

    public List<CurriculumMappingEntity> getCurriculumMappingEntityList() {
        return curriculumMappingEntityList;
    }

    public void setCurriculumMappingEntityList(List<CurriculumMappingEntity> curriculumMappingEntityList) {
        this.curriculumMappingEntityList = curriculumMappingEntityList;
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
        if (!(object instanceof SubjectEntity)) {
            return false;
        }
        SubjectEntity other = (SubjectEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.capstone.entities.SubjectEntity[ id=" + id + " ]";
    }
    
}
