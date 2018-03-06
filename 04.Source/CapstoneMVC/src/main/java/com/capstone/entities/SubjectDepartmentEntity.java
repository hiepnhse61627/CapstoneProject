package com.capstone.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author hoanglong
 */
@Entity
@Table(name = "Subject_Department")
@NamedQueries({
        @NamedQuery(name = "SubjectDepartmentEntity.findAll", query = "SELECT s FROM SubjectDepartmentEntity s"),
        @NamedQuery(name = "SubjectDepartmentEntity.findById", query = "SELECT s FROM SubjectDepartmentEntity s WHERE s.id = :id")})
public class SubjectDepartmentEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn(name = "DeptId", referencedColumnName = "DeptId")
    @ManyToOne
    private DepartmentEntity deptId;
    @JoinColumn(name = "SubjectId", referencedColumnName = "Id")
    @ManyToOne
    private SubjectEntity subjectId;

    public SubjectDepartmentEntity() {
    }

    public SubjectDepartmentEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DepartmentEntity getDeptId() {
        return deptId;
    }

    public void setDeptId(DepartmentEntity deptId) {
        this.deptId = deptId;
    }

    public SubjectEntity getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(SubjectEntity subjectId) {
        this.subjectId = subjectId;
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
        if (!(object instanceof SubjectDepartmentEntity)) {
            return false;
        }
        SubjectDepartmentEntity other = (SubjectDepartmentEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication102.SubjectDepartmentEntity[ id=" + id + " ]";
    }

}
