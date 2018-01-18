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
 * @author hoanglong
 */
@Entity
@Table(name = "Emp_Competence")
@NamedQueries({
    @NamedQuery(name = "EmpCompetenceEntity.findAll", query = "SELECT e FROM EmpCompetenceEntity e"),
    @NamedQuery(name = "EmpCompetenceEntity.findById", query = "SELECT e FROM EmpCompetenceEntity e WHERE e.id = :id")})
public class EmpCompetenceEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn(name = "EmployeeId", referencedColumnName = "Id")
    @ManyToOne
    private EmployeeEntity employeeId;
    @JoinColumn(name = "SubjectId", referencedColumnName = "Id")
    @ManyToOne
    private SubjectEntity subjectId;

    public EmpCompetenceEntity() {
    }

    public EmpCompetenceEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EmployeeEntity getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(EmployeeEntity employeeId) {
        this.employeeId = employeeId;
    }

    public SubjectEntity getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(SubjectEntity subjectId) {
        this.subjectId = subjectId;
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
        if (!(object instanceof EmpCompetenceEntity)) {
            return false;
        }
        EmpCompetenceEntity other = (EmpCompetenceEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpaentitygen.EmpCompetenceEntity[ id=" + id + " ]";
    }
    
}
