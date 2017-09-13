package com.capstone.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Subject_MarkComponent", catalog = "CapstoneProject", schema = "dbo")
public class SubjectMarkComponent implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Column(name = "ComponentPercent")
    private Integer componentPercent;
    @Column(name = "ComponentMarkId")
    private Integer componentMarkId;
    @JoinColumn(name = "SubjectId", referencedColumnName = "SubjectId")
    @ManyToOne
    private Subject subjectId;
    @OneToMany(mappedBy = "markComponentId")
    private List<Marks> marksList;

    public SubjectMarkComponent() {
    }

    public SubjectMarkComponent(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getComponentPercent() {
        return componentPercent;
    }

    public void setComponentPercent(Integer componentPercent) {
        this.componentPercent = componentPercent;
    }

    public Integer getComponentMarkId() {
        return componentMarkId;
    }

    public void setComponentMarkId(Integer componentMarkId) {
        this.componentMarkId = componentMarkId;
    }

    public Subject getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Subject subjectId) {
        this.subjectId = subjectId;
    }

    public List<Marks> getMarksList() {
        return marksList;
    }

    public void setMarksList(List<Marks> marksList) {
        this.marksList = marksList;
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
        if (!(object instanceof SubjectMarkComponent)) {
            return false;
        }
        SubjectMarkComponent other = (SubjectMarkComponent) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.SubjectMarkComponent[ id=" + id + " ]";
    }

}

