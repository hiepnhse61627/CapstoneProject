package com.capstone.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ChangedSchedule")
@NamedQueries({
        @NamedQuery(name = "ChangedScheduleEntity.findAll", query = "SELECT c FROM ChangedScheduleEntity c"),
        @NamedQuery(name = "ChangedScheduleEntity.findByScheduleID", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.changedScheduleEntityPK.scheduleID = :scheduleID"),
        @NamedQuery(name = "ChangedScheduleEntity.findByChangedDate", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.changedScheduleEntityPK.changedDate = :changedDate"),
        @NamedQuery(name = "ChangedScheduleEntity.findByChanger", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.changer = :changer"),
        @NamedQuery(name = "ChangedScheduleEntity.findByReason", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.reason = :reason"),
        @NamedQuery(name = "ChangedScheduleEntity.findByComment", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.comment = :comment"),
        @NamedQuery(name = "ChangedScheduleEntity.findByTermId", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.termId = :termId"),
        @NamedQuery(name = "ChangedScheduleEntity.findByCourseId", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.courseId = :courseId"),
        @NamedQuery(name = "ChangedScheduleEntity.findBySubjectCode", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.subjectCode = :subjectCode"),
        @NamedQuery(name = "ChangedScheduleEntity.findByClassName", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.className = :className"),
        @NamedQuery(name = "ChangedScheduleEntity.findByFromLecturer", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.fromLecturer = :fromLecturer"),
        @NamedQuery(name = "ChangedScheduleEntity.findByToLecturer", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.toLecturer = :toLecturer"),
        @NamedQuery(name = "ChangedScheduleEntity.findByFromRoomNo", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.fromRoomNo = :fromRoomNo"),
        @NamedQuery(name = "ChangedScheduleEntity.findByToRoomNo", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.toRoomNo = :toRoomNo"),
        @NamedQuery(name = "ChangedScheduleEntity.findByFromDate", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.fromDate = :fromDate"),
        @NamedQuery(name = "ChangedScheduleEntity.findByToDate", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.toDate = :toDate"),
        @NamedQuery(name = "ChangedScheduleEntity.findByFromSlot", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.fromSlot = :fromSlot"),
        @NamedQuery(name = "ChangedScheduleEntity.findByToSlot", query = "SELECT c FROM ChangedScheduleEntity c WHERE c.toSlot = :toSlot")})
public class ChangedScheduleEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ChangedScheduleEntityPK changedScheduleEntityPK;
    @Basic(optional = false)
    @Column(name = "Changer")
    private String changer;
    @Basic(optional = false)
    @Column(name = "Reason")
    private String reason;
    @Column(name = "Comment")
    private String comment;
    @Column(name = "TermId")
    private Integer termId;
    @Column(name = "CourseId")
    private Integer courseId;
    @Column(name = "SubjectCode")
    private String subjectCode;
    @Column(name = "ClassName")
    private String className;
    @Column(name = "FromLecturer")
    private String fromLecturer;
    @Column(name = "ToLecturer")
    private String toLecturer;
    @Column(name = "FromRoomNo")
    private String fromRoomNo;
    @Column(name = "ToRoomNo")
    private String toRoomNo;
    @Column(name = "FromDate")
    private String fromDate;
    @Column(name = "ToDate")
    private String toDate;
    @Column(name = "FromSlot")
    private Integer fromSlot;
    @Column(name = "ToSlot")
    private Integer toSlot;

    public ChangedScheduleEntity() {
    }

    public ChangedScheduleEntity(ChangedScheduleEntityPK changedScheduleEntityPK) {
        this.changedScheduleEntityPK = changedScheduleEntityPK;
    }

    public ChangedScheduleEntity(ChangedScheduleEntityPK changedScheduleEntityPK, String changer, String reason) {
        this.changedScheduleEntityPK = changedScheduleEntityPK;
        this.changer = changer;
        this.reason = reason;
    }

    public ChangedScheduleEntity(int scheduleID, Date changedDate) {
        this.changedScheduleEntityPK = new ChangedScheduleEntityPK(scheduleID, changedDate);
    }

    public ChangedScheduleEntityPK getChangedScheduleEntityPK() {
        return changedScheduleEntityPK;
    }

    public void setChangedScheduleEntityPK(ChangedScheduleEntityPK changedScheduleEntityPK) {
        this.changedScheduleEntityPK = changedScheduleEntityPK;
    }

    public String getChanger() {
        return changer;
    }

    public void setChanger(String changer) {
        this.changer = changer;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getTermId() {
        return termId;
    }

    public void setTermId(Integer termId) {
        this.termId = termId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFromLecturer() {
        return fromLecturer;
    }

    public void setFromLecturer(String fromLecturer) {
        this.fromLecturer = fromLecturer;
    }

    public String getToLecturer() {
        return toLecturer;
    }

    public void setToLecturer(String toLecturer) {
        this.toLecturer = toLecturer;
    }

    public String getFromRoomNo() {
        return fromRoomNo;
    }

    public void setFromRoomNo(String fromRoomNo) {
        this.fromRoomNo = fromRoomNo;
    }

    public String getToRoomNo() {
        return toRoomNo;
    }

    public void setToRoomNo(String toRoomNo) {
        this.toRoomNo = toRoomNo;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Integer getFromSlot() {
        return fromSlot;
    }

    public void setFromSlot(Integer fromSlot) {
        this.fromSlot = fromSlot;
    }

    public Integer getToSlot() {
        return toSlot;
    }

    public void setToSlot(Integer toSlot) {
        this.toSlot = toSlot;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (changedScheduleEntityPK != null ? changedScheduleEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ChangedScheduleEntity)) {
            return false;
        }
        ChangedScheduleEntity other = (ChangedScheduleEntity) object;
        if ((this.changedScheduleEntityPK == null && other.changedScheduleEntityPK != null) || (this.changedScheduleEntityPK != null && !this.changedScheduleEntityPK.equals(other.changedScheduleEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication105.ChangedScheduleEntity[ changedScheduleEntityPK=" + changedScheduleEntityPK + " ]";
    }

}
