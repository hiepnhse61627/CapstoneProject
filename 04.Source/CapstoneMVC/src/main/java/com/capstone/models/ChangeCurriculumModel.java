package com.capstone.models;

import java.util.List;

public class ChangeCurriculumModel {
    private String subjectCode;
    private List<MarkModel> data;

    public ChangeCurriculumModel() {
    }

    public ChangeCurriculumModel(String subjectCode, List<MarkModel> data) {
        this.subjectCode = subjectCode;
        this.data = data;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public List<MarkModel> getData() {
        return data;
    }

    public void setData(List<MarkModel> data) {
        this.data = data;
    }
}
