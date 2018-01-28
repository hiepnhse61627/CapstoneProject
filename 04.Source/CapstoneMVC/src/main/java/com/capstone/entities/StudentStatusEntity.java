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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Student_Status", catalog = "CapstoneProject", schema = "dbo")
@NamedQueries({
        @NamedQuery(name = "StudentStatusEntity.findAll", query = "SELECT s FROM StudentStatusEntity s")})
public class StudentStatusEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Status", length = 50)
    private String status;
    @Column(name = "Term")
    private String term;
    @JoinColumn(name = "SemesterId", referencedColumnName = "Id")
    @ManyToOne
    private RealSemesterEntity semesterId;
    @JoinColumn(name = "StudentId", referencedColumnName = "Id")
    @ManyToOne
    private StudentEntity studentId;

    public StudentStatusEntity() {
    }

    public StudentStatusEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public RealSemesterEntity getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(RealSemesterEntity semesterId) {
        this.semesterId = semesterId;
    }

    public StudentEntity getStudentId() {
        return studentId;
    }

    public void setStudentId(StudentEntity studentId) {
        this.studentId = studentId;
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
        if (!(object instanceof StudentStatusEntity)) {
            return false;
        }
        StudentStatusEntity other = (StudentStatusEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.capstone.entities.StudentStatusEntity[ id=" + id + " ]";
    }

}
