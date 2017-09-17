package com.capstone.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "Course", schema = "dbo", catalog = "CapstoneProject")
public class CourseEntity {
    private int id;
    private String clazz;
    private Collection<MarksEntity> marksById;

    @Id
    @Column(name = "Id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Class")
    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseEntity that = (CourseEntity) o;

        if (id != that.id) return false;
        if (clazz != null ? !clazz.equals(that.clazz) : that.clazz != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (clazz != null ? clazz.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "courseByCourseId")
    public Collection<MarksEntity> getMarksById() {
        return marksById;
    }

    public void setMarksById(Collection<MarksEntity> marksById) {
        this.marksById = marksById;
    }
}
