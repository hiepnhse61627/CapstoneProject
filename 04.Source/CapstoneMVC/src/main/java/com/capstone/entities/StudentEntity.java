/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Student", catalog = "CapstoneProject", schema = "dbo", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"RollNumber"})})
@NamedQueries({
    @NamedQuery(name = "StudentEntity.findAll", query = "SELECT s FROM StudentEntity s")})
public class StudentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "RollNumber", nullable = false, length = 50)
    private String rollNumber;
    @Column(name = "FullName", length = 150)
    private String fullName;
    @Column(name = "Email", length = 255)
    private String email;
    @Column(name = "DateOfBirth")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfBirth;
    @Column(name = "Gender")
    private Boolean gender;
    @Column(name = "Term")
    private Integer term;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studentId")
    private List<DocumentStudentEntity> documentStudentEntityList;
    @OneToMany(mappedBy = "studentId")
    private List<OldRollNumberEntity> oldRollNumberEntityList;
    @OneToMany(mappedBy = "studentId")
    private List<MarksEntity> marksEntityList;
    @JoinColumn(name = "ProgramId", referencedColumnName = "Id")
    @ManyToOne
    private ProgramEntity programId;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public List<DocumentStudentEntity> getDocumentStudentEntityList() {
        return documentStudentEntityList;
    }

    public void setDocumentStudentEntityList(List<DocumentStudentEntity> documentStudentEntityList) {
        this.documentStudentEntityList = documentStudentEntityList;
    }

    public List<OldRollNumberEntity> getOldRollNumberEntityList() {
        return oldRollNumberEntityList;
    }

    public void setOldRollNumberEntityList(List<OldRollNumberEntity> oldRollNumberEntityList) {
        this.oldRollNumberEntityList = oldRollNumberEntityList;
    }

    public List<MarksEntity> getMarksEntityList() {
        return marksEntityList;
    }

    public void setMarksEntityList(List<MarksEntity> marksEntityList) {
        this.marksEntityList = marksEntityList;
    }

    public ProgramEntity getProgramId() {
        return programId;
    }

    public void setProgramId(ProgramEntity programId) {
        this.programId = programId;
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
        return "com.capstone.entities.StudentEntity[ id=" + id + " ]";
    }
    
}
