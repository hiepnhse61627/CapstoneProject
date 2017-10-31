package com.capstone.models;

import com.capstone.entities.*;
import com.capstone.services.*;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.security.auth.Subject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Ultilities {

    public static List<String> notexist = new ArrayList<>();

    public static List<MarksEntity> SortSemestersByMarks(List<MarksEntity> set) {
        ArrayList<String> seasons = new ArrayList<String>() {{
            add("spring");
            add("summer");
            add("fall");
            add("n/a");
        }};

        try {
            set.sort(Comparator.comparingInt(a -> {
                MarksEntity mark = (MarksEntity) a;
                if (mark.getSemesterId() == null) return 0;
                if (mark.getSemesterId().getSemester().equalsIgnoreCase("n/a")) return 0;
                String removewhite = mark.getSemesterId().getSemester().replaceAll("\\s+", "");
                String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
                Pattern pattern = Pattern.compile("^\\D*(\\d)");
                Matcher matcher = pattern.matcher(removeline);
                matcher.find();
                return Integer.parseInt(removeline.substring(matcher.start(1), removeline.length()));
            }).thenComparingInt(a -> {
                MarksEntity mark = (MarksEntity) a;
                if (mark.getSemesterId() == null) return 0;
                if (mark.getSemesterId().getSemester().equalsIgnoreCase("n/a")) return seasons.indexOf("n/a");
                String removewhite = mark.getSemesterId().getSemester().replaceAll("\\s+", "");
                String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
                Pattern pattern = Pattern.compile("^\\D*(\\d)");
                Matcher matcher = pattern.matcher(removeline);
                matcher.find();
                String season = removeline.substring(0, matcher.start(1)).toLowerCase();
                return seasons.indexOf(season);
            }).thenComparingInt(a -> {
                MarksEntity mark = (MarksEntity) a;
                if (mark.getSemesterId() == null) return 0;
                String semester = mark.getSemesterId().getSemester();
                return semester.indexOf("_");
            }));
        } catch (Exception e) {
            System.out.println("Sort failed");
        }

        return set;
    }

    public static List<MarksEntity> FilterStudentsOnlyPassAndFail(List<MarksEntity> set) {
        List<MarksEntity> newSet = set.stream()
                .filter(c -> !c.getStatus().trim().toLowerCase().contains("study") &&
                        !c.getStatus().trim().toLowerCase().contains("start") &&
                        !c.getStatus().trim().toLowerCase().contains(("diem")))
                .collect(Collectors.toList());
        newSet = SortSemestersByMarks(newSet);
        return newSet;
    }

    public static List<MarksEntity> FilterStudentsOnlyPassAndFailAndStudying(List<MarksEntity> set) {
        List<MarksEntity> newSet = set.stream()
                .filter(c -> !c.getStatus().trim().toLowerCase().contains("start") &&
                        !c.getStatus().trim().toLowerCase().contains(("diem")))
                .collect(Collectors.toList());
        newSet = SortSemestersByMarks(newSet);
        return newSet;
    }

    public static Table<String, String, List<MarksEntity>> StudentSubjectHashmap(List<MarksEntity> list) {
        Table<String, String, List<MarksEntity>> map = HashBasedTable.create();
        for (MarksEntity m : list) {
            if (map.get(m.getStudentId().getRollNumber(), m.getSubjectMarkComponentId().getSubjectId().getId()) == null) {
                List<MarksEntity> newMarkList = new ArrayList<>();
                newMarkList.add(m);
                map.put(m.getStudentId().getRollNumber(), m.getSubjectMarkComponentId().getSubjectId().getId(), newMarkList);
            } else {
                List<MarksEntity> marks = map.get(m.getStudentId().getRollNumber(), m.getSubjectMarkComponentId().getSubjectId().getId());
                marks.add(m);
                marks = SortSemestersByMarks(marks);
                map.put(m.getStudentId().getRollNumber(), m.getSubjectMarkComponentId().getSubjectId().getId(), marks);
            }
        }

        return map;
    }

    public static List<FailPrequisiteModel> FilterStudentPassedSubFailPrequisite(List<MarksEntity> list, Map<String, PrequisiteEntity> prequisites) {
        List<String> allSemesters = Ultilities.SortSemesters(new RealSemesterServiceImpl().getAllSemester())
                .stream()
                .map(c -> c.getSemester().trim())
                .collect(Collectors.toList());

        List<MarksEntity> newList = FilterStudentsOnlyPassAndFailAndStudying(list);

        List<FailPrequisiteModel> result = new ArrayList<>();
        Table<String, String, List<MarksEntity>> map = HashBasedTable.create();

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
                Map<String, List<MarksEntity>> subjects = map.row(studentId);

                for (Map.Entry<String, PrequisiteEntity> subject : prequisites.entrySet()) {

                    PrequisiteEntity pre = subject.getValue();
                    if (pre.getPrequisiteSubs() != null && !pre.getPrequisiteSubs().isEmpty()) {

                        if (subjects.get(subject.getKey()) != null && !subjects.get(subject.getKey()).isEmpty()) {

                            for (MarksEntity m : subjects.get(subject.getKey())) {
                                if (m.getStatus().toLowerCase().contains("pass") || m.getStatus().toLowerCase().contains("exempt") || m.getStatus().toLowerCase().contains("studying")) {

                                    int totalFail = 0;
                                    FailPrequisiteModel failedRow = null;

                                    String[] rows;
                                    if (allSemesters.indexOf(m.getSemesterId().getSemester()) < allSemesters.indexOf(pre.getEffectionSemester())) {
                                        rows = pre.getPrequisiteSubs().split("OR");
                                    } else {
                                        rows = pre.getNewPrequisiteSubs() == null ? pre.getPrequisiteSubs().split("OR") : pre.getNewPrequisiteSubs().split("OR");
                                    }

                                    for (String row : rows) {
                                        row = row.replaceAll("\\(", "").replaceAll("\\)", "");

                                        boolean isPass = false;

                                        String[] cell = row.trim().split(",");
                                        for (String prequisite : cell) {
                                            prequisite = prequisite.trim();

                                            // HANDLE LOGIC HERE
                                            if (subjects.get(prequisite) != null && !subjects.get(prequisite).isEmpty()) {
                                                MarksEntity tmp = null;
                                                for (MarksEntity k2 : SortSemestersByMarks(subjects.get(prequisite))) {
                                                    tmp = k2;

                                                    int failMark;
                                                    if (allSemesters.indexOf(m.getSemesterId().getSemester()) < allSemesters.indexOf(pre.getEffectionSemester())) {
                                                        failMark = pre.getFailMark();
                                                    } else {
                                                        failMark = pre.getNewFailMark() == null ? pre.getFailMark() : pre.getNewFailMark();
                                                    }

                                                    if (k2.getAverageMark() >= failMark || k2.getStatus().toLowerCase().contains("exempt")) {
                                                        isPass = true;
                                                        break;
                                                    }
                                                }

                                                if (!isPass) {
                                                    failedRow = new FailPrequisiteModel(tmp, m.getSubjectMarkComponentId().getSubjectId().getId());

                                                    for (SubjectEntity replace : tmp.getSubjectMarkComponentId().getSubjectId().getSubjectEntityList()) {
                                                        List<MarksEntity> replaced = subjects.get(replace.getId());
                                                        if (replaced != null) {
                                                            replaced = SortSemestersByMarks(replaced);
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
                                            }
                                            //////////////////////

                                        }

                                        if (!isPass) {
                                            totalFail++;
                                        }
                                    }

                                    if (totalFail == rows.length) {
                                        if (failedRow != null) result.add(failedRow);
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public static boolean HasFailedPrequisitesOfOneStudent(List<MarksEntity> list, PrequisiteEntity prequisite) {
        ISubjectService subjectService = new SubjectServiceImpl();

        List<String> allSemesters = Ultilities.SortSemesters(new RealSemesterServiceImpl().getAllSemester())
                .stream()
                .map(c -> c.getSemester().trim())
                .collect(Collectors.toList());

        List<MarksEntity> newList = FilterStudentsOnlyPassAndFailAndStudying(list);

        Map<String, List<MarksEntity>> map = new LinkedHashMap<>();
        if (!list.isEmpty()) {
            for (MarksEntity m : newList) {
                if (map.get(m.getSubjectMarkComponentId().getSubjectId().getId()) == null) {
                    List<MarksEntity> newMarkList = new ArrayList<>();
                    newMarkList.add(m);
                    map.put(m.getSubjectMarkComponentId().getSubjectId().getId(), newMarkList);
                } else {
                    map.get(m.getSubjectMarkComponentId().getSubjectId().getId()).add(m);
                }
            }

            PrequisiteEntity pre = prequisite;
            if (pre.getPrequisiteSubs() != null && !pre.getPrequisiteSubs().isEmpty()) {
                for (Map.Entry<String, List<MarksEntity>> m : map.entrySet()) {

                    boolean isPass = false;
                    SubjectEntity sub = subjectService.findSubjectById(m.getKey());

                    for (MarksEntity k2 : SortSemestersByMarks(m.getValue())) {

                        int failMark;
                        if (allSemesters.indexOf(k2.getSemesterId().getSemester()) < allSemesters.indexOf(pre.getEffectionSemester())) {
                            failMark = pre.getFailMark();
                        } else {
                            failMark = pre.getNewFailMark() == null ? pre.getFailMark() : pre.getNewFailMark();
                        }

                        if (k2.getAverageMark() >= failMark || k2.getStatus().toLowerCase().contains("exempt")) {
                            isPass = true;
                            break;
                        }
                    }

                    if (!isPass) {
                        for (SubjectEntity replace : sub.getSubjectEntityList()) {
                            List<MarksEntity> replaced = map.get(replace.getId());
                            if (replaced != null) {
                                replaced = SortSemestersByMarks(replaced);
                                for (MarksEntity marks : replaced) {
                                    if (marks.getStatus().toLowerCase().contains("pass") || marks.getStatus().toLowerCase().contains("exempt")) {
                                        return false;
                                    }
                                }
                            }
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static Connection getConnection() {
        Connection connection = null;

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

    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) return false;

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
            result += stId + (count != list.size() - 1 ? "," : "");
            count++;
        }

        return result;
    }

    public static List<SubjectEntity> SortSubjectsByOrdering(List<SubjectEntity> listSubjects, int studentId) {
        IStudentService studentService = new StudentServiceImpl();
        StudentEntity student = studentService.findStudentById(studentId);
        List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();

        List<SubjectEntity> result = new ArrayList<>();
        if (docs.size() > 0) {
            docs.sort(Comparator.comparingLong(c -> {
                DocumentStudentEntity d = (DocumentStudentEntity) c;
                if (d.getCreatedDate() == null) {
                    return 0;
                } else {
                    return d.getCreatedDate().getTime();
                }
            }));
            CurriculumEntity curriculumEntity = docs.get(docs.size() - 1).getCurriculumId();
            List<SubjectCurriculumEntity> listCur = curriculumEntity.getSubjectCurriculumEntityList();
            listCur.sort(Comparator.comparingInt(SubjectCurriculumEntity::getOrdinalNumber));

            for (SubjectCurriculumEntity c : listCur) {
                if (listSubjects.stream().anyMatch(a -> a.getId().equals(c.getSubjectId().getId()))) {
                    result.add(c.getSubjectId());
                }
            }
        }

        return result;
    }
}
