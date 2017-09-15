package com.capstone.entities;

import javax.persistence.*;

@Entity
@Table(name = "Marks", schema = "dbo", catalog = "CapstoneProject")
public class MarksEntity {
    private int id;
    private String subjectId;
    private String rollNumber;
    private Integer semesterId;
    private Integer courseId;
    private Double averageMark;
    private String status;

    @Id
    @Column(name = "Id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "SubjectId")
    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
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
    @Column(name = "SemesterId")
    public Integer getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(Integer semesterId) {
        this.semesterId = semesterId;
    }

    @Basic
    @Column(name = "CourseId")
    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    @Basic
    @Column(name = "AverageMark")
    public Double getAverageMark() {
        return averageMark;
    }

    public void setAverageMark(Double averageMark) {
        this.averageMark = averageMark;
    }

    @Basic
    @Column(name = "Status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MarksEntity that = (MarksEntity) o;

        if (id != that.id) return false;
        if (subjectId != null ? !subjectId.equals(that.subjectId) : that.subjectId != null) return false;
        if (rollNumber != null ? !rollNumber.equals(that.rollNumber) : that.rollNumber != null) return false;
        if (semesterId != null ? !semesterId.equals(that.semesterId) : that.semesterId != null) return false;
        if (courseId != null ? !courseId.equals(that.courseId) : that.courseId != null) return false;
        if (averageMark != null ? !averageMark.equals(that.averageMark) : that.averageMark != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (subjectId != null ? subjectId.hashCode() : 0);
        result = 31 * result + (rollNumber != null ? rollNumber.hashCode() : 0);
        result = 31 * result + (semesterId != null ? semesterId.hashCode() : 0);
        result = 31 * result + (courseId != null ? courseId.hashCode() : 0);
        result = 31 * result + (averageMark != null ? averageMark.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
