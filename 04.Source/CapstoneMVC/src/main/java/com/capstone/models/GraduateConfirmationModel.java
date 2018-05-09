package com.capstone.models;

public class GraduateConfirmationModel {
    private String studentName;
    private String rollNumber;
    private String birthDate;
    private String program;
    private String form;
    private String graduateYear;
    private String diplomaCode;
    private String certificateCode;
    private String decisionNumber;

    public GraduateConfirmationModel(String studentName, String rollNumber, String birthDate,
                                     String program, String form, String graduateYear,
                                     String diplomaCode, String certificateCode, String decisionNumber) {
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.birthDate = birthDate;
        this.program = program;
        this.form = form;
        this.graduateYear = graduateYear;
        this.diplomaCode = diplomaCode;
        this.certificateCode = certificateCode;
        this.decisionNumber = decisionNumber;
    }

    public GraduateConfirmationModel() {
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getGraduateYear() {
        return graduateYear;
    }

    public void setGraduateYear(String graduateYear) {
        this.graduateYear = graduateYear;
    }

    public String getDiplomaCode() {
        return diplomaCode;
    }

    public void setDiplomaCode(String diplomaCode) {
        this.diplomaCode = diplomaCode;
    }

    public String getCertificateCode() {
        return certificateCode;
    }

    public void setCertificateCode(String certificateCode) {
        this.certificateCode = certificateCode;
    }

    public String getDecisionNumber() {
        return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
        this.decisionNumber = decisionNumber;
    }
}
