package com.capstone.models;

import com.capstone.entities.MarksEntity;

public class MarkCreditTermModel {
    private MarksEntity mark;
    private int credit;
    private Double term;

    public MarkCreditTermModel(MarksEntity mark, int credit, Double term) {
        this.mark = mark;
        this.credit = credit;
        this.term = term;
    }

    public MarksEntity getMark() {
        return mark;
    }

    public void setMark(MarksEntity mark) {
        this.mark = mark;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public Double getTerm() {
        return term;
    }

    public void setTerm(Double term) {
        this.term = term;
    }

}
