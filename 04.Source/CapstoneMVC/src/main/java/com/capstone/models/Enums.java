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

    public enum MarkStatus {
        PASSED("Passed"),
        FAIL("Fail"),
        STUDYING("Studying"),
        NOT_START("NotStart"),
        IS_EXEMPT("IsExempt"),
        IS_SUSPENDED("IsSuspended"),
        IS_ATTENDANCE_FAIL("IsAttendanceFail");

        private final String value;

        private MarkStatus(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
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


    public enum StudentStatus {
        Graduated("G"),
        HOCDI("HD"),
        HOCLAI("HL"),
        BAOLUU("BL"), //bảo lưu
        CHO("CO"), //chờ
        THOIHOC("TH"), //thôi học
        TRANSFER("TF"); // từ cơ sở khác chuyển tới


        private final String value;

        private StudentStatus(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public enum SubjectType {
        NORMAL(0),
        OJT(1),
        CAPSTONE(2);

        private final int value;

        private SubjectType(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum SpecialTerm {
        CAPSTONETERM(9),
        OJTTERM(6);

        private final int value;

        SpecialTerm(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum GoogleAuthentication {
        CLIENTID("1024234376610-fa3r5s7db2g82ccqecolm6rbfskbv3ci.apps.googleusercontent.com"),
        CLIENTSECRET("Ub8YG4mCEA6mciFTauDkMsLg");

        private final String value;

        GoogleAuthentication(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum GraduateVariable {
        PROGRAM_ID("graduateProgramId"),
        SEMESTER_ID("graduateSemesterId"),
        GRADUATE_LIST("graduateListExport");

        private final String value;

        GraduateVariable(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
