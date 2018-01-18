/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author hoanglong
 */
@Entity
@Table(name = "Slot")
@NamedQueries({
    @NamedQuery(name = "SlotEntity.findAll", query = "SELECT s FROM SlotEntity s"),
    @NamedQuery(name = "SlotEntity.findById", query = "SELECT s FROM SlotEntity s WHERE s.id = :id"),
    @NamedQuery(name = "SlotEntity.findBySlotName", query = "SELECT s FROM SlotEntity s WHERE s.slotName = :slotName"),
    @NamedQuery(name = "SlotEntity.findByStartTime", query = "SELECT s FROM SlotEntity s WHERE s.startTime = :startTime"),
    @NamedQuery(name = "SlotEntity.findByEndTime", query = "SELECT s FROM SlotEntity s WHERE s.endTime = :endTime")})
public class SlotEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "SlotName")
    private String slotName;
    @Column(name = "StartTime")
    private String startTime;
    @Column(name = "EndTime")
    private String endTime;
    @OneToMany(mappedBy = "slotId")
    private List<DaySlotEntity> daySlotEntityCollection;

    public SlotEntity() {
    }

    public SlotEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<DaySlotEntity> getDaySlotEntityCollection() {
        return daySlotEntityCollection;
    }

    public void setDaySlotEntityCollection(List<DaySlotEntity> daySlotEntityCollection) {
        this.daySlotEntityCollection = daySlotEntityCollection;
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
        if (!(object instanceof SlotEntity)) {
            return false;
        }
        SlotEntity other = (SlotEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpaentitygen.SlotEntity[ id=" + id + " ]";
    }
    
}
