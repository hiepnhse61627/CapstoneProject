package com.capstone.models;

public class MobileUserModel {
    private int id;
    private String code;
    private String name;
    private String position;
    private String emailEDU;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmailEDU() {
        return emailEDU;
    }

    public void setEmailEDU(String emailEDU) {
        this.emailEDU = emailEDU;
    }
}
