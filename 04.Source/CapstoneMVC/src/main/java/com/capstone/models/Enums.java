package com.capstone.models;

public class Enums {

    public enum Gender {
        FEMALE(false, "Nữ"),
        MALE(true, "Nam");

        private final boolean value;
        private final String name;

        private Gender(final boolean value, final String name) {
            this.value = value;
            this.name = name;
        }

        public boolean getValue() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum MarkComponent {
        QUIZ("QUIZ"),
        ASSIGNMENT("ASSIGNMENT"),
        PRESENTATION("PRESENTATION"),
        MID_TERM("MID-TERM"),
        FINAL("FINAL"),
        AVERAGE("AVERAGE");

        private final String value;

        private MarkComponent(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

}
