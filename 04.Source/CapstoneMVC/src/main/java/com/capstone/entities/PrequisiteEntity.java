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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Prequisite", catalog = "CapstoneProject", schema = "dbo")
@NamedQueries({
    @NamedQuery(name = "PrequisiteEntity.findAll", query = "SELECT p FROM PrequisiteEntity p")})
public class PrequisiteEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "SubjectId", nullable = false, length = 50)
    private String subjectId;
    @Column(name = "PrequisiteSubs", length = 2147483647)
    private String prequisiteSubs;
    @Column(name = "FailMark")
    private Integer failMark;
    @JoinColumn(name = "SubjectId", referencedColumnName = "Id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private SubjectEntity subjectEntity;
    @Column(name = "EffectionSemester", length = 50)
    private String effectionSemester;
    @Column(name = "NewPrequisiteSubs", length = 2147483647)
    private String newPrequisiteSubs;
    @Column(name = "NewFailMark")
    private Integer newFailMark;

    public PrequisiteEntity() {
    }

    public String getEffectionSemester() {
        return effectionSemester;
    }

    public void setEffectionSemester(String effectionSemester) {
        this.effectionSemester = effectionSemester;
    }

    public String getNewPrequisiteSubs() {
        return newPrequisiteSubs;
    }

    public void setNewPrequisiteSubs(String newPrequisiteSubs) {
        this.newPrequisiteSubs = newPrequisiteSubs;
    }

    public Integer getNewFailMark() {
        return newFailMark;
    }

    public void setNewFailMark(Integer newFailMark) {
        this.newFailMark = newFailMark;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
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

    public SubjectEntity getSubjectEntity() {
        return subjectEntity;
    }

    public void setSubjectEntity(SubjectEntity subjectEntity) {
        this.subjectEntity = subjectEntity;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (subjectId != null ? subjectId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PrequisiteEntity)) {
            return false;
        }
        PrequisiteEntity other = (PrequisiteEntity) object;
        if ((this.subjectId == null && other.subjectId != null) || (this.subjectId != null && !this.subjectId.equals(other.subjectId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.capstone.entites.PrequisiteEntity[ subjectId=" + subjectId + " ]";
    }
    
}
