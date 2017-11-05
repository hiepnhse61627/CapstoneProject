package com.capstone.models;

import java.util.List;

public class Suggestion {
    private boolean duchitieu;
    private List<List<String>> data;

    public Suggestion() {
    }

    public Suggestion(boolean duchitieu, List<List<String>> data) {
        this.duchitieu = duchitieu;
        this.data = data;
    }

    public boolean isDuchitieu() {
        return duchitieu;
    }

    public void setDuchitieu(boolean duchitieu) {
        this.duchitieu = duchitieu;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }
}
