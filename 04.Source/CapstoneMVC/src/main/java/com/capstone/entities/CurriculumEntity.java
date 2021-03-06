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
@Table(name = "Curriculum", catalog = "CapstoneProject", schema = "dbo")
@NamedQueries({
    @NamedQuery(name = "CurriculumEntity.findAll", query = "SELECT c FROM CurriculumEntity c")})
public class CurriculumEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Name", nullable = false, length = 50)
    private String name;
    @Column(name = "SpecializedCredits")
    private Integer specializedCredits;
    @Column(name = "OjtTerm")
    private Integer ojtTerm;
    @OneToMany(mappedBy = "curriculumId", fetch = FetchType.EAGER)
    private List<SubjectCurriculumEntity> subjectCurriculumEntityList;
    @OneToMany(mappedBy = "curriculumId")
    private List<DocumentStudentEntity> documentStudentEntityList;
    @JoinColumn(name = "ProgramId", referencedColumnName = "Id", nullable = false)
    @ManyToOne(optional = false)
    private ProgramEntity programId;
    @OneToMany(mappedBy = "curriculumId")
    private List<SimulateDocumentStudentEntity> simulateDocumentStudentEntityList;

    public CurriculumEntity() {
    }

    public CurriculumEntity(Integer id) {
        this.id = id;
    }

    public CurriculumEntity(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSpecializedCredits() {
        return specializedCredits;
    }

    public void setSpecializedCredits(Integer specializedCredits) {
        this.specializedCredits = specializedCredits;
    }

    public Integer getOjtTerm() {
        return ojtTerm;
    }

    public void setOjtTerm(Integer ojtTerm) {
        this.ojtTerm = ojtTerm;
    }

    public List<SubjectCurriculumEntity> getSubjectCurriculumEntityList() {
        return subjectCurriculumEntityList;
    }

    public void setSubjectCurriculumEntityList(List<SubjectCurriculumEntity> subjectCurriculumEntityList) {
        this.subjectCurriculumEntityList = subjectCurriculumEntityList;
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

    public List<SimulateDocumentStudentEntity> getSimulateDocumentStudentEntityList() {
        return simulateDocumentStudentEntityList;
    }

    public void setSimulateDocumentStudentEntityList(List<SimulateDocumentStudentEntity> simulateDocumentStudentEntityList) {
        this.simulateDocumentStudentEntityList = simulateDocumentStudentEntityList;
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
        if (!(object instanceof CurriculumEntity)) {
            return false;
        }
        CurriculumEntity other = (CurriculumEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.capstone.entities.CurriculumEntity[ id=" + id + " ]";
    }
    
}
