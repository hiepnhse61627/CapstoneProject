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
@Table(name = "RealSemester", catalog = "CapstoneProject", schema = "dbo")
public class RealSemesterEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Semester", length = 50)
    private String semester;
    @OneToMany(mappedBy = "semesterId")
    private List<MarksEntity> marksList;

    public RealSemesterEntity() {
    }

    public RealSemesterEntity(Integer id) {
        this.id = id;
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

    public List<MarksEntity> getMarksList() {
        return marksList;
    }

    public void setMarksList(List<MarksEntity> marksList) {
        this.marksList = marksList;
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
        return "entities.RealSemester[ id=" + id + " ]";
    }
    
}
