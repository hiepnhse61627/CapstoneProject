package com.capstone.entities;

import javax.persistence.*;

@Entity
@Table(name = "Student", schema = "dbo", catalog = "CapstoneProject")
public class StudentEntity {
    private Integer id;
    private String rollNumber;
    private String fullName;
    private MarksEntity marksByRollNumber;

    @Id
    @Column(name = "ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "RollNumber")
    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    @Basic
    @Column(name = "FullName")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StudentEntity that = (StudentEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (rollNumber != null ? !rollNumber.equals(that.rollNumber) : that.rollNumber != null) return false;
        if (fullName != null ? !fullName.equals(that.fullName) : that.fullName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (rollNumber != null ? rollNumber.hashCode() : 0);
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "RollNumber", referencedColumnName = "RollNumber", nullable = false)
    public MarksEntity getMarksByRollNumber() {
        return marksByRollNumber;
    }

    public void setMarksByRollNumber(MarksEntity marksByRollNumber) {
        this.marksByRollNumber = marksByRollNumber;
    }
}
