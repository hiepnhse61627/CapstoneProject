package com.capstone.models;

import com.capstone.entities.RealSemesterEntity;

public class CustomRealSemesterEntity {

    private String link;
    private RealSemesterEntity entity;

    public CustomRealSemesterEntity() {
    }

    public CustomRealSemesterEntity(String link, RealSemesterEntity entity) {
        this.link = link;
        this.entity = entity;
    }

    public RealSemesterEntity getEntity() {
        return entity;
    }

    public void setEntity(RealSemesterEntity entity) {
        this.entity = entity;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
