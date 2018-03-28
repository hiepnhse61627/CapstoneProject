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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "Attendances")
@NamedQueries({
    @NamedQuery(name = "AttendancesEntity.findAll", query = "SELECT a FROM AttendancesEntity a"),
    @NamedQuery(name = "AttendancesEntity.findByScheduleID", query = "SELECT a FROM AttendancesEntity a WHERE a.attendancesEntityPK.scheduleID = :scheduleID"),
    @NamedQuery(name = "AttendancesEntity.findByRollNumber", query = "SELECT a FROM AttendancesEntity a WHERE a.attendancesEntityPK.rollNumber = :rollNumber"),
    @NamedQuery(name = "AttendancesEntity.findByStatus", query = "SELECT a FROM AttendancesEntity a WHERE a.status = :status"),
    @NamedQuery(name = "AttendancesEntity.findByComment", query = "SELECT a FROM AttendancesEntity a WHERE a.comment = :comment"),
    @NamedQuery(name = "AttendancesEntity.findByTaker", query = "SELECT a FROM AttendancesEntity a WHERE a.taker = :taker"),
    @NamedQuery(name = "AttendancesEntity.findByRecordTime", query = "SELECT a FROM AttendancesEntity a WHERE a.recordTime = :recordTime")})
public class AttendancesEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AttendancesEntityPK attendancesEntityPK;
    @Basic(optional = false)
    @Column(name = "Status")
    private boolean status;
    @Column(name = "Comment")
    private String comment;
    @Basic(optional = false)
    @Column(name = "Taker")
    private String taker;
    @Basic(optional = false)
    @Column(name = "RecordTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date recordTime;

    public AttendancesEntity() {
    }

    public AttendancesEntity(AttendancesEntityPK attendancesEntityPK) {
        this.attendancesEntityPK = attendancesEntityPK;
    }

    public AttendancesEntity(AttendancesEntityPK attendancesEntityPK, boolean status, String taker, Date recordTime) {
        this.attendancesEntityPK = attendancesEntityPK;
        this.status = status;
        this.taker = taker;
        this.recordTime = recordTime;
    }

    public AttendancesEntity(int scheduleID, String rollNumber) {
        this.attendancesEntityPK = new AttendancesEntityPK(scheduleID, rollNumber);
    }

    public AttendancesEntityPK getAttendancesEntityPK() {
        return attendancesEntityPK;
    }

    public void setAttendancesEntityPK(AttendancesEntityPK attendancesEntityPK) {
        this.attendancesEntityPK = attendancesEntityPK;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTaker() {
        return taker;
    }

    public void setTaker(String taker) {
        this.taker = taker;
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
        hash += (attendancesEntityPK != null ? attendancesEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AttendancesEntity)) {
            return false;
        }
        AttendancesEntity other = (AttendancesEntity) object;
        if ((this.attendancesEntityPK == null && other.attendancesEntityPK != null) || (this.attendancesEntityPK != null && !this.attendancesEntityPK.equals(other.attendancesEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication106.AttendancesEntity[ attendancesEntityPK=" + attendancesEntityPK + " ]";
    }
    
}
