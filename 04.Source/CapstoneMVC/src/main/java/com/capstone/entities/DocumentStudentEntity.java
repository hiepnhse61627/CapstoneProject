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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Rem
 */
@Entity
@Table(name = "Document_Student")
@NamedQueries({
    @NamedQuery(name = "DocumentStudentEntity.findAll", query = "SELECT d FROM DocumentStudentEntity d")
    , @NamedQuery(name = "DocumentStudentEntity.findById", query = "SELECT d FROM DocumentStudentEntity d WHERE d.id = :id")})
public class DocumentStudentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn(name = "CurriculumId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private CurriculumEntity curriculumId;
    @JoinColumn(name = "DocumentId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private DocumentEntity documentId;
    @JoinColumn(name = "StudentId", referencedColumnName = "ID")
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
        return "entities.DocumentStudentEntity[ id=" + id + " ]";
    }
    
}
