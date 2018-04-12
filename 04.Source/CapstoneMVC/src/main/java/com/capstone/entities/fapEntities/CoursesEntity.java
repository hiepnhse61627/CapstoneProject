/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities.fapEntities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author hoanglong
 */
@Entity
@Table(name = "Courses")
@NamedQueries({
    @NamedQuery(name = "CoursesEntity.findAll", query = "SELECT c FROM CoursesEntity c"),
    @NamedQuery(name = "CoursesEntity.findByCourseID", query = "SELECT c FROM CoursesEntity c WHERE c.courseID = :courseID"),
    @NamedQuery(name = "CoursesEntity.findByCourseDetail", query = "SELECT c FROM CoursesEntity c WHERE c.courseDetail = :courseDetail"),
    @NamedQuery(name = "CoursesEntity.findByTermID", query = "SELECT c FROM CoursesEntity c WHERE c.termID = :termID"),
    @NamedQuery(name = "CoursesEntity.findByGroupName", query = "SELECT c FROM CoursesEntity c WHERE c.groupName = :groupName"),
    @NamedQuery(name = "CoursesEntity.findBySubjectID", query = "SELECT c FROM CoursesEntity c WHERE c.subjectID = :subjectID"),
    @NamedQuery(name = "CoursesEntity.findBySyllabusID", query = "SELECT c FROM CoursesEntity c WHERE c.syllabusID = :syllabusID"),
    @NamedQuery(name = "CoursesEntity.findByStartDate", query = "SELECT c FROM CoursesEntity c WHERE c.startDate = :startDate"),
    @NamedQuery(name = "CoursesEntity.findByEndDate", query = "SELECT c FROM CoursesEntity c WHERE c.endDate = :endDate"),
    @NamedQuery(name = "CoursesEntity.findByNumberOfSlots", query = "SELECT c FROM CoursesEntity c WHERE c.numberOfSlots = :numberOfSlots"),
    @NamedQuery(name = "CoursesEntity.findByIsBis", query = "SELECT c FROM CoursesEntity c WHERE c.isBis = :isBis"),
    @NamedQuery(name = "CoursesEntity.findByIsTemp", query = "SELECT c FROM CoursesEntity c WHERE c.isTemp = :isTemp"),
    @NamedQuery(name = "CoursesEntity.findBySlotTypeCode", query = "SELECT c FROM CoursesEntity c WHERE c.slotTypeCode = :slotTypeCode"),
    @NamedQuery(name = "CoursesEntity.findByIsOpen", query = "SELECT c FROM CoursesEntity c WHERE c.isOpen = :isOpen"),
    @NamedQuery(name = "CoursesEntity.findByIsInternation", query = "SELECT c FROM CoursesEntity c WHERE c.isInternation = :isInternation")})
public class CoursesEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CourseID")
    private Integer courseID;
    @Basic(optional = false)
    @Column(name = "CourseDetail")
    private String courseDetail;
    @Basic(optional = false)
    @Column(name = "TermID")
    private short termID;
    @Basic(optional = false)
    @Column(name = "GroupName")
    private String groupName;
    @Basic(optional = false)
    @Column(name = "SubjectID")
    private short subjectID;
    @Column(name = "SyllabusID")
    private Short syllabusID;
    @Basic(optional = false)
    @Column(name = "StartDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Basic(optional = false)
    @Column(name = "EndDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Basic(optional = false)
    @Column(name = "NumberOfSlots")
    private short numberOfSlots;
    @Basic(optional = false)
    @Column(name = "IsBis")
    private boolean isBis;
    @Basic(optional = false)
    @Column(name = "IsTemp")
    private boolean isTemp;
    @Column(name = "SlotTypeCode")
    private String slotTypeCode;
    @Basic(optional = false)
    @Column(name = "IsOpen")
    private boolean isOpen;
    @Column(name = "IsInternation")
    private Boolean isInternation;

    public CoursesEntity() {
    }

    public CoursesEntity(Integer courseID) {
        this.courseID = courseID;
    }

    public CoursesEntity(Integer courseID, String courseDetail, short termID, String groupName, short subjectID, Date startDate, Date endDate, short numberOfSlots, boolean isBis, boolean isTemp, boolean isOpen) {
        this.courseID = courseID;
        this.courseDetail = courseDetail;
        this.termID = termID;
        this.groupName = groupName;
        this.subjectID = subjectID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfSlots = numberOfSlots;
        this.isBis = isBis;
        this.isTemp = isTemp;
        this.isOpen = isOpen;
    }

    public Integer getCourseID() {
        return courseID;
    }

    public void setCourseID(Integer courseID) {
        this.courseID = courseID;
    }

    public String getCourseDetail() {
        return courseDetail;
    }

    public void setCourseDetail(String courseDetail) {
        this.courseDetail = courseDetail;
    }

    public short getTermID() {
        return termID;
    }

    public void setTermID(short termID) {
        this.termID = termID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public short getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(short subjectID) {
        this.subjectID = subjectID;
    }

    public Short getSyllabusID() {
        return syllabusID;
    }

    public void setSyllabusID(Short syllabusID) {
        this.syllabusID = syllabusID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public short getNumberOfSlots() {
        return numberOfSlots;
    }

    public void setNumberOfSlots(short numberOfSlots) {
        this.numberOfSlots = numberOfSlots;
    }

    public boolean getIsBis() {
        return isBis;
    }

    public void setIsBis(boolean isBis) {
        this.isBis = isBis;
    }

    public boolean getIsTemp() {
        return isTemp;
    }

    public void setIsTemp(boolean isTemp) {
        this.isTemp = isTemp;
    }

    public String getSlotTypeCode() {
        return slotTypeCode;
    }

    public void setSlotTypeCode(String slotTypeCode) {
        this.slotTypeCode = slotTypeCode;
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Boolean getIsInternation() {
        return isInternation;
    }

    public void setIsInternation(Boolean isInternation) {
        this.isInternation = isInternation;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (courseID != null ? courseID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CoursesEntity)) {
            return false;
        }
        CoursesEntity other = (CoursesEntity) object;
        if ((this.courseID == null && other.courseID != null) || (this.courseID != null && !this.courseID.equals(other.courseID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication106.CoursesEntity[ courseID=" + courseID + " ]";
    }
    
}
