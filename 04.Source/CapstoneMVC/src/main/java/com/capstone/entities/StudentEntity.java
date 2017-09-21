/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Student", catalog = "CapstoneProject", schema = "dbo", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"RollNumber"})})
public class StudentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "RollNumber", nullable = false, length = 50)
    private String rollNumber;
    @Column(name = "FullName", length = 150)
    private String fullName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studentId")
    private List<DocumentStudentEntity> documentStudentList;
    @OneToMany(mappedBy = "studentId")
    private List<MarksEntity> marksList;

    public StudentEntity() {
    }

    public StudentEntity(Integer id) {
        this.id = id;
    }

    public StudentEntity(Integer id, String rollNumber) {
        this.id = id;
        this.rollNumber = rollNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<DocumentStudentEntity> getDocumentStudentList() {
        return documentStudentList;
    }

    public void setDocumentStudentList(List<DocumentStudentEntity> documentStudentList) {
        this.documentStudentList = documentStudentList;
    }

    public List<MarksEntity> getMarksList() {
        return marksList;
    }

    public void setMarksList(List<MarksEntity> marksList) {
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
        if (!(object instanceof StudentEntity)) {
            return false;
        }
        StudentEntity other = (StudentEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Student[ id=" + id + " ]";
    }
    
}
