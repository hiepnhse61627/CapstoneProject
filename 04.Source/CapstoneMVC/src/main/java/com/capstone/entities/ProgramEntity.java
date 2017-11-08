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
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Program", catalog = "CapstoneProject", schema = "dbo")
@NamedQueries({
    @NamedQuery(name = "ProgramEntity.findAll", query = "SELECT p FROM ProgramEntity p")})
public class ProgramEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Name", nullable = false, length = 10)
    private String name;
    @Column(name = "FullName", length = 50)
    private String fullName;
    @Column(name = "OJT")
    private Integer ojt;
    @Column(name = "Capstone")
    private Integer capstone;
    @Column(name = "Graduate")
    private Integer graduate;
    @ManyToMany
    private List<ProgramEntity> programEntityList;
    @OneToMany(mappedBy = "programId")
    private List<StudentEntity> studentEntityList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "programId")
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

    public List<ProgramEntity> getProgramEntityList() {
        return programEntityList;
    }

    public void setProgramEntityList(List<ProgramEntity> programEntityList) {
        this.programEntityList = programEntityList;
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
