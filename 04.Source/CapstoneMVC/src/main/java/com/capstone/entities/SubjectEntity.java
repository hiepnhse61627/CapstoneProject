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
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Subject", catalog = "CapstoneProject", schema = "dbo")
@NamedQueries({
    @NamedQuery(name = "SubjectEntity.findAll", query = "SELECT s FROM SubjectEntity s")})
public class SubjectEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;
    @Column(name = "Name", length = 255)
    private String name;
    @Column(name = "Abbreviation", length = 255)
    private String abbreviation;
    @Column(name = "Credits")
    private Integer credits;
    @Column(name = "IsSpecialized")
    private Boolean isSpecialized;
    @Column(name = "Type")
    private Integer type;
    @JoinTable(name = "Replacement_Subject", joinColumns = {
        @JoinColumn(name = "SubjectId", referencedColumnName = "Id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "ReplacementId", referencedColumnName = "Id", nullable = false)})
    @ManyToMany
    private List<SubjectEntity> subjectEntityList;
    @ManyToMany(mappedBy = "subjectEntityList")
    private List<SubjectEntity> subjectEntityList1;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "subjectEntity")
    private PrequisiteEntity prequisiteEntity;
    @OneToMany(mappedBy = "subjectId")
    private List<SubjectCurriculumEntity> subjectCurriculumEntityList;
    @OneToMany(mappedBy = "subjectId")
    private List<SubjectMarkComponentEntity> subjectMarkComponentEntityList;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<SubjectEntity> getSubjectEntityList() {
        return subjectEntityList;
    }

    public void setSubjectEntityList(List<SubjectEntity> subjectEntityList) {
        this.subjectEntityList = subjectEntityList;
    }

    public List<SubjectEntity> getSubjectEntityList1() {
        return subjectEntityList1;
    }

    public void setSubjectEntityList1(List<SubjectEntity> subjectEntityList1) {
        this.subjectEntityList1 = subjectEntityList1;
    }

    public PrequisiteEntity getPrequisiteEntity() {
        return prequisiteEntity;
    }

    public void setPrequisiteEntity(PrequisiteEntity prequisiteEntity) {
        this.prequisiteEntity = prequisiteEntity;
    }

    public List<SubjectCurriculumEntity> getSubjectCurriculumEntityList() {
        return subjectCurriculumEntityList;
    }

    public void setSubjectCurriculumEntityList(List<SubjectCurriculumEntity> subjectCurriculumEntityList) {
        this.subjectCurriculumEntityList = subjectCurriculumEntityList;
    }

    public List<SubjectMarkComponentEntity> getSubjectMarkComponentEntityList() {
        return subjectMarkComponentEntityList;
    }

    public void setSubjectMarkComponentEntityList(List<SubjectMarkComponentEntity> subjectMarkComponentEntityList) {
        this.subjectMarkComponentEntityList = subjectMarkComponentEntityList;
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
