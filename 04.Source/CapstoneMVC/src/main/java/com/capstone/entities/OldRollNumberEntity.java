/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "OldRollNumber", catalog = "CapstoneProject", schema = "dbo")
@NamedQueries({
    @NamedQuery(name = "OldRollNumberEntity.findAll", query = "SELECT o FROM OldRollNumberEntity o")})
public class OldRollNumberEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "OldRollNumber", length = 50)
    private String oldRollNumber;
    @Column(name = "ChangedCurriculumDate")
    @Temporal(TemporalType.DATE)
    private Date changedCurriculumDate;
    @OneToMany(mappedBy = "oldStudentId")
    private List<DocumentStudentEntity> documentStudentEntityList;
    @JoinColumn(name = "ProgramId", referencedColumnName = "Id")
    @ManyToOne
    private ProgramEntity programId;
    @JoinColumn(name = "StudentId", referencedColumnName = "Id")
    @ManyToOne
    private StudentEntity studentId;

    public OldRollNumberEntity() {
    }

    public OldRollNumberEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOldRollNumber() {
        return oldRollNumber;
    }

    public void setOldRollNumber(String oldRollNumber) {
        this.oldRollNumber = oldRollNumber;
    }

    public Date getChangedCurriculumDate() {
        return changedCurriculumDate;
    }

    public void setChangedCurriculumDate(Date changedCurriculumDate) {
        this.changedCurriculumDate = changedCurriculumDate;
    }

    public List<DocumentStudentEntity> getDocumentStudentEntityList() {
        return documentStudentEntityList;
    }

    public void setDocumentStudentEntityList(List<DocumentStudentEntity> documentStudentEntityList) {
        this.documentStudentEntityList = documentStudentEntityList;
    }

    public ProgramEntity getProgramId() {
        return programId;
    }

    public void setProgramId(ProgramEntity programId) {
        this.programId = programId;
    }

    public StudentEntity getStudentId() {
        return studentId;
    }

    public void setStudentId(StudentEntity studentId) {
        this.studentId = studentId;
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
        if (!(object instanceof OldRollNumberEntity)) {
            return false;
        }
        OldRollNumberEntity other = (OldRollNumberEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.capstone.entities.OldRollNumberEntity[ id=" + id + " ]";
    }
    
}
