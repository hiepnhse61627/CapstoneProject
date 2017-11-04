package com.capstone.enums;

public enum SubjectTypeEnum {
    OJT(1),
    Capstone(2),
    Graduate(3);

    private int Id;

    SubjectTypeEnum(int Id) {
        this.Id = Id;
    }

    public int getId() {
        return Id;
    }
}
