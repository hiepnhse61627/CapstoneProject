package com.capstone.models;

public class SubjectModel {
    private String subjectID;
    private String subjectName;
    private int credits;
    private String prerequisiteSubject;
    private String prerequisiteEffectStart;
    private String prerequisiteEffectEnd;
    private String replacementSubject;
    private boolean result;
    private String errorMessage;

    public SubjectModel() {
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

    public String getPrerequisiteEffectStart() {
        return prerequisiteEffectStart;
    }

    public void setPrerequisiteEffectStart(String prerequisiteEffectStart) {
        this.prerequisiteEffectStart = prerequisiteEffectStart;
    }

    public String getPrerequisiteEffectEnd() {
        return prerequisiteEffectEnd;
    }

    public void setPrerequisiteEffectEnd(String prerequisiteEffectEnd) {
        this.prerequisiteEffectEnd = prerequisiteEffectEnd;
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
}
