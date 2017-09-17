package com.capstone.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "Program", schema = "dbo", catalog = "CapstoneProject")
public class ProgramEntity {
    private int id;
    private String name;
    private Collection<CurriculumEntity> curriculaById;

    @Id
    @Column(name = "Id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

        ProgramEntity that = (ProgramEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "programByProgramId")
    public Collection<CurriculumEntity> getCurriculaById() {
        return curriculaById;
    }

    public void setCurriculaById(Collection<CurriculumEntity> curriculaById) {
        this.curriculaById = curriculaById;
    }
}
