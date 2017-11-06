/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Dynamic_Menu", catalog = "CapstoneProject", schema = "dbo")
@NamedQueries({
    @NamedQuery(name = "DynamicMenuEntity.findAll", query = "SELECT d FROM DynamicMenuEntity d")})
public class DynamicMenuEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Role", length = 2147483647)
    private String role;
    @Column(name = "FunctionGroup", length = 2147483647)
    private String functionGroup;
    @Column(name = "FunctionName", length = 2147483647)
    private String functionName;
    @Column(name = "GroupName", length = 2147483647)
    private String groupName;
    @Column(name = "Link", length = 2147483647)
    private String link;

    public DynamicMenuEntity() {
    }

    public DynamicMenuEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFunctionGroup() {
        return functionGroup;
    }

    public void setFunctionGroup(String functionGroup) {
        this.functionGroup = functionGroup;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
        if (!(object instanceof DynamicMenuEntity)) {
            return false;
        }
        DynamicMenuEntity other = (DynamicMenuEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.capstone.entities.DynamicMenuEntity[ id=" + id + " ]";
    }
    
}
