package com.capstone.models;

import com.capstone.entities.ScheduleEntity;

import java.util.List;

public class FireBaseMessagingModel {
    String to;
    NotificationModel notification;
    FirebaseDataModel data;
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public NotificationModel getNotification() {
        return notification;
    }

    public void setNotification(NotificationModel notification) {
        this.notification = notification;
    }

    public FirebaseDataModel getData() {
        return data;
    }

    public void setData(FirebaseDataModel data) {
        this.data = data;
    }
}
