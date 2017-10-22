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
 * @author Rem
 */
@Entity
@Table(name = "Program")
@NamedQueries({
    @NamedQuery(name = "ProgramEntity.findAll", query = "SELECT p FROM ProgramEntity p")})
public class ProgramEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @OneToMany(mappedBy = "programId")
    private List<SubjectCurriculumEntity> subjectCurriculumEntityList;
    @Basic(optional = false)
    @Column(name = "FullName")
    private String fullName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "programId")
    private List<CurriculumEntity> curriculumEntityList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "programId")
    private List<StudentEntity> studentList;

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

    public List<SubjectCurriculumEntity> getSubjectCurriculumEntityList() {
        return subjectCurriculumEntityList;
    }

    public void setSubjectCurriculumEntityList(List<SubjectCurriculumEntity> subjectCurriculumEntityList) {
        this.subjectCurriculumEntityList = subjectCurriculumEntityList;
	}
	
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<CurriculumEntity> getCurriculumEntityList() {
        return curriculumEntityList;
    }

    public void setCurriculumEntityList(List<CurriculumEntity> curriculumEntityList) {
        this.curriculumEntityList = curriculumEntityList;
    }

    public List<StudentEntity> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<StudentEntity> studentList) {
        this.studentList = studentList;
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
