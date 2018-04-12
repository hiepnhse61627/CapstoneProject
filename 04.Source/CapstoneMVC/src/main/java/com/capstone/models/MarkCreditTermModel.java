package com.capstone.models;

import com.capstone.entities.MarksEntity;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "markDetail", propOrder = {"mark", "credit","term"})
@XmlRootElement()
public class MarkCreditTermModel {

    @XmlElement(required = true)
    private MarksEntity mark;
    @XmlElement(required = true)
    private int credit;
    @XmlElement(required = true)
    private Double term;

    public MarkCreditTermModel() {
    }

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
