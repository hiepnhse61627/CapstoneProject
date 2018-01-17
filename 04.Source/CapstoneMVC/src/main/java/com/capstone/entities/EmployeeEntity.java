/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author hoanglong
 */
@Entity
@Table(name = "Employee")
@NamedQueries({
        @NamedQuery(name = "EmployeeEntity.findAll", query = "SELECT e FROM EmployeeEntity e"),
        @NamedQuery(name = "EmployeeEntity.findById", query = "SELECT e FROM EmployeeEntity e WHERE e.id = :id"),
        @NamedQuery(name = "EmployeeEntity.findByCode", query = "SELECT e FROM EmployeeEntity e WHERE e.code = :code"),
        @NamedQuery(name = "EmployeeEntity.findByFullName", query = "SELECT e FROM EmployeeEntity e WHERE e.fullName = :fullName"),
        @NamedQuery(name = "EmployeeEntity.findByPosition", query = "SELECT e FROM EmployeeEntity e WHERE e.position = :position"),
        @NamedQuery(name = "EmployeeEntity.findByEmailEDU", query = "SELECT e FROM EmployeeEntity e WHERE e.emailEDU = :emailEDU"),
        @NamedQuery(name = "EmployeeEntity.findByEmailFE", query = "SELECT e FROM EmployeeEntity e WHERE e.emailFE = :emailFE"),
        @NamedQuery(name = "EmployeeEntity.findByPersonalEmail", query = "SELECT e FROM EmployeeEntity e WHERE e.personalEmail = :personalEmail"),
        @NamedQuery(name = "EmployeeEntity.findByGender", query = "SELECT e FROM EmployeeEntity e WHERE e.gender = :gender"),
        @NamedQuery(name = "EmployeeEntity.findByDateOfBirth", query = "SELECT e FROM EmployeeEntity e WHERE e.dateOfBirth = :dateOfBirth"),
        @NamedQuery(name = "EmployeeEntity.findByPhone", query = "SELECT e FROM EmployeeEntity e WHERE e.phone = :phone"),
        @NamedQuery(name = "EmployeeEntity.findByAddress", query = "SELECT e FROM EmployeeEntity e WHERE e.address = :address"),
        @NamedQuery(name = "EmployeeEntity.findByContract", query = "SELECT e FROM EmployeeEntity e WHERE e.contract = :contract")})
public class EmployeeEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Code")
    private String code;
    @Column(name = "FullName")
    private String fullName;
    @Column(name = "Position")
    private String position;
    @Column(name = "EmailEDU")
    private String emailEDU;
    @Column(name = "EmailFE")
    private String emailFE;
    @Column(name = "PersonalEmail")
    private String personalEmail;
    @Column(name = "Gender")
    private Boolean gender;
    @Column(name = "DateOfBirth")
    private String dateOfBirth;
    @Column(name = "Phone")
    private String phone;
    @Column(name = "Contract")
    private String contract;
    @Column(name = "Address")
    private String address;
    @OneToMany(mappedBy = "employeeId")
    private List<EmpCompetenceEntity> empCompetenceEntityList;
    @OneToMany(mappedBy = "empId")
    private List<ScheduleEntity> scheduleEntityList;

    public EmployeeEntity() {
    }

    public EmployeeEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmailEDU() {
        return emailEDU;
    }

    public void setEmailEDU(String emailEDU) {
        this.emailEDU = emailEDU;
    }

    public String getEmailFE() {
        return emailFE;
    }

    public void setEmailFE(String emailFE) {
        this.emailFE = emailFE;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public List<EmpCompetenceEntity> getEmpCompetenceEntityList() {
        return empCompetenceEntityList;
    }

    public void setEmpCompetenceEntityList(List<EmpCompetenceEntity> empCompetenceEntityList) {
        this.empCompetenceEntityList = empCompetenceEntityList;
    }

    public List<ScheduleEntity> getScheduleEntityList() {
        return scheduleEntityList;
    }

    public void setScheduleEntityList(List<ScheduleEntity> scheduleEntityList) {
        this.scheduleEntityList = scheduleEntityList;
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
        if (!(object instanceof EmployeeEntity)) {
            return false;
        }
        EmployeeEntity other = (EmployeeEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication91.EmployeeEntity[ id=" + id + " ]";
    }

}
