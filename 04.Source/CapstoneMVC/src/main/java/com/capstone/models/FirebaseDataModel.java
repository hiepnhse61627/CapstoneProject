package com.capstone.models;

import java.util.List;

public class FirebaseDataModel {
    List<ScheduleModel> newScheduleList;

    public List<ScheduleModel> getNewScheduleList() {
        return newScheduleList;
    }

    public void setNewScheduleList(List<ScheduleModel> newScheduleList) {
        this.newScheduleList = newScheduleList;
    }
}
