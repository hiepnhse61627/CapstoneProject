package com.capstone.models;

public class StudentFailedSubject {
    private String StudentName;
    private String StudentCode;
    private String SubjectFailed;
    private String SubjectRelearned;
    private int failedCredit;

    public StudentFailedSubject() {
    }

    public StudentFailedSubject(String studentName, String studentCode, String subjectFailed, String subjectRelearned, int failedCredit) {
        StudentName = studentName;
        StudentCode = studentCode;
        SubjectFailed = subjectFailed;
        SubjectRelearned = subjectRelearned;
        this.failedCredit = failedCredit;
    }

    public int getFailedCredit() {
        return failedCredit;
    }

    public void setFailedCredit(int failedCredit) {
        this.failedCredit = failedCredit;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getStudentCode() {
        return StudentCode;
    }

    public void setStudentCode(String studentCode) {
        StudentCode = studentCode;
    }

    public String getSubjectFailed() {
        return SubjectFailed;
    }

    public void setSubjectFailed(String subjectFailed) {
        SubjectFailed = subjectFailed;
    }

    public String getSubjectRelearned() {
        return SubjectRelearned;
    }

    public void setSubjectRelearned(String subjectRelearned) {
        SubjectRelearned = subjectRelearned;
    }
}
