package com.capstone.models;

import com.capstone.entities.MarksEntity;
import org.apache.commons.lang.builder.CompareToBuilder;

public class FailPrequisiteModel {
    private MarksEntity mark;
    private String subjectWhichPrequisiteFail;
    private String semester;

    public FailPrequisiteModel() {
    }

    public FailPrequisiteModel(MarksEntity mark, String subjectWhichPrequisiteFail, String semester) {
        this.mark = mark;
        this.subjectWhichPrequisiteFail = subjectWhichPrequisiteFail;
        this.semester = semester;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
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

    @Override
    public boolean equals(Object obj) {
        FailPrequisiteModel f = (FailPrequisiteModel) obj;
        if (this.mark.getId() == f.getMark().getId() && this.subjectWhichPrequisiteFail.equals(f.getSubjectWhichPrequisiteFail())) {
            return true;
        }

        return false;
    }
}
