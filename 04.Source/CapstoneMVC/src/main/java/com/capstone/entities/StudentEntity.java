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
import javax.xml.bind.annotation.*;

/**
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Student", catalog = "CapstoneProject", schema = "dbo", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"RollNumber"})})

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "student", propOrder = {"rollNumber", "fullName", "dateOfBirth", "gender", "programId"})
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "StudentEntity.findAll", query = "SELECT s FROM StudentEntity s")})
public class StudentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    private Integer id;
    @Basic(optional = false)
    @Column(name = "RollNumber", nullable = false, length = 50)
    @XmlElement(required = true)
    private String rollNumber;
    @Column(name = "FullName", length = 150)
    @XmlElement(required = true)
    private String fullName;
    @Column(name = "Email", length = 255)
    @XmlTransient
    private String email;
    @Column(name = "DateOfBirth")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlElement(required = true)
    private Date dateOfBirth;
    @Column(name = "Gender")
    @XmlElement(required = true)
    private Boolean gender;
    @Column(name = "Term")
    @XmlTransient
    private Integer term;
    @Column(name = "Shift", length = 5)
    @XmlTransient
    private String shift;
    @Column(name = "PayRollClass", length = 50)
    @XmlTransient
    private String payRollClass;
    @Column(name = "PassFailCredits")
    @XmlTransient
    private Integer passFailCredits;
    @Column(name = "PassCredits")
    @XmlTransient
    private Integer passCredits;
    @Column(name = "PassFailAverageMark", precision = 53)
    @XmlTransient
    private Double passFailAverageMark;
    @OneToMany(mappedBy = "studentId")
    @XmlTransient
    private List<DocumentStudentEntity> documentStudentEntityList;
    @OneToMany(mappedBy = "studentId")
    @XmlTransient
    private List<OldRollNumberEntity> oldRollNumberEntityList;
    @OneToMany(mappedBy = "studentId")
    @XmlTransient
    private List<MarksEntity> marksEntityList;
    @JoinColumn(name = "ProgramId", referencedColumnName = "Id")
    @ManyToOne
    @XmlElement(required = true)
    private ProgramEntity programId;
    @OneToMany(mappedBy = "studentId")
    @XmlTransient
    private List<StudentStatusEntity> studentStatusEntityList;
    @OneToMany(mappedBy = "studentId")
    @XmlTransient
    private List<CourseStudentEntity> courseStudentEntityList;
    @OneToMany(mappedBy = "studentId")
    @XmlTransient
    private List<SimulateDocumentStudentEntity> simulateDocumentStudentEntityList;

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

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getPayRollClass() {
        return payRollClass;
    }

    public void setPayRollClass(String payRollClass) {
        this.payRollClass = payRollClass;
    }

    public Integer getPassFailCredits() {
        return passFailCredits;
    }

    public void setPassFailCredits(Integer passFailCredits) {
        this.passFailCredits = passFailCredits;
    }

    public Integer getPassCredits() {
        return passCredits;
    }

    public void setPassCredits(Integer passCredits) {
        this.passCredits = passCredits;
    }

    public Double getPassFailAverageMark() {
        return passFailAverageMark;
    }

    public void setPassFailAverageMark(Double passFailAverageMark) {
        this.passFailAverageMark = passFailAverageMark;
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

    public List<StudentStatusEntity> getStudentStatusEntityList() {
        return studentStatusEntityList;
    }

    public void setStudentStatusEntityList(List<StudentStatusEntity> studentStatusEntityList) {
        this.studentStatusEntityList = studentStatusEntityList;
    }

    public List<CourseStudentEntity> getCourseStudentEntityList() {
        return courseStudentEntityList;
    }

    public void setCourseStudentEntityList(List<CourseStudentEntity> courseStudentEntityList) {
        this.courseStudentEntityList = courseStudentEntityList;
    }

    public List<SimulateDocumentStudentEntity> getSimulateDocumentStudentEntityList() {
        return simulateDocumentStudentEntityList;
    }

    public void setSimulateDocumentStudentEntityList(List<SimulateDocumentStudentEntity> simulateDocumentStudentEntityList) {
        this.simulateDocumentStudentEntityList = simulateDocumentStudentEntityList;
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
