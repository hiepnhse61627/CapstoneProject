package com.capstone.models;

public class ProgramModel {
    private int id;
    private String name;
    private String fullName;
    private int ojt;
    private int capstone;
    private int graduate;
    private boolean result;
    private String errorMessage;

    public ProgramModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getOjt() {
        return ojt;
    }

    public void setOjt(int ojt) {
        this.ojt = ojt;
    }

    public int getCapstone() {
        return capstone;
    }

    public void setCapstone(int capstone) {
        this.capstone = capstone;
    }

    public int getGraduate() {
        return graduate;
    }

    public void setGraduate(int graduate) {
        this.graduate = graduate;
    }
}
