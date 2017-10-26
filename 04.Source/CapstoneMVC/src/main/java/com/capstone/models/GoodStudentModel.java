package com.capstone.models;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.SubjectCurriculumEntity;

import java.util.ArrayList;
import java.util.List;

public class GoodStudentModel {
    private int studentId;
    private String rollNumber;
    private String fullName;
    private int curriculumId;
    private int currentTerm;
    private List<GoodStudentMarkModel> markList;
    private List<SubjectCurriculumEntity> subjectCurriculumList;

    public GoodStudentModel() {
        markList = new ArrayList<>();
        subjectCurriculumList = new ArrayList<>();
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(int curriculumId) {
        this.curriculumId = curriculumId;
    }

    public int getCurrentTerm() {
        return currentTerm;
    }

    public void setCurrentTerm(int currentTerm) {
        this.currentTerm = currentTerm;
    }

    public List<GoodStudentMarkModel> getMarkList() {
        return markList;
    }

    public void setMarkList(List<GoodStudentMarkModel> markList) {
        this.markList = markList;
    }

    public List<SubjectCurriculumEntity> getSubjectCurriculumList() {
        return subjectCurriculumList;
    }

    public void setSubjectCurriculumList(List<SubjectCurriculumEntity> subjectCurriculumList) {
        this.subjectCurriculumList = subjectCurriculumList;
    }
}
