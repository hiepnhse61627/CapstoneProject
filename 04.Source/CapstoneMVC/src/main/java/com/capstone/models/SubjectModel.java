package com.capstone.models;

public class SubjectModel {
    private String subjectID;
    private String subjectName;
    private int credits;
    private String prerequisiteSubject;
    private String effectionSemester;
    private String replacementSubject;
    private Integer failMark;
    private boolean result;
    private String errorMessage;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    private int number;

    public SubjectModel() {
    }

    public SubjectModel(String subjectID, String subjectName) {
        this.subjectID = subjectID;
        this.subjectName = subjectName;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
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

    public String getPrerequisiteSubject() {
        return prerequisiteSubject;
    }

    public void setPrerequisiteSubject(String prerequisiteSubject) {
        this.prerequisiteSubject = prerequisiteSubject;
    }

    public String getEffectionSemester() {
        return effectionSemester;
    }

    public void setEffectionSemester(String effectionSemester) {
        this.effectionSemester = effectionSemester;
    }

    public String getReplacementSubject() {
        return replacementSubject;
    }

    public void setReplacementSubject(String replacementSubject) {
        this.replacementSubject = replacementSubject;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getFailMark() {
        return failMark;
    }

    public void setFailMark(Integer failMark) {
        this.failMark = failMark;
    }
}
