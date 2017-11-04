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
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Name", nullable = false, length = 10)
    private String name;
    @Column(name = "FullName", length = 50)
    private String fullName;
    @OneToMany(mappedBy = "programId")
    private List<StudentEntity> studentEntityList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "programId")
    private List<CurriculumEntity> curriculumEntityList;
    @Column(name = "OJT")
    private int ojt;
    @Column(name = "Capstone", length = 50)
    private int capstone;
    @Column(name = "Graduate", length = 50)
    private int graduate;

    public ProgramEntity() {
    }

    public ProgramEntity(Integer id) {
        this.id = id;
    }

    public ProgramEntity(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getOjt() {
        return ojt;
    }

    public void setOjt(int ojt) {
        this.ojt = ojt;
    }

    public int getCapstone() {
        return capstone;
    }

    public void setCapstone(int capstone) {
        this.capstone = capstone;
    }

    public int getGraduate() {
        return graduate;
    }

    public void setGraduate(int graduate) {
        this.graduate = graduate;
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
        return "com.capstone.entites.ProgramEntity[ id=" + id + " ]";
    }
    
}
