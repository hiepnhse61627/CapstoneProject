/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities.fapEntities;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author hoanglong
 */
@Entity
@Table(name = "Subjects")
@NamedQueries({
    @NamedQuery(name = "Subjects.findAll", query = "SELECT s FROM Subjects s"),
    @NamedQuery(name = "Subjects.findBySubjectID", query = "SELECT s FROM Subjects s WHERE s.subjectID = :subjectID"),
    @NamedQuery(name = "Subjects.findBySubjectCode", query = "SELECT s FROM Subjects s WHERE s.subjectCode = :subjectCode"),
    @NamedQuery(name = "Subjects.findByOldSubjectCode", query = "SELECT s FROM Subjects s WHERE s.oldSubjectCode = :oldSubjectCode"),
    @NamedQuery(name = "Subjects.findByShortName", query = "SELECT s FROM Subjects s WHERE s.shortName = :shortName"),
    @NamedQuery(name = "Subjects.findBySubjectName", query = "SELECT s FROM Subjects s WHERE s.subjectName = :subjectName"),
    @NamedQuery(name = "Subjects.findBySubjectGroup", query = "SELECT s FROM Subjects s WHERE s.subjectGroup = :subjectGroup"),
    @NamedQuery(name = "Subjects.findBySubjectV", query = "SELECT s FROM Subjects s WHERE s.subjectV = :subjectV"),
    @NamedQuery(name = "Subjects.findByTakeAttendance", query = "SELECT s FROM Subjects s WHERE s.takeAttendance = :takeAttendance"),
    @NamedQuery(name = "Subjects.findByReplacedBy", query = "SELECT s FROM Subjects s WHERE s.replacedBy = :replacedBy"),
    @NamedQuery(name = "Subjects.findByIsGraded", query = "SELECT s FROM Subjects s WHERE s.isGraded = :isGraded"),
    @NamedQuery(name = "Subjects.findByKeepCredits", query = "SELECT s FROM Subjects s WHERE s.keepCredits = :keepCredits"),
    @NamedQuery(name = "Subjects.findByNewestSubjectID", query = "SELECT s FROM Subjects s WHERE s.newestSubjectID = :newestSubjectID"),
    @NamedQuery(name = "Subjects.findByFee", query = "SELECT s FROM Subjects s WHERE s.fee = :fee"),
    @NamedQuery(name = "Subjects.findByIsBeforeOJT", query = "SELECT s FROM Subjects s WHERE s.isBeforeOJT = :isBeforeOJT"),
    @NamedQuery(name = "Subjects.findByFeatured", query = "SELECT s FROM Subjects s WHERE s.featured = :featured"),
    @NamedQuery(name = "Subjects.findByIsRequired", query = "SELECT s FROM Subjects s WHERE s.isRequired = :isRequired"),
    @NamedQuery(name = "Subjects.findByFeeIS", query = "SELECT s FROM Subjects s WHERE s.feeIS = :feeIS"),
    @NamedQuery(name = "Subjects.findByNumberOfStudent", query = "SELECT s FROM Subjects s WHERE s.numberOfStudent = :numberOfStudent")})
public class Subjects implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "SubjectID")
    private Short subjectID;
    @Column(name = "SubjectCode")
    private String subjectCode;
    @Column(name = "OldSubjectCode")
    private String oldSubjectCode;
    @Column(name = "ShortName")
    private String shortName;
    @Basic(optional = false)
    @Column(name = "SubjectName")
    private String subjectName;
    @Column(name = "SubjectGroup")
    private String subjectGroup;
    @Column(name = "SubjectV")
    private String subjectV;
    @Basic(optional = false)
    @Column(name = "TakeAttendance")
    private boolean takeAttendance;
    @Column(name = "ReplacedBy")
    private String replacedBy;
    @Basic(optional = false)
    @Column(name = "IsGraded")
    private boolean isGraded;
    @Basic(optional = false)
    @Column(name = "KeepCredits")
    private boolean keepCredits;
    @Column(name = "NewestSubjectID")
    private Short newestSubjectID;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "Fee")
    private BigDecimal fee;
    @Basic(optional = false)
    @Column(name = "IsBeforeOJT")
    private boolean isBeforeOJT;
    @Column(name = "Featured")
    private String featured;
    @Basic(optional = false)
    @Column(name = "IsRequired")
    private boolean isRequired;
    @Column(name = "FeeIS")
    private BigDecimal feeIS;
    @Column(name = "NumberOfStudent")
    private Integer numberOfStudent;

    public Subjects() {
    }

    public Subjects(Short subjectID) {
        this.subjectID = subjectID;
    }

    public Subjects(Short subjectID, String subjectName, boolean takeAttendance, boolean isGraded, boolean keepCredits, boolean isBeforeOJT, boolean isRequired) {
        this.subjectID = subjectID;
        this.subjectName = subjectName;
        this.takeAttendance = takeAttendance;
        this.isGraded = isGraded;
        this.keepCredits = keepCredits;
        this.isBeforeOJT = isBeforeOJT;
        this.isRequired = isRequired;
    }

    public Short getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(Short subjectID) {
        this.subjectID = subjectID;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getOldSubjectCode() {
        return oldSubjectCode;
    }

    public void setOldSubjectCode(String oldSubjectCode) {
        this.oldSubjectCode = oldSubjectCode;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(String subjectGroup) {
        this.subjectGroup = subjectGroup;
    }

    public String getSubjectV() {
        return subjectV;
    }

    public void setSubjectV(String subjectV) {
        this.subjectV = subjectV;
    }

    public boolean getTakeAttendance() {
        return takeAttendance;
    }

    public void setTakeAttendance(boolean takeAttendance) {
        this.takeAttendance = takeAttendance;
    }

    public String getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(String replacedBy) {
        this.replacedBy = replacedBy;
    }

    public boolean getIsGraded() {
        return isGraded;
    }

    public void setIsGraded(boolean isGraded) {
        this.isGraded = isGraded;
    }

    public boolean getKeepCredits() {
        return keepCredits;
    }

    public void setKeepCredits(boolean keepCredits) {
        this.keepCredits = keepCredits;
    }

    public Short getNewestSubjectID() {
        return newestSubjectID;
    }

    public void setNewestSubjectID(Short newestSubjectID) {
        this.newestSubjectID = newestSubjectID;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public boolean getIsBeforeOJT() {
        return isBeforeOJT;
    }

    public void setIsBeforeOJT(boolean isBeforeOJT) {
        this.isBeforeOJT = isBeforeOJT;
    }

    public String getFeatured() {
        return featured;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }

    public boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public BigDecimal getFeeIS() {
        return feeIS;
    }

    public void setFeeIS(BigDecimal feeIS) {
        this.feeIS = feeIS;
    }

    public Integer getNumberOfStudent() {
        return numberOfStudent;
    }

    public void setNumberOfStudent(Integer numberOfStudent) {
        this.numberOfStudent = numberOfStudent;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (subjectID != null ? subjectID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Subjects)) {
            return false;
        }
        Subjects other = (Subjects) object;
        if ((this.subjectID == null && other.subjectID != null) || (this.subjectID != null && !this.subjectID.equals(other.subjectID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication106.Subjects[ subjectID=" + subjectID + " ]";
    }
    
}
