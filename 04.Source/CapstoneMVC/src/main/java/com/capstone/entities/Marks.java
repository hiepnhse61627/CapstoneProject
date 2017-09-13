package com.capstone.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Marks", catalog = "CapstoneProject", schema = "dbo")
public class Marks implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id", nullable = false)
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "AverageMark", precision = 53)
    private Double averageMark;
    @Column(name = "Status", length = 50)
    private String status;
    @JoinColumn(name = "CourseId", referencedColumnName = "Id")
    @ManyToOne
    private Course courseId;
    @JoinColumn(name = "RollNumber", referencedColumnName = "RollNumber")
    @ManyToOne
    private Student rollNumber;
    @JoinColumn(name = "MarkComponentId", referencedColumnName = "ID")
    @ManyToOne
    private SubjectMarkComponent markComponentId;

    public Marks() {
    }

    public Marks(Integer id) {
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

    public Course getCourseId() {
        return courseId;
    }

    public void setCourseId(Course courseId) {
        this.courseId = courseId;
    }

    public Student getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(Student rollNumber) {
        this.rollNumber = rollNumber;
    }

    public SubjectMarkComponent getMarkComponentId() {
        return markComponentId;
    }

    public void setMarkComponentId(SubjectMarkComponent markComponentId) {
        this.markComponentId = markComponentId;
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
        if (!(object instanceof Marks)) {
            return false;
        }
        Marks other = (Marks) object;
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

