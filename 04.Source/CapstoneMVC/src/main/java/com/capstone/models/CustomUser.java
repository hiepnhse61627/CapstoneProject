package com.capstone.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUser extends User {

    private String fullname;
    private String picture;

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String fullname, String picture) {
        super(username, password, authorities);
        this.fullname = fullname;
        this.picture = picture;
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
