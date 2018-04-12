package com.capstone.models;

public class RequiredDocuments {
    //bằng tốt nghiệp phổ thông
    boolean highschoolGraduate;
    //chứng minh nhân dân
    boolean idCard;
    //giấy khai sinh
    boolean birthRecords;
   //hạn chót
    String dueDate;
    //đợt tốt nghiệp trong năm
    String graduateTime;

    public RequiredDocuments(boolean highschoolGraduate, boolean idCard, boolean birthRecords, String dueDate, String graduateTime) {
        this.highschoolGraduate = highschoolGraduate;
        this.idCard = idCard;
        this.birthRecords = birthRecords;
        this.dueDate = dueDate;
        this.graduateTime = graduateTime;
    }

    public RequiredDocuments() {
    }

    public boolean hasHighschoolGraduate() {
        return highschoolGraduate;
    }

    public void setHighschoolGraduate(boolean highschoolGraduate) {
        this.highschoolGraduate = highschoolGraduate;
    }

    public boolean hasIdCard() {
        return idCard;
    }

    public void setIdCard(boolean idCard) {
        this.idCard = idCard;
    }

    public boolean hasBirthRecords() {
        return birthRecords;
    }

    public void setBirthRecords(boolean birthRecords) {
        this.birthRecords = birthRecords;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getGraduateTime() {
        return graduateTime;
    }

    public void setGraduateTime(String graduateTime) {
        this.graduateTime = graduateTime;
    }
}
