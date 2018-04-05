package com.capstone.entities.fapEntities;

import com.capstone.models.StudentAndMark;
import sun.misc.FloatingDecimal;

import javax.persistence.*;

@SqlResultSetMapping(name = "FapAvgMarkResult", classes = {
        @ConstructorResult(targetClass = StudentAndMark.class,
                columns = {@ColumnResult(name = "RollNumber"),
                        @ColumnResult(name = "AverageMark"),
                        @ColumnResult(name = "IsPassed"),
                        @ColumnResult(name = "SubjectCode"),
                        @ColumnResult(name = "SemesterName")
                })
})
public class StudentAvgMarks {

    private String rollNumber;
    private Double avgMark;
    private boolean isPassed;
    private String subjectCode;
    private String semesterName;

    public StudentAvgMarks(String rollNumber, Double avgMark, Boolean isPassed, String subjectCode, String semesterName) {
        this.rollNumber = rollNumber;
        this.avgMark = avgMark;
        this.isPassed = isPassed;
        this.subjectCode = subjectCode;
        this.semesterName = semesterName;
    }

    public StudentAvgMarks() {
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public Double getAvgMark() {
        return avgMark;
    }

    public void setAvgMark(Double avgMark) {
        this.avgMark = avgMark;
    }

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean passed) {
        isPassed = passed;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }
}
