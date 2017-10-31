package com.capstone.models;

import com.capstone.entities.CredentialsEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUser extends User {
    private CredentialsEntity user;

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities, CredentialsEntity user) {
        super(username, password, authorities);
        this.user = user;
    }

    public CredentialsEntity getUser() {
        return user;
    }

    public void setUser(CredentialsEntity user) {
        this.user = user;
    }
}
