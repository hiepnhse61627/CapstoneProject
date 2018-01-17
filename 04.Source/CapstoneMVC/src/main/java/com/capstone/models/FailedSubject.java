package com.capstone.models;

public class FailedSubject {
    private String status;
    private String semester;
    private boolean redo=false;


    public FailedSubject(String status, String semester) {
        this.status = status;
        this.semester = semester;
    }

    public boolean isRedo() {
        return redo;
    }

    public void setRedo(boolean redo) {
        this.redo = redo;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
