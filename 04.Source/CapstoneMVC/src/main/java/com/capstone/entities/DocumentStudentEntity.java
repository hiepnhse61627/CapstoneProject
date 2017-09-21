/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Document_Student", catalog = "CapstoneProject", schema = "dbo")
public class DocumentStudentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "Id", nullable = false)
    private Integer id;
    @JoinColumn(name = "CurriculumId", referencedColumnName = "Id", nullable = false)
    @ManyToOne(optional = false)
    private CurriculumEntity curriculumId;
    @JoinColumn(name = "DocumentId", referencedColumnName = "Id", nullable = false)
    @ManyToOne(optional = false)
    private DocumentEntity documentId;
    @JoinColumn(name = "StudentId", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private StudentEntity studentId;

    public DocumentStudentEntity() {
    }

    public DocumentStudentEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        if (!(object instanceof DocumentStudentEntity)) {
            return false;
        }
        DocumentStudentEntity other = (DocumentStudentEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.DocumentStudent[ id=" + id + " ]";
    }
    
}
