package com.capstone.models;

import com.capstone.entities.CredentialsEntity;

public class CustomCredentialsEntity extends CredentialsEntity {
    private String newPassword;

    public CustomCredentialsEntity() {
    }

    public CustomCredentialsEntity(Integer id) {
        super(id);
    }

    public CustomCredentialsEntity(Integer id, String username, String password) {
        super(id, username, password);
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
