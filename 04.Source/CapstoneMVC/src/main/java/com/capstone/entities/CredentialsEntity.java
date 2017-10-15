/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Rem
 */
@Entity
@Table(name = "Credentials")
@NamedQueries({
    @NamedQuery(name = "CredentialsEntity.findAll", query = "SELECT c FROM CredentialsEntity c")
    , @NamedQuery(name = "CredentialsEntity.findById", query = "SELECT c FROM CredentialsEntity c WHERE c.id = :id")
    , @NamedQuery(name = "CredentialsEntity.findByUsername", query = "SELECT c FROM CredentialsEntity c WHERE c.username = :username")
    , @NamedQuery(name = "CredentialsEntity.findByPassword", query = "SELECT c FROM CredentialsEntity c WHERE c.password = :password")
    , @NamedQuery(name = "CredentialsEntity.findByRole", query = "SELECT c FROM CredentialsEntity c WHERE c.role = :role")})
public class CredentialsEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Username")
    private String username;
    @Basic(optional = false)
    @Column(name = "Password")
    private String password;
    @Column(name = "Role")
    private String role;
    @Column(name = "Email")
    private String email;

    public CredentialsEntity() {
    }

    public CredentialsEntity(Integer id) {
        this.id = id;
    }

    public CredentialsEntity(Integer id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CredentialsEntity)) {
            return false;
        }
        CredentialsEntity other = (CredentialsEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.capstone.entities.CredentialsEntity[ id=" + id + " ]";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
