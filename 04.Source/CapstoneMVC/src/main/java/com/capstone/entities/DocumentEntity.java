/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Document", catalog = "CapstoneProject", schema = "dbo")
public class DocumentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "Id", nullable = false)
    private Integer id;
    @Column(name = "Code", length = 50)
    private String code;
    @Column(name = "Description", length = 2147483647)
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "documentId")
    private List<DocumentStudentEntity> documentStudentList;
    @JoinColumn(name = "DocTypeId", referencedColumnName = "Id", nullable = false)
    @ManyToOne(optional = false)
    private DocTypeEntity docTypeId;
    @OneToMany(mappedBy = "docParentId")
    private List<DocumentEntity> documentList;
    @JoinColumn(name = "DocParentId", referencedColumnName = "Id")
    @ManyToOne
    private DocumentEntity docParentId;

    public DocumentEntity() {
    }

    public DocumentEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DocumentStudentEntity> getDocumentStudentList() {
        return documentStudentList;
    }

    public void setDocumentStudentList(List<DocumentStudentEntity> documentStudentList) {
        this.documentStudentList = documentStudentList;
    }

    public DocTypeEntity getDocTypeId() {
        return docTypeId;
    }

    public void setDocTypeId(DocTypeEntity docTypeId) {
        this.docTypeId = docTypeId;
    }

    public List<DocumentEntity> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<DocumentEntity> documentList) {
        this.documentList = documentList;
    }

    public DocumentEntity getDocParentId() {
        return docParentId;
    }

    public void setDocParentId(DocumentEntity docParentId) {
        this.docParentId = docParentId;
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
        if (!(object instanceof DocumentEntity)) {
            return false;
        }
        DocumentEntity other = (DocumentEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Document[ id=" + id + " ]";
    }
    
}
