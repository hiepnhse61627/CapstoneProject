/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities.fapEntities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author hoanglong
 */
@Embeddable
public class AttendancesEntityPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "ScheduleID")
    private int scheduleID;
    @Basic(optional = false)
    @Column(name = "RollNumber")
    private String rollNumber;

    public AttendancesEntityPK() {
    }

    public AttendancesEntityPK(int scheduleID, String rollNumber) {
        this.scheduleID = scheduleID;
        this.rollNumber = rollNumber;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) scheduleID;
        hash += (rollNumber != null ? rollNumber.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AttendancesEntityPK)) {
            return false;
        }
        AttendancesEntityPK other = (AttendancesEntityPK) object;
        if (this.scheduleID != other.scheduleID) {
            return false;
        }
        if ((this.rollNumber == null && other.rollNumber != null) || (this.rollNumber != null && !this.rollNumber.equals(other.rollNumber))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication106.AttendancesEntityPK[ scheduleID=" + scheduleID + ", rollNumber=" + rollNumber + " ]";
    }
    
}
