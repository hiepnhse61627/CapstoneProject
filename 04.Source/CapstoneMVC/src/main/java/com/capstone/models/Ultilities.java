package com.capstone.models;

import com.capstone.entities.*;
import com.capstone.services.IMarksService;
import com.capstone.services.MarksServiceImpl;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Ultilities {

    public static List<String> notexist = new ArrayList<>();

    public static List<MarksEntity> FilterStudentsOnlyPassAndFail(List<MarksEntity> set) {
//        ArrayList<String> seasons = new ArrayList<String>() {{
//            add("spring");
//            add("summer");
//            add("fall");
//        }};

        List<MarksEntity> newSet = set.stream()
                .filter(c -> !c.getStatus().trim().toLowerCase().contains("study") &&
                                !c.getStatus().trim().toLowerCase().contains("start") &&
                                !c.getStatus().trim().toLowerCase().contains(("diem")))
                .collect(Collectors.toList());

//        set.sort(Comparator.comparingInt(a -> {
//            String removewhite = ((MarksEntity) a).getSemesterId().getSemester().replaceAll("\\s+", "");
//            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
//            Pattern pattern = Pattern.compile("^\\D*(\\d)");
//            Matcher matcher = pattern.matcher(removeline);
//            matcher.find();
//            return Integer.parseInt(removeline.substring(matcher.start(1), removeline.length()));
//        }).thenComparingInt(a -> {
//            String removewhite = ((MarksEntity) a).getSemesterId().getSemester().replaceAll("\\s+", "");
//            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
//            Pattern pattern = Pattern.compile("^\\D*(\\d)");
//            Matcher matcher = pattern.matcher(removeline);
//            matcher.find();
//            String season = removeline.substring(0, matcher.start(1)).toLowerCase();
//            return seasons.indexOf(season);
//        }).thenComparingInt(a -> {
//            String semester = ((MarksEntity) a).getSemesterId().getSemester();
//            return semester.indexOf("_");
//        }));

        return newSet;
    }

//    public static List<MarkModel> SortMarkModelBySemester(List<MarkModel> set) {
//        ArrayList<String> seasons = new ArrayList<String>() {{
//            add("spring");
//            add("summer");
//            add("fall");
//        }};
//
//        set.sort(Comparator.comparingInt(a -> {
//            String removewhite = ((MarkModel) a).getSemester().replaceAll("\\s+", "");
//            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
//            Pattern pattern = Pattern.compile("^\\D*(\\d)");
//            Matcher matcher = pattern.matcher(removeline);
//            matcher.find();
//            return Integer.parseInt(removeline.substring(matcher.start(1), removeline.length()));
//        }).thenComparingInt(a -> {
//            String removewhite = ((MarkModel) a).getSemester().replaceAll("\\s+", "");
//            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
//            Pattern pattern = Pattern.compile("^\\D*(\\d)");
//            Matcher matcher = pattern.matcher(removeline);
//            matcher.find();
//            String season = removeline.substring(0, matcher.start(1)).toLowerCase();
//            return seasons.indexOf(season);
//        }).thenComparingInt(a -> {
//            String semester = ((MarkModel) a).getSemester();
//            return semester.indexOf("_");
//        }).thenComparingLong(a -> {
//            MarkModel en = (MarkModel) a;
//            Date time = en.getStartDate();
//            if (time == null) {
//                time = Date.from(Instant.MIN);
//            }
//            return time.getTime();
//        }));

//        return set;
//    }

    public static List<FailPrequisiteModel> FilterStudentPassedSubFailPrequisite(List<MarksEntity> list, String subId, List<String> prequisiteRow, int mark) {
        IMarksService marksService = new MarksServiceImpl();

        List<MarksEntity> newList = FilterStudentsOnlyPassAndFail(list);

        List<FailPrequisiteModel> result = new ArrayList<>();
        Table<String, String, List<MarksEntity>> map = HashBasedTable.create();
        list = Ultilities.FilterStudentsOnlyPassAndFail(list.stream().filter(c -> !c.getStatus().toLowerCase().contains("studying")).collect(Collectors.toList()));
        if (!list.isEmpty()) {
            for (MarksEntity m : newList) {
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
//                    List<MarksEntity> markList = FilterStudentsOnlyPassAndFail(subject.get(subId));
                    for (MarksEntity m : subject.get(subId)) {
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
                                        List<MarksEntity> g = FilterStudentsOnlyPassAndFail(subject.get(prequisite));
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
//        String connectionString = "jdbc:sqlserver://localhost:1433;database=CapstoneProject";
        Connection connection = null;
//        String username = "sa";
//        String password = "sa";
//
//        try {
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            connection = DriverManager.getConnection(connectionString, username, password);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = fac.createEntityManager();
            em.getTransaction().begin();
            connection = em.unwrap(java.sql.Connection.class);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" -- REVERT USING LEGACY CONNECTION");

            String connectionString = "jdbc:sqlserver://localhost:1433;database=CapstoneProject";
            String username = "sa";
            String password = "sa";

            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(connectionString, username, password);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return connection;
    }

    public static List<RealSemesterEntity> SortSemesters(List<RealSemesterEntity> set) {
        ArrayList<String> seasons = new ArrayList<String>() {{
            add("spring");
            add("summer");
            add("fall");
            add("n/a");
        }};

        set.sort(Comparator.comparingInt(a -> {
            if (((RealSemesterEntity) a).getSemester().equalsIgnoreCase("n/a")) return 0;
            String removewhite = ((RealSemesterEntity) a).getSemester().replaceAll("\\s+", "");
            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
            Pattern pattern = Pattern.compile("^\\D*(\\d)");
            Matcher matcher = pattern.matcher(removeline);
            matcher.find();
            return Integer.parseInt(removeline.substring(matcher.start(1), removeline.length()));
        }).thenComparingInt(a -> {
            if (((RealSemesterEntity) a).getSemester().equalsIgnoreCase("n/a")) return seasons.indexOf("n/a");
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

    public static List<String> SortSemestersString(List<String> set) {
        ArrayList<String> seasons = new ArrayList<String>() {{
            add("spring");
            add("summer");
            add("fall");
            add("n/a");
        }};

        set.sort(Comparator.comparingInt(a -> {
            if (a.toString().equalsIgnoreCase("n/a")) return 0;
            String removewhite = a.toString().replaceAll("\\s+", "");
            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
            Pattern pattern = Pattern.compile("^\\D*(\\d)");
            Matcher matcher = pattern.matcher(removeline);
            matcher.find();
            return Integer.parseInt(removeline.substring(matcher.start(1), removeline.length()));
        }).thenComparingInt(a -> {
            if (a.toString().equalsIgnoreCase("n/a")) return seasons.indexOf("n/a");
            String removewhite = a.toString().replaceAll("\\s+", "");
            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
            Pattern pattern = Pattern.compile("^\\D*(\\d)");
            Matcher matcher = pattern.matcher(removeline);
            matcher.find();
            String season = removeline.substring(0, matcher.start(1)).toLowerCase();
            return seasons.indexOf(season);
        }).thenComparingInt(a -> {
            String semester = a.toString();
            return semester.indexOf("_");
        }));

        return set;
    }

    public static RealSemesterEntity getSemesterByTerm(int studentId, int term) {
        EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = fac.createEntityManager();
        StudentEntity student = em.find(StudentEntity.class, studentId);
        List<DocumentStudentEntity> list = student.getDocumentStudentEntityList();
        list.sort(Comparator.comparingLong(a -> {
            if (a.getCreatedDate() == null) return 0;
            else return a.getCreatedDate().getTime();
        }));

        List<SubjectCurriculumEntity> listCur = list.get(list.size() - 1).getCurriculumId().getSubjectCurriculumEntityList();
        List<SubjectCurriculumEntity> newList = new ArrayList<>();
        for (SubjectCurriculumEntity tmp : listCur) {
            if (tmp.getTermNumber() == term) {
                newList.add(tmp);
            }
        }
        List<String> subjectList = new ArrayList<>();
        newList.forEach(c -> {
            if (!subjectList.contains(c.getSubjectId().getId())) subjectList.add(c.getSubjectId().getId());
        });

        List<RealSemesterEntity> listSemester = new ArrayList<>();
        if (subjectList != null && !subjectList.isEmpty()) {
            TypedQuery<MarksEntity> query = em.createQuery("SELECT a FROM MarksEntity a WHERE a.studentId.id = :stu AND a.subjectMarkComponentId.subjectId.id IN :list", MarksEntity.class);
            query.setParameter("stu", studentId);
            query.setParameter("list", subjectList);
            List<MarksEntity> listMark = query.getResultList();
            if (listMark != null && !listMark.isEmpty()) {
                for (MarksEntity tmp : listMark) {
                    listSemester.add(tmp.getSemesterId());
                }
            }
        }

        if (listSemester.isEmpty()) return null;
        else return SortSemesters(listSemester).get(0);
    }

    public static List<MarksEntity> FilterListFailStudent(List<MarksEntity> list) {
        IMarksService marksService = new MarksServiceImpl();

        Table<String, String, List<MarksEntity>> map = HashBasedTable.create();
        for (MarksEntity m : list) {
            if (map.get(m.getStudentId().getRollNumber(), m.getSubjectMarkComponentId().getSubjectId().getId()) == null) {
                List<MarksEntity> newMarkList = new ArrayList<>();
                newMarkList.add(m);
                map.put(m.getStudentId().getRollNumber(), m.getSubjectMarkComponentId().getSubjectId().getId(), newMarkList);
            } else {
                map.get(m.getStudentId().getRollNumber(), m.getSubjectMarkComponentId().getSubjectId().getId()).add(m);
            }
        }

        List<MarksEntity> resultList = new ArrayList<>();
        Set<String> students = map.rowKeySet();
        for (String rollnumber : students) {
            Map<String, List<MarksEntity>> row = map.row(rollnumber);
            for (Map.Entry<String, List<MarksEntity>> entry : row.entrySet()) {
                boolean isPass = false;

                MarksEntity tmp = null;
                for (MarksEntity k2 : entry.getValue()) {
                    tmp = k2;
                    if (k2.getStatus().toLowerCase().contains("pass") || k2.getStatus().toLowerCase().contains("exempt")) {
                        isPass = true;
                        break;
                    }
                }

                if (!isPass) {
                    SubjectEntity sub = tmp.getSubjectMarkComponentId().getSubjectId();

                    int totalFail = 0;
                    MarksEntity failedRow = tmp;

                    for (SubjectEntity replace : sub.getSubjectEntityList()) {
                        List<MarksEntity> replaced = marksService.getAllMarksByStudentAndSubject(tmp.getStudentId().getId(), replace.getId(), "0");
                        for (MarksEntity marks : replaced) {
                            tmp = marks;
                            if (marks.getStatus().toLowerCase().contains("pass") || marks.getStatus().toLowerCase().contains("exempt") || marks.getStatus().toLowerCase().contains("studying")) {
                                isPass = true;
                                break;
                            }
                        }

                        if (!isPass) {
                            failedRow = tmp;
                            totalFail++;
                        }
                    }

                    if (totalFail == sub.getSubjectEntityList().size()) {
                        resultList.add(failedRow);
                    }
                }
            }
        }

        return resultList;
    }

    public static boolean containsIgnoreCase(String str, String searchStr)     {
        if(str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }

    public static String parseIntegerListToString(Collection<Integer> list) {
        String result = "";
        int count = 0;
        for (int stId : list) {
            result += stId + (count != list.size() - 1 ? "," : "") ;
            count++;
        }

        return result;
    }
}
