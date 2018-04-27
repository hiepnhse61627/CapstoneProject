package com.capstone.entities.fapEntities;

public class StudentStudyingMarks {
    String rollNumber ;
    String semesterName;
    String subjectCode;

    public StudentStudyingMarks(String rollNumber, String semesterName, String subjectCode) {
        this.rollNumber = rollNumber;
        this.semesterName = semesterName;
        this.subjectCode = subjectCode;
    }

    public StudentStudyingMarks() {
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }
}
