package com.capstone.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "Subject_MarkComponent", schema = "dbo", catalog = "CapstoneProject")
public class SubjectMarkComponentEntity {
    private String subjectId;
    private Integer componentPercent;
    private Collection<MarksEntity> marksBySubjectId;
    private SubjectEntity subjectBySubjectId;

    @Id
    @Column(name = "SubjectId")
    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    @Basic
    @Column(name = "ComponentPercent")
    public Integer getComponentPercent() {
        return componentPercent;
    }

    public void setComponentPercent(Integer componentPercent) {
        this.componentPercent = componentPercent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubjectMarkComponentEntity that = (SubjectMarkComponentEntity) o;

        if (subjectId != null ? !subjectId.equals(that.subjectId) : that.subjectId != null) return false;
        if (componentPercent != null ? !componentPercent.equals(that.componentPercent) : that.componentPercent != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = subjectId != null ? subjectId.hashCode() : 0;
        result = 31 * result + (componentPercent != null ? componentPercent.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "subjectMarkComponentBySubjectId")
    public Collection<MarksEntity> getMarksBySubjectId() {
        return marksBySubjectId;
    }

    public void setMarksBySubjectId(Collection<MarksEntity> marksBySubjectId) {
        this.marksBySubjectId = marksBySubjectId;
    }

    @OneToOne(mappedBy = "subjectMarkComponentBySubjectId")
    public SubjectEntity getSubjectBySubjectId() {
        return subjectBySubjectId;
    }

    public void setSubjectBySubjectId(SubjectEntity subjectBySubjectId) {
        this.subjectBySubjectId = subjectBySubjectId;
    }
}
