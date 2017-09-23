package com.capstone.models;

import java.util.List;

public class StudentMarkModel {
    private int studentId;
    private String studentName;
    private String rollNumber;
    private List<MarkModel> markList;

    public StudentMarkModel() {
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public List<MarkModel> getMarkList() {
        return markList;
    }

    public void setMarkList(List<MarkModel> markList) {
        this.markList = markList;
    }
}

