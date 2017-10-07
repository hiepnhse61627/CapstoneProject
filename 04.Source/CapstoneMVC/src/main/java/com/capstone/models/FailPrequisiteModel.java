package com.capstone.models;

import com.capstone.entities.MarksEntity;

public class FailPrequisiteModel {
    private MarksEntity mark;
    private String subjectWhichPrequisiteFail;

    public FailPrequisiteModel() {
    }

    public FailPrequisiteModel(MarksEntity mark, String subjectWhichPrequisiteFail) {
        this.mark = mark;
        this.subjectWhichPrequisiteFail = subjectWhichPrequisiteFail;
    }

    public MarksEntity getMark() {
        return mark;
    }

    public void setMark(MarksEntity mark) {
        this.mark = mark;
    }

    public String getSubjectWhichPrequisiteFail() {
        return subjectWhichPrequisiteFail;
    }

    public void setSubjectWhichPrequisiteFail(String subjectWhichPrequisiteFail) {
        this.subjectWhichPrequisiteFail = subjectWhichPrequisiteFail;
    }
}
