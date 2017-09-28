package com.capstone.models;

import com.capstone.entities.MarksEntity;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import javax.persistence.Persistence;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ultilities {
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
        }).thenComparingLong(a -> {
            MarksEntity en = (MarksEntity) a;
            Timestamp time = en.getCourseId().getStartDate();
            if (time == null) {
                time = Timestamp.valueOf("1970-1-1 00:00:00");
                time.setTime(0);
            }
            return time.getTime();
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
        }));

        return set;
    }

    public static List<MarksEntity> FilterStudentPassedSubFailPrequisite(List<MarksEntity> list, String subId, String prequisiteId) {
        List<MarksEntity> result = new ArrayList<>();
        Table<String, String, List<MarksEntity>> map = HashBasedTable.create();
        if (!list.isEmpty()) {
            for (MarksEntity m : list) {
                if (map.get(m.getStudentId().getRollNumber(), m.getSubjectId().getSubjectId()) == null) {
                    List<MarksEntity> tmp = new ArrayList<>();
                    tmp.add(m);
                    map.put(m.getStudentId().getRollNumber(), m.getSubjectId().getSubjectId(), SortMarkBySemester(tmp));
                } else {
                    map.get(m.getStudentId().getRollNumber(), m.getSubjectId().getSubjectId()).add(m);
                    SortMarkBySemester(map.get(m.getStudentId().getRollNumber(), m.getSubjectId().getSubjectId()));
                }
            }

            Set<String> l = map.rowKeySet();
            for (String m : l) {
                Map<String, List<MarksEntity>> n = map.row(m);
                if (n.get(subId) != null && !n.get(subId).isEmpty()) {
                    List<MarksEntity> f = n.get(subId);
                    MarksEntity k = f.get(f.size() - 1);
                    if (k.getStatus().toLowerCase().contains("pass")) {
                        if (n.get(prequisiteId) != null && !n.get(prequisiteId).isEmpty()) {
                            f = n.get(subId);
                            k = f.get(f.size() - 1);
                            if (k.getStatus().toLowerCase().contains("fail")) {
                                result.add(k);
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
        String password = "123";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(connectionString, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }
}
