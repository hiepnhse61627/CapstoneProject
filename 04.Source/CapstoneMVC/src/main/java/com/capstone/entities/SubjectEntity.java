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
    @NamedQuery(name = "SubjectEntity.findAll", query = "SELECT s FROM SubjectEntity s")
    , @NamedQuery(name = "SubjectEntity.findById", query = "SELECT s FROM SubjectEntity s WHERE s.id = :id")
    , @NamedQuery(name = "SubjectEntity.findByName", query = "SELECT s FROM SubjectEntity s WHERE s.name = :name")
    , @NamedQuery(name = "SubjectEntity.findByAbbreviation", query = "SELECT s FROM SubjectEntity s WHERE s.abbreviation = :abbreviation")
    , @NamedQuery(name = "SubjectEntity.findByCredits", query = "SELECT s FROM SubjectEntity s WHERE s.credits = :credits")})
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
    private Boolean specialized;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "subjectEntity")
    private List<PrequisiteEntity> prequisiteEntityList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prequisiteSubjectEntity")
    private List<PrequisiteEntity> subOfPrequisiteList;
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

    public List<PrequisiteEntity> getSubOfPrequisiteList() {
        return subOfPrequisiteList;
    }

    public void setSubOfPrequisiteList(List<PrequisiteEntity> subOfPrequisiteList) {
        this.subOfPrequisiteList = subOfPrequisiteList;
    }

    public List<PrequisiteEntity> getPrequisiteEntityList() {
        return prequisiteEntityList;
    }

    public void setPrequisiteEntityList(List<PrequisiteEntity> prequisiteEntityList) {
        this.prequisiteEntityList = prequisiteEntityList;
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

    public Boolean isSpecialized() {
        return specialized;
    }

    public void setSpecialized(Boolean specialized) {
        this.specialized = specialized;
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
        return "entities.SubjectEntity[ id=" + id + " ]";
    }
    
}
