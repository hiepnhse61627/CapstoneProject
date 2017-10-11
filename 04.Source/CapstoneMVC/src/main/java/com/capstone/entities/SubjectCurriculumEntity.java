/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Rem
 */
@Entity
@Table(name = "Subject_Curriculum")
@NamedQueries({
    @NamedQuery(name = "SubjectCurriculumEntity.findAll", query = "SELECT s FROM SubjectCurriculumEntity s")
    , @NamedQuery(name = "SubjectCurriculumEntity.findById", query = "SELECT s FROM SubjectCurriculumEntity s WHERE s.id = :id")
    , @NamedQuery(name = "SubjectCurriculumEntity.findByName", query = "SELECT s FROM SubjectCurriculumEntity s WHERE s.name = :name")
    , @NamedQuery(name = "SubjectCurriculumEntity.findByDescription", query = "SELECT s FROM SubjectCurriculumEntity s WHERE s.description = :description")})
public class SubjectCurriculumEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Name")
    private String name;
    @Column(name = "Description")
    private String description;
    @JoinColumn(name = "ProgramId", referencedColumnName = "Id")
    @ManyToOne(fetch = FetchType.EAGER)
    private ProgramEntity programId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "subjectCurriculumEntity")
    private List<CurriculumMappingEntity> curriculumMappingEntityList;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CurriculumMappingEntity> getCurriculumMappingEntityList() {
        if (this.curriculumMappingEntityList == null) {
            this.curriculumMappingEntityList = new ArrayList<>();
        }
        return curriculumMappingEntityList;
    }

    public void setCurriculumMappingEntityList(List<CurriculumMappingEntity> curriculumMappingEntityList) {
        this.curriculumMappingEntityList = curriculumMappingEntityList;
    }

    public ProgramEntity getProgramId() {
        return programId;
    }

    public void setProgramId(ProgramEntity programId) {
        this.programId = programId;
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
        return "entities.SubjectCurriculumEntity[ id=" + id + " ]";
    }
    
}
