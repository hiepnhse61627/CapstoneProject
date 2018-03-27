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
@Table(name = "Schedules")
@NamedQueries({
    @NamedQuery(name = "SchedulesEntity.findAll", query = "SELECT s FROM SchedulesEntity s"),
    @NamedQuery(name = "SchedulesEntity.findByScheduleID", query = "SELECT s FROM SchedulesEntity s WHERE s.scheduleID = :scheduleID"),
    @NamedQuery(name = "SchedulesEntity.findByCourseID", query = "SELECT s FROM SchedulesEntity s WHERE s.courseID = :courseID"),
    @NamedQuery(name = "SchedulesEntity.findBySessionNo", query = "SELECT s FROM SchedulesEntity s WHERE s.sessionNo = :sessionNo"),
    @NamedQuery(name = "SchedulesEntity.findByLecturer", query = "SELECT s FROM SchedulesEntity s WHERE s.lecturer = :lecturer"),
    @NamedQuery(name = "SchedulesEntity.findByRoomNo", query = "SELECT s FROM SchedulesEntity s WHERE s.roomNo = :roomNo"),
    @NamedQuery(name = "SchedulesEntity.findByAreaID", query = "SELECT s FROM SchedulesEntity s WHERE s.areaID = :areaID"),
    @NamedQuery(name = "SchedulesEntity.findByDate", query = "SELECT s FROM SchedulesEntity s WHERE s.date = :date"),
    @NamedQuery(name = "SchedulesEntity.findBySlot", query = "SELECT s FROM SchedulesEntity s WHERE s.slot = :slot"),
    @NamedQuery(name = "SchedulesEntity.findByNote", query = "SELECT s FROM SchedulesEntity s WHERE s.note = :note"),
    @NamedQuery(name = "SchedulesEntity.findByBooker", query = "SELECT s FROM SchedulesEntity s WHERE s.booker = :booker"),
    @NamedQuery(name = "SchedulesEntity.findByRecordTime", query = "SELECT s FROM SchedulesEntity s WHERE s.recordTime = :recordTime")})
public class SchedulesEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ScheduleID")
    private Integer scheduleID;
    @Column(name = "CourseID")
    private Integer courseID;
    @Column(name = "SessionNo")
    private Short sessionNo;
    @Column(name = "Lecturer")
    private String lecturer;
    @Basic(optional = false)
    @Column(name = "RoomNo")
    private String roomNo;
    @Basic(optional = false)
    @Column(name = "AreaID")
    private short areaID;
    @Basic(optional = false)
    @Column(name = "Date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Basic(optional = false)
    @Column(name = "Slot")
    private short slot;
    @Column(name = "Note")
    private String note;
    @Column(name = "Booker")
    private String booker;
    @Column(name = "RecordTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date recordTime;

    public SchedulesEntity() {
    }

    public SchedulesEntity(Integer scheduleID) {
        this.scheduleID = scheduleID;
    }

    public SchedulesEntity(Integer scheduleID, String roomNo, short areaID, Date date, short slot) {
        this.scheduleID = scheduleID;
        this.roomNo = roomNo;
        this.areaID = areaID;
        this.date = date;
        this.slot = slot;
    }

    public Integer getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(Integer scheduleID) {
        this.scheduleID = scheduleID;
    }

    public Integer getCourseID() {
        return courseID;
    }

    public void setCourseID(Integer courseID) {
        this.courseID = courseID;
    }

    public Short getSessionNo() {
        return sessionNo;
    }

    public void setSessionNo(Short sessionNo) {
        this.sessionNo = sessionNo;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public short getAreaID() {
        return areaID;
    }

    public void setAreaID(short areaID) {
        this.areaID = areaID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public short getSlot() {
        return slot;
    }

    public void setSlot(short slot) {
        this.slot = slot;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBooker() {
        return booker;
    }

    public void setBooker(String booker) {
        this.booker = booker;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (scheduleID != null ? scheduleID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SchedulesEntity)) {
            return false;
        }
        SchedulesEntity other = (SchedulesEntity) object;
        if ((this.scheduleID == null && other.scheduleID != null) || (this.scheduleID != null && !this.scheduleID.equals(other.scheduleID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication106.SchedulesEntity[ scheduleID=" + scheduleID + " ]";
    }
    
}
