package com.capstone.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author StormNs
 */
@Entity
@Table(name = "Credentials_Roles")
@NamedQueries({
        @NamedQuery(name = "CredentialsRolesEntity.findAll", query = "SELECT c FROM CredentialsRolesEntity c")
        , @NamedQuery(name = "CredentialsRolesEntity.findById", query = "SELECT c FROM CredentialsRolesEntity c WHERE c.id = :id")})
public class CredentialsRolesEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn(name = "CredentialsId", referencedColumnName = "Id")
    @ManyToOne
    private CredentialsEntity credentialsId;
    @JoinColumn(name = "RolesId", referencedColumnName = "Id")
    @ManyToOne
    private RolesEntity rolesId;

    public CredentialsRolesEntity() {
    }

    public CredentialsRolesEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CredentialsEntity getCredentialsId() {
        return credentialsId;
    }

    public void setCredentialsId(CredentialsEntity credentialsId) {
        this.credentialsId = credentialsId;
    }

    public RolesEntity getRolesId() {
        return rolesId;
    }

    public void setRolesId(RolesEntity rolesId) {
        this.rolesId = rolesId;
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
        if (!(object instanceof CredentialsRolesEntity)) {
            return false;
        }
        CredentialsRolesEntity other = (CredentialsRolesEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.CredentialsRolesEntity[ id=" + id + " ]";
    }

}
