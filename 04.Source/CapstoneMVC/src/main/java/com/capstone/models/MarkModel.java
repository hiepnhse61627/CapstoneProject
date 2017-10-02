package com.capstone.models;

import java.sql.Timestamp;
import java.util.Date;

public class MarkModel {
    private String subject;
    private String semester;
    private String class1;
    private String status;
    private Date startDate;
    private Date endDate;
    private double averageMark;
    private int repeatingNumber;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getClass1() {
        return class1;
    }

    public void setClass1(String class1) {
        this.class1 = class1;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAverageMark() {
        return averageMark;
    }

    public void setAverageMark(double averageMark) {
        this.averageMark = averageMark;
    }

    public int getRepeatingNumber() {
        return repeatingNumber;
    }

    public void setRepeatingNumber(int repeatingNumber) {
        this.repeatingNumber = repeatingNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
