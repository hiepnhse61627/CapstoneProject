package com.capstone.models;

import java.sql.Timestamp;
import java.util.Date;

public class MarkModel {
    private int markId;
    private String subject;
    private String subjectName;
    private String semester;
    private String class1;
    private String status;
    private Date startDate;
    private Date endDate;
    private boolean active;
    private double averageMark;
    private int repeatingNumber;
    private int credits;

    public MarkModel() {
    }

    public MarkModel(int markId, String semester, boolean active, double averageMark) {
        this.markId = markId;
        this.semester = semester;
        this.active = active;
        this.averageMark = averageMark;
    }

    public int getMarkId() {
        return markId;
    }

    public void setMarkId(int markId) {
        this.markId = markId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

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
