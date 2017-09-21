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
@Table(name = "Subject_MarkComponent", catalog = "CapstoneProject", schema = "dbo")
public class SubjectMarkComponentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "SubjectId", nullable = false, length = 50)
    private String subjectId;
    @Column(name = "ComponentPercent")
    private Integer componentPercent;
    @OneToMany(mappedBy = "subjectId")
    private List<MarksEntity> marksList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "subjectMarkComponent")
    private SubjectEntity subject;

    public SubjectMarkComponentEntity() {
    }

    public SubjectMarkComponentEntity(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getComponentPercent() {
        return componentPercent;
    }

    public void setComponentPercent(Integer componentPercent) {
        this.componentPercent = componentPercent;
    }

    public List<MarksEntity> getMarksList() {
        return marksList;
    }

    public void setMarksList(List<MarksEntity> marksList) {
        this.marksList = marksList;
    }

    public SubjectEntity getSubject() {
        return subject;
    }

    public void setSubject(SubjectEntity subject) {
        this.subject = subject;
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
        if (!(object instanceof SubjectMarkComponentEntity)) {
            return false;
        }
        SubjectMarkComponentEntity other = (SubjectMarkComponentEntity) object;
        if ((this.subjectId == null && other.subjectId != null) || (this.subjectId != null && !this.subjectId.equals(other.subjectId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.SubjectMarkComponent[ subjectId=" + subjectId + " ]";
    }
    
}
