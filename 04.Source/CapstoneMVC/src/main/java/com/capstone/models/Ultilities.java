package com.capstone.models;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.RealSemesterEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.services.IMarksService;
import com.capstone.services.ISubjectService;
import com.capstone.services.MarksServiceImpl;
import com.capstone.services.SubjectServiceImpl;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.lang.builder.CompareToBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Ultilities {

    public static List<String> notexist = new ArrayList<>();

    public static List<MarksEntity> SortMarkBySemester(List<MarksEntity> set) {
        ArrayList<String> seasons = new ArrayList<String>() {{
            add("spring");
            add("summer");
            add("fall");
        }};

        set.sort(Comparator.comparingInt(a -> {
            String removewhite = ((MarksEntity) a).getSemesterId().getSemester().replaceAll("\\s+", "");
            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
            Pattern pattern = Pattern.compile("^\\D*(\\d)");
            Matcher matcher = pattern.matcher(removeline);
            matcher.find();
            return Integer.parseInt(removeline.substring(matcher.start(1), removeline.length()));
        }).thenComparingInt(a -> {
            String removewhite = ((MarksEntity) a).getSemesterId().getSemester().replaceAll("\\s+", "");
            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
            Pattern pattern = Pattern.compile("^\\D*(\\d)");
            Matcher matcher = pattern.matcher(removeline);
            matcher.find();
            String season = removeline.substring(0, matcher.start(1)).toLowerCase();
            return seasons.indexOf(season);
        }).thenComparingInt(a -> {
            String semester = ((MarksEntity) a).getSemesterId().getSemester();
            return semester.indexOf("_");
        }));

        return set;
    }

    public static List<MarkModel> SortMarkModelBySemester(List<MarkModel> set) {
        ArrayList<String> seasons = new ArrayList<String>() {{
            add("spring");
            add("summer");
            add("fall");
        }};

        set.sort(Comparator.comparingInt(a -> {
            String removewhite = ((MarkModel) a).getSemester().replaceAll("\\s+", "");
            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
            Pattern pattern = Pattern.compile("^\\D*(\\d)");
            Matcher matcher = pattern.matcher(removeline);
            matcher.find();
            return Integer.parseInt(removeline.substring(matcher.start(1), removeline.length()));
        }).thenComparingInt(a -> {
            String removewhite = ((MarkModel) a).getSemester().replaceAll("\\s+", "");
            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
            Pattern pattern = Pattern.compile("^\\D*(\\d)");
            Matcher matcher = pattern.matcher(removeline);
            matcher.find();
            String season = removeline.substring(0, matcher.start(1)).toLowerCase();
            return seasons.indexOf(season);
        }).thenComparingInt(a -> {
            String semester = ((MarkModel) a).getSemester();
            return semester.indexOf("_");
        }).thenComparingLong(a -> {
            MarkModel en = (MarkModel) a;
            Date time = en.getStartDate();
            if (time == null) {
                time = Date.from(Instant.MIN);
            }
            return time.getTime();
        }));

        return set;
    }

    public static List<FailPrequisiteModel> FilterStudentPassedSubFailPrequisite(List<MarksEntity> list, String subId, List<String> prequisiteRow, int mark) {
        IMarksService marksService = new MarksServiceImpl();
        List<FailPrequisiteModel> result = new ArrayList<>();
        Table<String, String, List<MarksEntity>> map = HashBasedTable.create();
        list = Ultilities.SortMarkBySemester(list.stream().filter(c -> !c.getStatus().toLowerCase().contains("studying")).collect(Collectors.toList()));
        if (!list.isEmpty()) {
            for (MarksEntity m : list) {
                if (map.get(m.getStudentId().getRollNumber(), m.getSubjectMarkComponentId().getSubjectId().getId()) == null) {
                    List<MarksEntity> newMarkList = new ArrayList<>();
                    newMarkList.add(m);
                    map.put(m.getStudentId().getRollNumber(), m.getSubjectMarkComponentId().getSubjectId().getId(), newMarkList);
                } else {
                    map.get(m.getStudentId().getRollNumber(), m.getSubjectMarkComponentId().getSubjectId().getId()).add(m);
                }
            }

            Set<String> studentIds = map.rowKeySet();
            for (String studentId : studentIds) {
                Map<String, List<MarksEntity>> subject = map.row(studentId);
                if (subject.get(subId) != null && !subject.get(subId).isEmpty()) {
                    List<MarksEntity> markList = SortMarkBySemester(subject.get(subId));
                    for (MarksEntity m : markList) {
                        if (m.getStatus().toLowerCase().contains("pass") || m.getStatus().toLowerCase().contains("exempt")) {

                            int totalFail = 0;
                            FailPrequisiteModel failedRow = null;

                            for (String row : prequisiteRow) {

                                boolean isPass = false;

                                String[] cell = row.trim().split(",");
                                for (String prequisite : cell) {
                                    prequisite = prequisite.trim();

                                    // HANDLE LOGIC HERE
                                    if (subject.get(prequisite) != null && !subject.get(prequisite).isEmpty()) {
                                        List<MarksEntity> g = SortMarkBySemester(subject.get(prequisite));
                                        MarksEntity tmp = null;
                                        for (MarksEntity k2 : g) {
                                            tmp = k2;
                                            if (k2.getAverageMark() >= mark || k2.getStatus().toLowerCase().contains("exempt")) {
                                                isPass = true;
                                                break;
                                            }
                                        }

                                        if (!isPass) {
                                            failedRow = new FailPrequisiteModel(tmp, m.getSubjectMarkComponentId().getSubjectId().getId());
//                                            break;

                                            for (SubjectEntity replace : tmp.getSubjectMarkComponentId().getSubjectId().getSubjectEntityList()) {
                                                List<MarksEntity> replaced = marksService.getAllMarksByStudentAndSubject(tmp.getStudentId().getId(), replace.getId(), "0");
                                                for (MarksEntity marks : replaced) {
                                                    tmp = marks;
                                                    if (marks.getStatus().toLowerCase().contains("pass") || marks.getStatus().toLowerCase().contains("exempt")) {
                                                        isPass = true;
                                                        break;
                                                    }
                                                }

                                                if (!isPass) {
                                                    failedRow = new FailPrequisiteModel(tmp, m.getSubjectMarkComponentId().getSubjectId().getId());
                                                }
                                            }
                                        }
                                    }
                                    //////////////////////

                                }

                                if (!isPass) {
                                    totalFail++;
                                }
                            }

                            if (totalFail == prequisiteRow.size()) {
                                if (failedRow != null) result.add(failedRow);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public static Connection getConnection() {
        String connectionString = "jdbc:sqlserver://localhost:1433;database=CapstoneProject";
        Connection connection = null;
        String username = "sa";
        String password = "sa";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(connectionString, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    public static List<RealSemesterEntity> SortSemesters(List<RealSemesterEntity> set) {
        ArrayList<String> seasons = new ArrayList<String>() {{
            add("spring");
            add("summer");
            add("fall");
        }};

        set.sort(Comparator.comparingInt(a -> {
            String removewhite = ((RealSemesterEntity) a).getSemester().replaceAll("\\s+", "");
            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
            Pattern pattern = Pattern.compile("^\\D*(\\d)");
            Matcher matcher = pattern.matcher(removeline);
            matcher.find();
            return Integer.parseInt(removeline.substring(matcher.start(1), removeline.length()));
        }).thenComparingInt(a -> {
            String removewhite = ((RealSemesterEntity) a).getSemester().replaceAll("\\s+", "");
            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
            Pattern pattern = Pattern.compile("^\\D*(\\d)");
            Matcher matcher = pattern.matcher(removeline);
            matcher.find();
            String season = removeline.substring(0, matcher.start(1)).toLowerCase();
            return seasons.indexOf(season);
        }).thenComparingInt(a -> {
            String semester = ((RealSemesterEntity) a).getSemester();
            return semester.indexOf("_");
        }));

        return set;
    }
}
