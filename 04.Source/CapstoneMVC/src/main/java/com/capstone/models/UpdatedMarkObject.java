package com.capstone.models;

public class UpdatedMarkObject {
    private String rollNumber;
    private String subjectCode;
    private String oldSemester;
    private String newSemester;
    private Double oldMark;
    private Double newMark;
    private String oldStatus;
    private String newStatus;


    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getOldSemester() {
        return oldSemester;
    }

    public void setOldSemester(String oldSemester) {
        this.oldSemester = oldSemester;
    }

    public String getNewSemester() {
        return newSemester;
    }

    public void setNewSemester(String newSemester) {
        this.newSemester = newSemester;
    }

    public Double getOldMark() {
        return oldMark;
    }

    public void setOldMark(Double oldMark) {
        this.oldMark = oldMark;
    }

    public Double getNewMark() {
        return newMark;
    }

    public void setNewMark(Double newMark) {
        this.newMark = newMark;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
}
