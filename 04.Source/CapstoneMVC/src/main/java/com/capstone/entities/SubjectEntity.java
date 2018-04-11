/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Subject")
@NamedQueries({
        @NamedQuery(name = "SubjectEntity.findAll", query = "SELECT s FROM SubjectEntity s"),
        @NamedQuery(name = "SubjectEntity.findById", query = "SELECT s FROM SubjectEntity s WHERE s.id = :id"),
        @NamedQuery(name = "SubjectEntity.findByName", query = "SELECT s FROM SubjectEntity s WHERE s.name = :name"),
        @NamedQuery(name = "SubjectEntity.findByAbbreviation", query = "SELECT s FROM SubjectEntity s WHERE s.abbreviation = :abbreviation"),
        @NamedQuery(name = "SubjectEntity.findByIsSpecialized", query = "SELECT s FROM SubjectEntity s WHERE s.isSpecialized = :isSpecialized"),
        @NamedQuery(name = "SubjectEntity.findByType", query = "SELECT s FROM SubjectEntity s WHERE s.type = :type"),
        @NamedQuery(name = "SubjectEntity.findByVnName", query = "SELECT s FROM SubjectEntity s WHERE s.vnName = :vnName")})
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
    @Column(name = "IsSpecialized")
    private Boolean isSpecialized;
    @Column(name = "Type")
    private Integer type;
    @Column(name = "VnName")
    private String vnName;
    @JoinTable(name = "Replacement_Subject", joinColumns = {
            @JoinColumn(name = "SubjectId", referencedColumnName = "Id")}, inverseJoinColumns = {
            @JoinColumn(name = "ReplacementId", referencedColumnName = "Id")})
    @ManyToMany
    private List<SubjectEntity> subjectEntityList;
    @ManyToMany(mappedBy = "subjectEntityList")
    private List<SubjectEntity> subjectEntityList1;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "subjectEntity")
    private PrequisiteEntity prequisiteEntity;
    @OneToMany(mappedBy = "subjectId")
    private List<EmpCompetenceEntity> empCompetenceEntityList;
    @OneToMany(mappedBy = "subjectId")
    private List<SubjectCurriculumEntity> subjectCurriculumEntityList;
    @OneToMany(mappedBy = "subjectId")
    private List<SubjectMarkComponentEntity> subjectMarkComponentEntityList;
    @JoinColumn(name = "DepartmentId", referencedColumnName = "DeptId")
    @ManyToOne
    private DepartmentEntity departmentId;

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

    public String getVnName() {
        return vnName;
    }

    public void setVnName(String vnName) {
        this.vnName = vnName;
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

    public List<EmpCompetenceEntity> getEmpCompetenceEntityList() {
        return empCompetenceEntityList;
    }

    public void setEmpCompetenceEntityList(List<EmpCompetenceEntity> empCompetenceEntityList) {
        this.empCompetenceEntityList = empCompetenceEntityList;
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



    public DepartmentEntity getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(DepartmentEntity departmentId) {
        this.departmentId = departmentId;
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
        return "javaapplication110.SubjectEntity[ id=" + id + " ]";
    }

}
