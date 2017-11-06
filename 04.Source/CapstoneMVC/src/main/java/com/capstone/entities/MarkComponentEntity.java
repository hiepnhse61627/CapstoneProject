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
@Table(name = "MarkComponent", catalog = "CapstoneProject", schema = "dbo")
@NamedQueries({
    @NamedQuery(name = "MarkComponentEntity.findAll", query = "SELECT m FROM MarkComponentEntity m")})
public class MarkComponentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Name", length = 2147483647)
    private String name;
    @OneToMany(mappedBy = "markComponentId")
    private List<SubjectMarkComponentEntity> subjectMarkComponentEntityList;

    public MarkComponentEntity() {
    }

    public MarkComponentEntity(Integer id) {
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
        if (!(object instanceof MarkComponentEntity)) {
            return false;
        }
        MarkComponentEntity other = (MarkComponentEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.capstone.entities.MarkComponentEntity[ id=" + id + " ]";
    }
    
}
