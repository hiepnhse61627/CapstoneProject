package com.capstone.models;

import java.util.List;

public class FirebaseDataModel {
    List<ScheduleModel> newScheduleList;
    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ScheduleModel> getNewScheduleList() {
        return newScheduleList;
    }

    public void setNewScheduleList(List<ScheduleModel> newScheduleList) {
        this.newScheduleList = newScheduleList;
    }
}
