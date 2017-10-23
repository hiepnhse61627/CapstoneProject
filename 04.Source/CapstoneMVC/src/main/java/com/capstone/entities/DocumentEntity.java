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
@Table(name = "Document", catalog = "CapstoneProject", schema = "dbo")
@NamedQueries({
    @NamedQuery(name = "DocumentEntity.findAll", query = "SELECT d FROM DocumentEntity d")})
public class DocumentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Code", length = 50)
    private String code;
    @Column(name = "Description", length = 2147483647)
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "documentId")
    private List<DocumentStudentEntity> documentStudentEntityList;
    @JoinColumn(name = "DocTypeId", referencedColumnName = "Id", nullable = false)
    @ManyToOne(optional = false)
    private DocTypeEntity docTypeId;
    @OneToMany(mappedBy = "docParentId")
    private List<DocumentEntity> documentEntityList;
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

    public List<DocumentStudentEntity> getDocumentStudentEntityList() {
        return documentStudentEntityList;
    }

    public void setDocumentStudentEntityList(List<DocumentStudentEntity> documentStudentEntityList) {
        this.documentStudentEntityList = documentStudentEntityList;
    }

    public DocTypeEntity getDocTypeId() {
        return docTypeId;
    }

    public void setDocTypeId(DocTypeEntity docTypeId) {
        this.docTypeId = docTypeId;
    }

    public List<DocumentEntity> getDocumentEntityList() {
        return documentEntityList;
    }

    public void setDocumentEntityList(List<DocumentEntity> documentEntityList) {
        this.documentEntityList = documentEntityList;
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
        return "com.capstone.entities.DocumentEntity[ id=" + id + " ]";
    }
    
}
