package com.capstone.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "Curriculum", schema = "dbo", catalog = "CapstoneProject")
public class CurriculumEntity {
    private int id;
    private int programId;
    private String name;
    private ProgramEntity programByProgramId;
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
    @Column(name = "ProgramId")
    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    @Basic
    @Column(name = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurriculumEntity that = (CurriculumEntity) o;

        if (id != that.id) return false;
        if (programId != that.programId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + programId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "ProgramId", referencedColumnName = "Id", nullable = false)
    public ProgramEntity getProgramByProgramId() {
        return programByProgramId;
    }

    public void setProgramByProgramId(ProgramEntity programByProgramId) {
        this.programByProgramId = programByProgramId;
    }

    @OneToMany(mappedBy = "curriculumByCurriculumId")
    public Collection<DocumentStudentEntity> getDocumentStudentsById() {
        return documentStudentsById;
    }

    public void setDocumentStudentsById(Collection<DocumentStudentEntity> documentStudentsById) {
        this.documentStudentsById = documentStudentsById;
    }
}
