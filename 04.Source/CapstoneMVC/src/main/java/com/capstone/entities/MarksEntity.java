/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Marks", catalog = "CapstoneProject", schema = "dbo")
public class MarksEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "AverageMark", precision = 53)
    private Double averageMark;
    @Column(name = "Status", length = 50)
    private String status;
    @JoinColumn(name = "CourseId", referencedColumnName = "Id")
    @ManyToOne
    private CourseEntity courseId;
    @JoinColumn(name = "SemesterId", referencedColumnName = "Id")
    @ManyToOne
    private RealSemesterEntity semesterId;
    @JoinColumn(name = "StudentId", referencedColumnName = "ID")
    @ManyToOne
    private StudentEntity studentId;
    @JoinColumn(name = "SubjectId", referencedColumnName = "SubjectId")
    @ManyToOne
    private SubjectMarkComponentEntity subjectId;

    public MarksEntity() {
    }

    public MarksEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAverageMark() {
        return averageMark;
    }

    public void setAverageMark(Double averageMark) {
        this.averageMark = averageMark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CourseEntity getCourseId() {
        return courseId;
    }

    public void setCourseId(CourseEntity courseId) {
        this.courseId = courseId;
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

    public SubjectMarkComponentEntity getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(SubjectMarkComponentEntity subjectId) {
        this.subjectId = subjectId;
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
        if (!(object instanceof MarksEntity)) {
            return false;
        }
        MarksEntity other = (MarksEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Marks[ id=" + id + " ]";
    }
    
}
