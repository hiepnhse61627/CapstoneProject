package com.capstone.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author StormNs
 */
@Entity
@Table(name = "RolesAuthority")
@NamedQueries({
        @NamedQuery(name = "RolesAuthorityEntity.findAll", query = "SELECT r FROM RolesAuthorityEntity r")
        , @NamedQuery(name = "RolesAuthorityEntity.findById", query = "SELECT r FROM RolesAuthorityEntity r WHERE r.id = :id")})
public class RolesAuthorityEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn(name = "MenuId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private DynamicMenuEntity menuId;
    @JoinColumn(name = "RolesId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private RolesEntity rolesId;

    public RolesAuthorityEntity() {
    }

    public RolesAuthorityEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DynamicMenuEntity getMenuId() {
        return menuId;
    }

    public void setMenuId(DynamicMenuEntity menuId) {
        this.menuId = menuId;
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
        if (!(object instanceof RolesAuthorityEntity)) {
            return false;
        }
        RolesAuthorityEntity other = (RolesAuthorityEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.RolesAuthorityEntity[ id=" + id + " ]";
    }

}
