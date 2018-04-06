/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import org.eclipse.persistence.annotations.CascadeOnDelete;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.*;

/**
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Program", catalog = "CapstoneProject", schema = "dbo")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "program", propOrder = {"name", "fullName"})
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "ProgramEntity.findAll", query = "SELECT p FROM ProgramEntity p")})
public class ProgramEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Name", nullable = false, length = 10)
    @XmlElement(required = true)
    private String name;
    @Column(name = "FullName", length = 50)
    @XmlElement(required = true)
    private String fullName;
    @Column(name = "OJT")
    @XmlTransient
    private Integer ojt;
    @Column(name = "Capstone")
    @XmlTransient
    private Integer capstone;
    @Column(name = "Graduate")
    @XmlTransient
    private Integer graduate;
    @Column(name = "GraduateCredits")
    @XmlTransient
    private Integer graduateCredits;
    @Column(name = "SpecializedCredits")
    @XmlTransient
    private Integer specializedCredits;
    @OneToMany(mappedBy = "programId")
    @XmlTransient
    private List<GraduationConditionEntity> graduationConditionEntityList;
    @OneToMany(mappedBy = "programId")
    @XmlTransient
    private List<OldRollNumberEntity> oldRollNumberEntityList;
    @OneToMany(mappedBy = "programId")
    @XmlTransient
    private List<StudentEntity> studentEntityList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "programId")
    @XmlTransient
    private List<CurriculumEntity> curriculumEntityList;

    public ProgramEntity() {
    }

    public ProgramEntity(Integer id) {
        this.id = id;
    }

    public ProgramEntity(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getOjt() {
        return ojt;
    }

    public void setOjt(Integer ojt) {
        this.ojt = ojt;
    }

    public Integer getCapstone() {
        return capstone;
    }

    public void setCapstone(Integer capstone) {
        this.capstone = capstone;
    }

    public Integer getGraduate() {
        return graduate;
    }

    public void setGraduate(Integer graduate) {
        this.graduate = graduate;
    }

    public Integer getGraduateCredits() {
        return graduateCredits;
    }

    public void setGraduateCredits(Integer graduateCredits) {
        this.graduateCredits = graduateCredits;
    }

    public Integer getSpecializedCredits() {
        return specializedCredits;
    }

    public void setSpecializedCredits(Integer specializedCredits) {
        this.specializedCredits = specializedCredits;
    }

    public List<GraduationConditionEntity> getGraduationConditionEntityList() {
        return graduationConditionEntityList;
    }

    public void setGraduationConditionEntityList(List<GraduationConditionEntity> graduationConditionEntityList) {
        this.graduationConditionEntityList = graduationConditionEntityList;
    }

    public List<OldRollNumberEntity> getOldRollNumberEntityList() {
        return oldRollNumberEntityList;
    }

    public void setOldRollNumberEntityList(List<OldRollNumberEntity> oldRollNumberEntityList) {
        this.oldRollNumberEntityList = oldRollNumberEntityList;
    }

    public List<StudentEntity> getStudentEntityList() {
        return studentEntityList;
    }

    public void setStudentEntityList(List<StudentEntity> studentEntityList) {
        this.studentEntityList = studentEntityList;
    }

    public List<CurriculumEntity> getCurriculumEntityList() {
        return curriculumEntityList;
    }

    public void setCurriculumEntityList(List<CurriculumEntity> curriculumEntityList) {
        this.curriculumEntityList = curriculumEntityList;
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
        if (!(object instanceof ProgramEntity)) {
            return false;
        }
        ProgramEntity other = (ProgramEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.capstone.entities.ProgramEntity[ id=" + id + " ]";
    }

}
