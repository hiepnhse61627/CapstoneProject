package com.capstone.entities.fapEntities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Embeddable
public class ChangedScheduleEntityPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "ScheduleID")
    private int scheduleID;
    @Basic(optional = false)
    @Column(name = "ChangedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date changedDate;

    public ChangedScheduleEntityPK() {
    }

    public ChangedScheduleEntityPK(int scheduleID, Date changedDate) {
        this.scheduleID = scheduleID;
        this.changedDate = changedDate;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    public Date getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(Date changedDate) {
        this.changedDate = changedDate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) scheduleID;
        hash += (changedDate != null ? changedDate.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ChangedScheduleEntityPK)) {
            return false;
        }
        ChangedScheduleEntityPK other = (ChangedScheduleEntityPK) object;
        if (this.scheduleID != other.scheduleID) {
            return false;
        }
        if ((this.changedDate == null && other.changedDate != null) || (this.changedDate != null && !this.changedDate.equals(other.changedDate))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication105.ChangedScheduleEntityPK[ scheduleID=" + scheduleID + ", changedDate=" + changedDate + " ]";
    }

}
