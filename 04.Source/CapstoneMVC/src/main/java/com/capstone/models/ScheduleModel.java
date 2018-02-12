package com.capstone.models;

import com.capstone.entities.DaySlotEntity;
import com.capstone.entities.EmployeeEntity;
import com.capstone.entities.RoomEntity;

import java.util.List;

public class ScheduleModel {
    private String date;
    private String room;
    private String courseName;
    private String slot;
    private String endTime;
    private String startTime;
    private String lecture;

    public String getLecture() {
        return lecture;
    }

    public void setLecture(String lecture) {
        this.lecture = lecture;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String couresName) {
        this.courseName = couresName;
    }
}

