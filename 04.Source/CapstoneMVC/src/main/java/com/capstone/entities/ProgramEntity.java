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
@Table(name = "Program")
@NamedQueries({
    @NamedQuery(name = "ProgramEntity.findAll", query = "SELECT p FROM ProgramEntity p")
    , @NamedQuery(name = "ProgramEntity.findById", query = "SELECT p FROM ProgramEntity p WHERE p.id = :id")
    , @NamedQuery(name = "ProgramEntity.findByName", query = "SELECT p FROM ProgramEntity p WHERE p.name = :name")})
public class ProgramEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "programId")
    private List<CurriculumEntity> curriculumEntityList;

    public ProgramEntity() {
    }

    public ProgramEntity(Integer id) {
        this.id = id;
    }

    public ProgramEntity(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    public List<CurriculumEntity> getCurriculumEntityList() {
        return curriculumEntityList;
    }

    public void setCurriculumEntityList(List<CurriculumEntity> curriculumEntityList) {
        this.curriculumEntityList = curriculumEntityList;
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
        if (!(object instanceof ProgramEntity)) {
            return false;
        }
        ProgramEntity other = (ProgramEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ProgramEntity[ id=" + id + " ]";
    }
    
}
