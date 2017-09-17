package com.capstone.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "Student", schema = "dbo", catalog = "CapstoneProject")
public class StudentEntity {
    private int id;
    private String rollNumber;
    private String fullName;
    private Collection<DocumentStudentEntity> documentStudentsById;
    private Collection<MarksEntity> marksById;

    @Id
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

        if (id != that.id) return false;
        if (rollNumber != null ? !rollNumber.equals(that.rollNumber) : that.rollNumber != null) return false;
        if (fullName != null ? !fullName.equals(that.fullName) : that.fullName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (rollNumber != null ? rollNumber.hashCode() : 0);
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "studentByStudentId")
    public Collection<DocumentStudentEntity> getDocumentStudentsById() {
        return documentStudentsById;
    }

    public void setDocumentStudentsById(Collection<DocumentStudentEntity> documentStudentsById) {
        this.documentStudentsById = documentStudentsById;
    }

    @OneToMany(mappedBy = "studentByStudentId")
    public Collection<MarksEntity> getMarksById() {
        return marksById;
    }

    public void setMarksById(Collection<MarksEntity> marksById) {
        this.marksById = marksById;
    }
}
