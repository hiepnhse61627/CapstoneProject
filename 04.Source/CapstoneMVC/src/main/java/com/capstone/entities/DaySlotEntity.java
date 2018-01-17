/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author hoanglong
 */
@Entity
@Table(name = "Day_Slot")
@NamedQueries({
    @NamedQuery(name = "DaySlotEntity.findAll", query = "SELECT d FROM DaySlotEntity d"),
    @NamedQuery(name = "DaySlotEntity.findById", query = "SELECT d FROM DaySlotEntity d WHERE d.id = :id"),
    @NamedQuery(name = "DaySlotEntity.findByDate", query = "SELECT d FROM DaySlotEntity d WHERE d.date = :date")})
public class DaySlotEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @JoinColumn(name = "SlotId", referencedColumnName = "Id")
    @ManyToOne
    private SlotEntity slotId;
    @OneToMany(mappedBy = "dateId")
    private List<ScheduleEntity> scheduleEntityCollection;

    public DaySlotEntity() {
    }

    public DaySlotEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public SlotEntity getSlotId() {
        return slotId;
    }

    public void setSlotId(SlotEntity slotId) {
        this.slotId = slotId;
    }

    public List<ScheduleEntity> getScheduleEntityCollection() {
        return scheduleEntityCollection;
    }

    public void setScheduleEntityCollection(List<ScheduleEntity> scheduleEntityCollection) {
        this.scheduleEntityCollection = scheduleEntityCollection;
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
        if (!(object instanceof DaySlotEntity)) {
            return false;
        }
        DaySlotEntity other = (DaySlotEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpaentitygen.DaySlotEntity[ id=" + id + " ]";
    }
    
}
