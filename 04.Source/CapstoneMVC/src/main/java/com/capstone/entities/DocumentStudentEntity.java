package com.capstone.entities;

import javax.persistence.*;

@Entity
@Table(name = "Document_Student", schema = "dbo", catalog = "CapstoneProject")
public class DocumentStudentEntity {
    private int id;
    private int documentId;
    private int studentId;
    private int curriculumId;
    private DocumentEntity documentByDocumentId;
    private StudentEntity studentByStudentId;
    private CurriculumEntity curriculumByCurriculumId;

    @Id
    @Column(name = "Id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "DocumentId")
    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    @Basic
    @Column(name = "StudentId")
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    @Basic
    @Column(name = "CurriculumId")
    public int getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(int curriculumId) {
        this.curriculumId = curriculumId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentStudentEntity that = (DocumentStudentEntity) o;

        if (id != that.id) return false;
        if (documentId != that.documentId) return false;
        if (studentId != that.studentId) return false;
        if (curriculumId != that.curriculumId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + documentId;
        result = 31 * result + studentId;
        result = 31 * result + curriculumId;
        return result;
    }

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "DocumentId", referencedColumnName = "Id")
    public DocumentEntity getDocumentByDocumentId() {
        return documentByDocumentId;
    }

    public void setDocumentByDocumentId(DocumentEntity documentByDocumentId) {
        this.documentByDocumentId = documentByDocumentId;
    }

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "StudentId", referencedColumnName = "ID")
    public StudentEntity getStudentByStudentId() {
        return studentByStudentId;
    }

    public void setStudentByStudentId(StudentEntity studentByStudentId) {
        this.studentByStudentId = studentByStudentId;
    }

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "CurriculumId", referencedColumnName = "Id")
    public CurriculumEntity getCurriculumByCurriculumId() {
        return curriculumByCurriculumId;
    }

    public void setCurriculumByCurriculumId(CurriculumEntity curriculumByCurriculumId) {
        this.curriculumByCurriculumId = curriculumByCurriculumId;
    }
}
