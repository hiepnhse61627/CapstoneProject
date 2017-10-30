package com.capstone.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUser extends User {

    private String fullname;
    private String picture;
    private String rollNumber;

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String fullname, String picture, String rollNumber) {
        super(username, password, authorities);
        this.fullname = fullname;
        this.picture = picture;
        this.rollNumber = rollNumber;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
