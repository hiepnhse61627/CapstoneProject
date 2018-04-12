package com.capstone.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author StormNs
 */
@Entity
@Table(name = "GraduateDetail")
@NamedQueries({
        @NamedQuery(name = "GraduateDetailEntity.findAll", query = "SELECT g FROM GraduateDetailEntity g")
        , @NamedQuery(name = "GraduateDetailEntity.findByStudentId", query = "SELECT g FROM GraduateDetailEntity g WHERE g.studentId = :studentId")
        , @NamedQuery(name = "GraduateDetailEntity.findByDiplomaCode", query = "SELECT g FROM GraduateDetailEntity g WHERE g.diplomaCode = :diplomaCode")
        , @NamedQuery(name = "GraduateDetailEntity.findByCertificateCode", query = "SELECT g FROM GraduateDetailEntity g WHERE g.certificateCode = :certificateCode")
        , @NamedQuery(name = "GraduateDetailEntity.findByGraduateDecisionNumber", query = "SELECT g FROM GraduateDetailEntity g WHERE g.graduateDecisionNumber = :graduateDecisionNumber")
        , @NamedQuery(name = "GraduateDetailEntity.findByForm", query = "SELECT g FROM GraduateDetailEntity g WHERE g.form = :form")
        , @NamedQuery(name = "GraduateDetailEntity.findByDate", query = "SELECT g FROM GraduateDetailEntity g WHERE g.date = :date")
        , @NamedQuery(name = "GraduateDetailEntity.findByGraded", query = "SELECT g FROM GraduateDetailEntity g WHERE g.graded = :graded")})
public class GraduateDetailEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "StudentId")
    private Integer studentId;
    //số hiệu văn bằng
    @Column(name = "DiplomaCode")
    private String diplomaCode;
    //vào sổ cấp văn bằng, chứng chỉ số
    @Column(name = "CertificateCode")
    private String certificateCode;
    //số quyết định tốt nghiệp
    @Column(name = "GraduateDecisionNumber")
    private String graduateDecisionNumber;
    //hình thức đào tạo
    @Column(name = "Form")
    private String form;
    @Column(name = "Date")
    private String date;
    //xếp loại
    @Column(name = "Graded")
    private String graded;

    public GraduateDetailEntity() {
    }

    public GraduateDetailEntity(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getDiplomaCode() {
        return diplomaCode;
    }

    public void setDiplomaCode(String diplomaCode) {
        this.diplomaCode = diplomaCode;
    }

    public String getCertificateCode() {
        return certificateCode;
    }

    public void setCertificateCode(String certificateCode) {
        this.certificateCode = certificateCode;
    }

    public String getGraduateDecisionNumber() {
        return graduateDecisionNumber;
    }

    public void setGraduateDecisionNumber(String graduateDecisionNumber) {
        this.graduateDecisionNumber = graduateDecisionNumber;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGraded() {
        return graded;
    }

    public void setGraded(String graded) {
        this.graded = graded;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studentId != null ? studentId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GraduateDetailEntity)) {
            return false;
        }
        GraduateDetailEntity other = (GraduateDetailEntity) object;
        if ((this.studentId == null && other.studentId != null) || (this.studentId != null && !this.studentId.equals(other.studentId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.GraduateDetailEntity[ studentId=" + studentId + " ]";
    }

}
