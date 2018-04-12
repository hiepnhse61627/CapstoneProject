package com.capstone.models;

import java.util.Comparator;

public class MarkCreditTermModelComparator implements Comparator<MarkCreditTermModel> {
    @Override
    public int compare(MarkCreditTermModel o1, MarkCreditTermModel o2) {
        return o1.getTerm().compareTo(o2.getTerm());
    }
}