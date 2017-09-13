package com.capstone.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Subject", catalog = "CapstoneProject", schema = "dbo")
public class Subject implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "SubjectId", nullable = false, length = 50)
    private String subjectId;
    @Column(name = "SubjectName", length = 255)
    private String subjectName;
    @OneToMany(mappedBy = "subjectId")
    private List<SubjectMarkComponent> subjectMarkComponentList;

    public Subject() {
    }

    public Subject(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public List<SubjectMarkComponent> getSubjectMarkComponentList() {
        return subjectMarkComponentList;
    }

    public void setSubjectMarkComponentList(List<SubjectMarkComponent> subjectMarkComponentList) {
        this.subjectMarkComponentList = subjectMarkComponentList;
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
        if (!(object instanceof Subject)) {
            return false;
        }
        Subject other = (Subject) object;
        if ((this.subjectId == null && other.subjectId != null) || (this.subjectId != null && !this.subjectId.equals(other.subjectId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Subject[ subjectId=" + subjectId + " ]";
    }

}

