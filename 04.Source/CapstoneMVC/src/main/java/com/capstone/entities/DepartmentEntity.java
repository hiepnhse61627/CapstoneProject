package com.capstone.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author hoanglong
 */
@Entity
@Table(name = "Department")
@NamedQueries({
        @NamedQuery(name = "DepartmentEntity.findAll", query = "SELECT d FROM DepartmentEntity d"),
        @NamedQuery(name = "DepartmentEntity.findByDeptId", query = "SELECT d FROM DepartmentEntity d WHERE d.deptId = :deptId"),
        @NamedQuery(name = "DepartmentEntity.findByDeptName", query = "SELECT d FROM DepartmentEntity d WHERE d.deptName = :deptName"),
        @NamedQuery(name = "DepartmentEntity.findByDeptShortName", query = "SELECT d FROM DepartmentEntity d WHERE d.deptShortName = :deptShortName")})
public class DepartmentEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "DeptId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deptId;
    @Column(name = "DeptName")
    private String deptName;
    @Column(name = "DeptShortName")
    private String deptShortName;
    @OneToMany(mappedBy = "deptId")
    private List<SubjectDepartmentEntity> subjectDepartmentEntityList;

    public DepartmentEntity() {
    }

    public DepartmentEntity(Integer deptId) {
        this.deptId = deptId;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptShortName() {
        return deptShortName;
    }

    public void setDeptShortName(String deptShortName) {
        this.deptShortName = deptShortName;
    }

    public List<SubjectDepartmentEntity> getSubjectDepartmentEntityList() {
        return subjectDepartmentEntityList;
    }

    public void setSubjectDepartmentEntityList(List<SubjectDepartmentEntity> subjectDepartmentEntityList) {
        this.subjectDepartmentEntityList = subjectDepartmentEntityList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (deptId != null ? deptId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DepartmentEntity)) {
            return false;
        }
        DepartmentEntity other = (DepartmentEntity) object;
        if ((this.deptId == null && other.deptId != null) || (this.deptId != null && !this.deptId.equals(other.deptId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication102.DepartmentEntity[ deptId=" + deptId + " ]";
    }

}