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

}
