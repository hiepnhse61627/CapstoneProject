package com.capstone.entities;

import javax.persistence.*;

@Entity
@Table(name = "Subject", schema = "dbo", catalog = "CapstoneProject")
public class SubjectEntity {
    private String id;
    private String name;
    private String abbreviation;
    private String prequisiteId;
    private Integer credits;
    private SubjectMarkComponentEntity subjectMarkComponentById;

    @Id
    @Column(name = "Id")
    public String getId() { return id; }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "Abbreviation")
    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Basic
    @Column(name = "PrequisiteId")
    public String getPrequisiteId() {
        return prequisiteId;
    }

    public void setPrequisiteId(String prequisiteId) {
        this.prequisiteId = prequisiteId;
    }

    @Basic
    @Column(name = "Credits")
    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubjectEntity that = (SubjectEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (abbreviation != null ? !abbreviation.equals(that.abbreviation) : that.abbreviation != null) return false;
        if (prequisiteId != null ? !prequisiteId.equals(that.prequisiteId) : that.prequisiteId != null) return false;
        if (credits != null ? !credits.equals(that.credits) : that.credits != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (abbreviation != null ? abbreviation.hashCode() : 0);
        result = 31 * result + (prequisiteId != null ? prequisiteId.hashCode() : 0);
        result = 31 * result + (credits != null ? credits.hashCode() : 0);
        return result;
    }

    @OneToOne
    @JoinColumn(name = "Id", referencedColumnName = "SubjectId", nullable = false)
    public SubjectMarkComponentEntity getSubjectMarkComponentById() {
        return subjectMarkComponentById;
    }

    public void setSubjectMarkComponentById(SubjectMarkComponentEntity subjectMarkComponentById) {
        this.subjectMarkComponentById = subjectMarkComponentById;
    }
}
