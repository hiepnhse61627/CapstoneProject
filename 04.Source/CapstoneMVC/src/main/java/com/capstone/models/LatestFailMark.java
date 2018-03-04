package com.capstone.models;

import com.capstone.entities.MarksEntity;

public class LatestFailMark {
    public boolean isFailed;
    public MarksEntity latestFailedMark;

    public LatestFailMark() {
        this.isFailed = false;
        this.latestFailedMark = null;
    }

    public MarksEntity getLatestFailedMark() {
        return latestFailedMark;
    }

    public void setLatestFailedMark(MarksEntity latestFailedMark) {
        this.latestFailedMark = latestFailedMark;
    }

    public boolean isFailed() {

        return isFailed;
    }

    public void setFailed(boolean failed) {
        isFailed = failed;
    }
}
