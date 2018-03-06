/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author hoanglong
 */
@Entity
@Table(name = "Schedule")
@NamedQueries({
    @NamedQuery(name = "ScheduleEntity.findAll", query = "SELECT s FROM ScheduleEntity s"),
    @NamedQuery(name = "ScheduleEntity.findById", query = "SELECT s FROM ScheduleEntity s WHERE s.id = :id")})
public class ScheduleEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn(name = "CourseId", referencedColumnName = "Id")
    @ManyToOne
    private CourseEntity courseId;
    @JoinColumn(name = "DateId", referencedColumnName = "Id")
    @ManyToOne
    private DaySlotEntity dateId;
    @JoinColumn(name = "EmpId", referencedColumnName = "Id")
    @ManyToOne
    private EmployeeEntity empId;
    @JoinColumn(name = "RoomId", referencedColumnName = "Id")
    @ManyToOne
    private RoomEntity roomId;
    @Column(name = "GroupName")
    private String groupName;
    @Column(name = "isActive")
    private boolean isActive;
    @Column(name = "parentScheduleId")
    private Integer parentScheduleId;

    public ScheduleEntity() {
    }

    public ScheduleEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CourseEntity getCourseId() {
        return courseId;
    }

    public void setCourseId(CourseEntity courseId) {
        this.courseId = courseId;
    }

    public DaySlotEntity getDateId() {
        return dateId;
    }

    public void setDateId(DaySlotEntity dateId) {
        this.dateId = dateId;
    }

    public EmployeeEntity getEmpId() {
        return empId;
    }

    public void setEmpId(EmployeeEntity empId) {
        this.empId = empId;
    }

    public RoomEntity getRoomId() {
        return roomId;
    }

    public void setRoomId(RoomEntity roomId) {
        this.roomId = roomId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getParentScheduleId() {
        return parentScheduleId;
    }

    public void setParentScheduleId(Integer parentScheduleId) {
        this.parentScheduleId = parentScheduleId;
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
        if (!(object instanceof ScheduleEntity)) {
            return false;
        }
        ScheduleEntity other = (ScheduleEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpaentitygen.ScheduleEntity[ id=" + id + " ]";
    }
    
}
