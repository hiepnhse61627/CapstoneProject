package com.capstone.models;

public class StudentAndSubject{
    private String studentName;
    private String studySubject;

    public StudentAndSubject(String studentName, String studySubject) {
        this.studentName = studentName;
        this.studySubject = studySubject;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudySubject() {
        return studySubject;
    }

    public void setStudySubject(String studySubject) {
        this.studySubject = studySubject;
    }
}