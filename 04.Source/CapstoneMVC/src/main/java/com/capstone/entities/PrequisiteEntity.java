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
 * @author Rem
 */
@Entity
@Table(name = "Prequisite")
@NamedQueries({
    @NamedQuery(name = "PrequisiteEntity.findAll", query = "SELECT p FROM PrequisiteEntity p")
    , @NamedQuery(name = "PrequisiteEntity.findById", query = "SELECT p FROM PrequisiteEntity p WHERE p.id = :id")
    , @NamedQuery(name = "PrequisiteEntity.findByPrequisiteSubs", query = "SELECT p FROM PrequisiteEntity p WHERE p.prequisiteSubs = :prequisiteSubs")
    , @NamedQuery(name = "PrequisiteEntity.findByFailMark", query = "SELECT p FROM PrequisiteEntity p WHERE p.failMark = :failMark")})
public class PrequisiteEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "PrequisiteSubs")
    private String prequisiteSubs;
    @Column(name = "FailMark")
    private Integer failMark;
    @JoinColumn(name = "SubId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private SubjectEntity subId;

    public PrequisiteEntity() {
    }

    public PrequisiteEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrequisiteSubs() {
        return prequisiteSubs;
    }

    public void setPrequisiteSubs(String prequisiteSubs) {
        this.prequisiteSubs = prequisiteSubs;
    }

    public Integer getFailMark() {
        return failMark;
    }

    public void setFailMark(Integer failMark) {
        this.failMark = failMark;
    }

    public SubjectEntity getSubId() {
        return subId;
    }

    public void setSubId(SubjectEntity subId) {
        this.subId = subId;
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
        if (!(object instanceof PrequisiteEntity)) {
            return false;
        }
        PrequisiteEntity other = (PrequisiteEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.capstone.entities.PrequisiteEntity[ id=" + id + " ]";
    }
    
}
