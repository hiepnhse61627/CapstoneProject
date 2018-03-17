package com.capstone.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.persistence.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rolesId")
    private List<RolesAuthorityEntity> rolesAuthorityEntityList;
    @OneToMany(mappedBy = "rolesId")
    private List<CredentialsRolesEntity> credentialsRolesEntityList;

    public RolesEntity() {
    }

    public RolesEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RolesAuthorityEntity> getRolesAuthorityEntityList() {
        return rolesAuthorityEntityList;
    }

    public void setRolesAuthorityEntityList(List<RolesAuthorityEntity> rolesAuthorityEntityList) {
        this.rolesAuthorityEntityList = rolesAuthorityEntityList;
    }

    public List<CredentialsRolesEntity> getCredentialsRolesEntityList() {
        return credentialsRolesEntityList;
    }

    public void setCredentialsRolesEntityList(List<CredentialsRolesEntity> credentialsRolesEntityList) {
        this.credentialsRolesEntityList = credentialsRolesEntityList;
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

