package com.capstone.entities;

import javax.persistence.*;

@Entity
@Table(name = "Subject", schema = "dbo", catalog = "CapstoneProject")
public class SubjectEntity {
    private String subjectId;
    private String subjectName;
    private SubjectMarkComponentEntity subjectMarkComponentBySubjectId;

    @Id
    @Column(name = "SubjectId")
    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    @Basic
    @Column(name = "SubjectName")
    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubjectEntity that = (SubjectEntity) o;

        if (subjectId != null ? !subjectId.equals(that.subjectId) : that.subjectId != null) return false;
        if (subjectName != null ? !subjectName.equals(that.subjectName) : that.subjectName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = subjectId != null ? subjectId.hashCode() : 0;
        result = 31 * result + (subjectName != null ? subjectName.hashCode() : 0);
        return result;
    }

    @OneToOne
    @JoinColumn(name = "SubjectId", referencedColumnName = "SubjectId", nullable = false)
    public SubjectMarkComponentEntity getSubjectMarkComponentBySubjectId() {
        return subjectMarkComponentBySubjectId;
    }

    public void setSubjectMarkComponentBySubjectId(SubjectMarkComponentEntity subjectMarkComponentBySubjectId) {
        this.subjectMarkComponentBySubjectId = subjectMarkComponentBySubjectId;
    }
}
