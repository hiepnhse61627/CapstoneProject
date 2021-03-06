/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "RealSemester", catalog = "CapstoneProject", schema = "dbo")
@NamedQueries({
    @NamedQuery(name = "RealSemesterEntity.findAll", query = "SELECT r FROM RealSemesterEntity r")})
public class RealSemesterEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Semester", length = 50)
    private String semester;
    @OneToMany(mappedBy = "semesterId")
    private List<MarksEntity> marksEntityList;
    @OneToMany(mappedBy = "semesterId")
    private List<StudentStatusEntity> studentStatusEntityList;
    @Column(name = "IsActivated")
    private boolean active;
    @Column(name = "StartDate")
    private String startDate;
    @Column(name = "EndDate")
    private String endDate;


    public RealSemesterEntity() {
    }

    public RealSemesterEntity(Integer id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public List<MarksEntity> getMarksEntityList() {
        return marksEntityList;
    }

    public void setMarksEntityList(List<MarksEntity> marksEntityList) {
        this.marksEntityList = marksEntityList;
    }

    public List<StudentStatusEntity> getStudentStatusEntityList() {
        return studentStatusEntityList;
    }

    public void setStudentStatusEntityList(List<StudentStatusEntity> studentStatusEntityList) {
        this.studentStatusEntityList = studentStatusEntityList;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
        if (!(object instanceof RealSemesterEntity)) {
            return false;
        }
        RealSemesterEntity other = (RealSemesterEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.capstone.entities.RealSemesterEntity[ id=" + id + " ]";
    }
    
}
