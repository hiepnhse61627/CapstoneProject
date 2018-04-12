package com.capstone.models;

public class MarkModelExcel {
    private double average;
    private String semesterName;
    private String subjectId;
    private String status;

    public MarkModelExcel(double average, String semesterName, String subjectId, String status) {
        this.average = average;
        this.semesterName = semesterName;
        this.subjectId = subjectId;
        this.status = status;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
