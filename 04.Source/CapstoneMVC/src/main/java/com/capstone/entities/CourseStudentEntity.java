package com.capstone.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author hoanglong
 */
@Entity
@Table(name = "Course_Student")
@NamedQueries({
        @NamedQuery(name = "CourseStudentEntity.findAll", query = "SELECT c FROM CourseStudentEntity c"),
        @NamedQuery(name = "CourseStudentEntity.findById", query = "SELECT c FROM CourseStudentEntity c WHERE c.id = :id")})
public class CourseStudentEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn(name = "CourseId", referencedColumnName = "Id")
    @ManyToOne
    private CourseEntity courseId;
    @JoinColumn(name = "StudentId", referencedColumnName = "Id")
    @ManyToOne
    private StudentEntity studentId;
    @Column(name = "GroupName")
    private String groupName;

    public CourseStudentEntity() {
    }

    public CourseStudentEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CourseEntity getCourseId() {
        return courseId;
    }

    public void setCourseId(CourseEntity courseId) {
        this.courseId = courseId;
    }

    public StudentEntity getStudentId() {
        return studentId;
    }

    public void setStudentId(StudentEntity studentId) {
        this.studentId = studentId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
        if (!(object instanceof CourseStudentEntity)) {
            return false;
        }
        CourseStudentEntity other = (CourseStudentEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication97.CourseStudentEntity[ id=" + id + " ]";
    }

}
