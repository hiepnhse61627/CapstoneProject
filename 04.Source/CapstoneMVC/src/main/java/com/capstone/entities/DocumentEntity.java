package com.capstone.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "Document", schema = "dbo", catalog = "CapstoneProject")
public class DocumentEntity {
    private int id;
    private int docTypeId;
    private Integer docParentId;
    private String code;
    private String description;
    private DocTypeEntity docTypeByDocTypeId;
    private DocumentEntity documentByDocParentId;
    private Collection<DocumentEntity> documentsById;
    private Collection<DocumentStudentEntity> documentStudentsById;

    @Id
    @Column(name = "Id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "DocTypeId")
    public int getDocTypeId() {
        return docTypeId;
    }

    public void setDocTypeId(int docTypeId) {
        this.docTypeId = docTypeId;
    }

    @Basic
    @Column(name = "DocParentId")
    public Integer getDocParentId() {
        return docParentId;
    }

    public void setDocParentId(Integer docParentId) {
        this.docParentId = docParentId;
    }

    @Basic
    @Column(name = "Code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentEntity that = (DocumentEntity) o;

        if (id != that.id) return false;
        if (docTypeId != that.docTypeId) return false;
        if (docParentId != null ? !docParentId.equals(that.docParentId) : that.docParentId != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + docTypeId;
        result = 31 * result + (docParentId != null ? docParentId.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "DocTypeId", referencedColumnName = "Id")
    public DocTypeEntity getDocTypeByDocTypeId() {
        return docTypeByDocTypeId;
    }

    public void setDocTypeByDocTypeId(DocTypeEntity docTypeByDocTypeId) {
        this.docTypeByDocTypeId = docTypeByDocTypeId;
    }

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "DocParentId", referencedColumnName = "Id")
    public DocumentEntity getDocumentByDocParentId() {
        return documentByDocParentId;
    }

    public void setDocumentByDocParentId(DocumentEntity documentByDocParentId) {
        this.documentByDocParentId = documentByDocParentId;
    }

    @OneToMany(mappedBy = "documentByDocParentId")
    public Collection<DocumentEntity> getDocumentsById() {
        return documentsById;
    }

    public void setDocumentsById(Collection<DocumentEntity> documentsById) {
        this.documentsById = documentsById;
    }

    @OneToMany(mappedBy = "documentByDocumentId")
    public Collection<DocumentStudentEntity> getDocumentStudentsById() {
        return documentStudentsById;
    }

    public void setDocumentStudentsById(Collection<DocumentStudentEntity> documentStudentsById) {
        this.documentStudentsById = documentStudentsById;
    }
}
