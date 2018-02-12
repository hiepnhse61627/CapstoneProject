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
@Table(name = "Room")
@NamedQueries({
        @NamedQuery(name = "RoomEntity.findAll", query = "SELECT r FROM RoomEntity r"),
        @NamedQuery(name = "RoomEntity.findById", query = "SELECT r FROM RoomEntity r WHERE r.id = :id"),
        @NamedQuery(name = "RoomEntity.findByName", query = "SELECT r FROM RoomEntity r WHERE r.name = :name"),
        @NamedQuery(name = "RoomEntity.findByIsAvailable", query = "SELECT r FROM RoomEntity r WHERE r.isAvailable = :isAvailable"),
        @NamedQuery(name = "RoomEntity.findByCapacity", query = "SELECT r FROM RoomEntity r WHERE r.capacity = :capacity"),
        @NamedQuery(name = "RoomEntity.findByNote", query = "SELECT r FROM RoomEntity r WHERE r.note = :note")})
public class RoomEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Name")
    private String name;
    @Column(name = "IsAvailable")
    private Boolean isAvailable;
    @Column(name = "Capacity")
    private Integer capacity;
    @Column(name = "Note")
    private String note;
    @OneToMany(mappedBy = "roomId")
    private List<ScheduleEntity> scheduleEntityList;

    public RoomEntity() {
    }

    public RoomEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<ScheduleEntity> getScheduleEntityList() {
        return scheduleEntityList;
    }

    public void setScheduleEntityList(List<ScheduleEntity> scheduleEntityList) {
        this.scheduleEntityList = scheduleEntityList;
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
        if (!(object instanceof RoomEntity)) {
            return false;
        }
        RoomEntity other = (RoomEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication93.RoomEntity[ id=" + id + " ]";
    }

}
