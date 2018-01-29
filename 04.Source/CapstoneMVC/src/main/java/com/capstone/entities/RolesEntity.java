package com.capstone.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author StormNs
 */
@Entity
@Table(name = "Roles")
@NamedQueries({
        @NamedQuery(name = "RolesEntity.findAll", query = "SELECT r FROM RolesEntity r")
        , @NamedQuery(name = "RolesEntity.findById", query = "SELECT r FROM RolesEntity r WHERE r.id = :id")})
public class RolesEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    private String id;
    @OneToMany(mappedBy = "rolesId")
    private Collection<CredentialsRolesEntity> credentialsRolesEntityCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rolesId")
    private Collection<RolesAuthorityEntity> rolesAuthorityEntityCollection;

    public RolesEntity() {
    }

    public RolesEntity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Collection<CredentialsRolesEntity> getCredentialsRolesEntityCollection() {
        return credentialsRolesEntityCollection;
    }

    public void setCredentialsRolesEntityCollection(Collection<CredentialsRolesEntity> credentialsRolesEntityCollection) {
        this.credentialsRolesEntityCollection = credentialsRolesEntityCollection;
    }

    public Collection<RolesAuthorityEntity> getRolesAuthorityEntityCollection() {
        return rolesAuthorityEntityCollection;
    }

    public void setRolesAuthorityEntityCollection(Collection<RolesAuthorityEntity> rolesAuthorityEntityCollection) {
        this.rolesAuthorityEntityCollection = rolesAuthorityEntityCollection;
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
        if (!(object instanceof RolesEntity)) {
            return false;
        }
        RolesEntity other = (RolesEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.RolesEntity[ id=" + id + " ]";
    }

}

