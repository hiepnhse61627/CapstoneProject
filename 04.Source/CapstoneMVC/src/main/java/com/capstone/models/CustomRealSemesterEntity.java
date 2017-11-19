package com.capstone.models;

import com.capstone.entities.RealSemesterEntity;

public class CustomRealSemesterEntity {

    private String link;
    private boolean finished;
    private RealSemesterEntity entity;

    public CustomRealSemesterEntity() {
    }

    public CustomRealSemesterEntity(String link, RealSemesterEntity entity, boolean finished) {
        this.link = link;
        this.entity = entity;
        this.finished = finished;
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

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
