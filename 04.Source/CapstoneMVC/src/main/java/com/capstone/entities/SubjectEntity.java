/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Subject", catalog = "CapstoneProject", schema = "dbo")
public class SubjectEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "Id", nullable = false, length = 50)
    private String id;
    @Column(name = "Name", length = 255)
    private String name;
    @Column(name = "Abbreviation", length = 255)
    private String abbreviation;
    @Column(name = "Credits")
    private Integer credits;
    @OneToMany(mappedBy = "prequisiteId")
    private List<SubjectEntity> subjectList;
    @JoinColumn(name = "PrequisiteId", referencedColumnName = "Id")
    @ManyToOne(cascade = CascadeType.ALL)
    private SubjectEntity prequisiteId;
    @JoinColumn(name = "Id", referencedColumnName = "SubjectId", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private SubjectMarkComponentEntity subjectMarkComponent;

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

    public List<SubjectEntity> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<SubjectEntity> subjectList) {
        this.subjectList = subjectList;
    }

    public SubjectEntity getPrequisiteId() {
        return prequisiteId;
    }

    public void setPrequisiteId(SubjectEntity prequisiteId) {
        this.prequisiteId = prequisiteId;
    }

    public SubjectMarkComponentEntity getSubjectMarkComponent() {
        return subjectMarkComponent;
    }

    public void setSubjectMarkComponent(SubjectMarkComponentEntity subjectMarkComponent) {
        this.subjectMarkComponent = subjectMarkComponent;
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
        return "entities.Subject[ id=" + id + " ]";
    }
    
}
