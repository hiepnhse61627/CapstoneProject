package com.capstone.models;

import com.capstone.entities.StudentEntity;
import com.capstone.entities.SubjectEntity;

public class ImportedMarkObject {

    private StudentEntity studentEntity;
    private String semesterName;
    private SubjectEntity subjectCode;
    private Double averageMark;
    private String status;

    public StudentEntity getStudentEntity() {
        return studentEntity;
    }

    public void setStudentEntity(StudentEntity studentEntity) {
        this.studentEntity = studentEntity;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public SubjectEntity getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(SubjectEntity subjectCode) {
        this.subjectCode = subjectCode;
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
}
