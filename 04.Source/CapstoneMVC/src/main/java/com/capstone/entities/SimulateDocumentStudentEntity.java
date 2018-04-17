package com.capstone.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author StormNs
 */
@Entity
@Table(name = "SimulateDocument_Student")
@NamedQueries({
        @NamedQuery(name = "SimulateDocumentStudentEntity.findAll", query = "SELECT s FROM SimulateDocumentStudentEntity s")
        , @NamedQuery(name = "SimulateDocumentStudentEntity.findById", query = "SELECT s FROM SimulateDocumentStudentEntity s WHERE s.id = :id")
        , @NamedQuery(name = "SimulateDocumentStudentEntity.findByOldStudentId", query = "SELECT s FROM SimulateDocumentStudentEntity s WHERE s.oldStudentId = :oldStudentId")
        , @NamedQuery(name = "SimulateDocumentStudentEntity.findByCreatedDate", query = "SELECT s FROM SimulateDocumentStudentEntity s WHERE s.createdDate = :createdDate")
        , @NamedQuery(name = "SimulateDocumentStudentEntity.findByIsActive", query = "SELECT s FROM SimulateDocumentStudentEntity s WHERE s.isActive = :isActive")})
public class SimulateDocumentStudentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Column(name = "OldStudentId")
    private Integer oldStudentId;
    @Column(name = "CreatedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "IsActive")
    private Boolean isActive;
    @JoinColumn(name = "CurriculumId", referencedColumnName = "Id")
    @ManyToOne
    private CurriculumEntity curriculumId;
    @JoinColumn(name = "DocumentId", referencedColumnName = "Id")
    @ManyToOne
    private DocumentEntity documentId;
    @JoinColumn(name = "StudentId", referencedColumnName = "Id")
    @ManyToOne
    private StudentEntity studentId;

    public SimulateDocumentStudentEntity() {
    }

    public SimulateDocumentStudentEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOldStudentId() {
        return oldStudentId;
    }

    public void setOldStudentId(Integer oldStudentId) {
        this.oldStudentId = oldStudentId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public CurriculumEntity getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(CurriculumEntity curriculumId) {
        this.curriculumId = curriculumId;
    }

    public DocumentEntity getDocumentId() {
        return documentId;
    }

    public void setDocumentId(DocumentEntity documentId) {
        this.documentId = documentId;
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
        if (!(object instanceof SimulateDocumentStudentEntity)) {
            return false;
        }
        SimulateDocumentStudentEntity other = (SimulateDocumentStudentEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.SimulateDocumentStudentEntity[ id=" + id + " ]";
    }

}