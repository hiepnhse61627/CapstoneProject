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
import javax.xml.bind.annotation.*;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Marks", catalog = "CapstoneProject", schema = "dbo")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mark", propOrder = {"averageMark", "status", "subjectMarkComponentId"})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MarksEntity.findAll", query = "SELECT m FROM MarksEntity m")})
public class MarksEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "AverageMark", precision = 53)
    @XmlElement(required = true)
    private Double averageMark;
    @Column(name = "Status", length = 50)
    @XmlElement(required = true)
    private String status;
    @Column(name = "IsActivated")
    @XmlTransient
    private Boolean isActivated;
    @Column(name = "IsEnabled")
    @XmlTransient
    private Boolean isEnabled;
    @JoinColumn(name = "CourseId", referencedColumnName = "Id")
    @ManyToOne
    @XmlTransient
    private CourseEntity courseId;
    @JoinColumn(name = "SemesterId", referencedColumnName = "Id")
    @ManyToOne
    @XmlTransient
    private RealSemesterEntity semesterId;
    @JoinColumn(name = "StudentId", referencedColumnName = "Id")
    @ManyToOne
    @XmlTransient
    private StudentEntity studentId;
    @JoinColumn(name = "SubjectMarkComponentId", referencedColumnName = "Id")
    @ManyToOne
    @XmlElement(required = true)
    private SubjectMarkComponentEntity subjectMarkComponentId;

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

    public Boolean getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
        this.isActivated = isActivated;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
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

    public SubjectMarkComponentEntity getSubjectMarkComponentId() {
        return subjectMarkComponentId;
    }

    public void setSubjectMarkComponentId(SubjectMarkComponentEntity subjectMarkComponentId) {
        this.subjectMarkComponentId = subjectMarkComponentId;
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
        return "com.capstone.entities.MarksEntity[ id=" + id + " ]";
    }
    
}
