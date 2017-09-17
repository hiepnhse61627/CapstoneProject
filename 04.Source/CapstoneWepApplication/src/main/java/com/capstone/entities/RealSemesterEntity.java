package com.capstone.entities;

import javax.persistence.*;

@Entity
@Table(name = "RealSemester", schema = "dbo", catalog = "CapstoneProject")
public class RealSemesterEntity {
    private Integer id;
    private String semester;

    @Id
    @Column(name = "Id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Semester")
    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealSemesterEntity that = (RealSemesterEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (semester != null ? !semester.equals(that.semester) : that.semester != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (semester != null ? semester.hashCode() : 0);
        return result;
    }
}
