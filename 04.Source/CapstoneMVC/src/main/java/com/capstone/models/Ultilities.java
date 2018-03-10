package com.capstone.models;

import com.capstone.entities.*;
import com.capstone.services.*;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Ultilities {

    public static List<String> notexist = new ArrayList<>();

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

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
                .filter(c -> c.getStatus().toLowerCase().contains("pass") ||
                        c.getStatus().toLowerCase().contains("fail") ||
                        c.getStatus().toLowerCase().contains("study"))
                .collect(Collectors.toList());
        newSet = SortSemestersByMarks(newSet);
        return newSet;
    }

    public static List<MarksEntity> FilterStudentsOnlyPassAndFailAndStudyiAndNotStartg(List<MarksEntity> set) {
        List<MarksEntity> newSet = set;
// .stream()
//                .filter(c -> !c.getStatus().trim().toLowerCase().contains("start") &&
//                        !c.getStatus().trim().toLowerCase().contains(("diem")) &&
//                        !c.getStatus().trim().toLowerCase().contains(("start")))
//                .collect(Collectors.toList());
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

        List<MarksEntity> newList = FilterStudentsOnlyPassAndFailAndStudyiAndNotStartg(list);

        IMarksService service = new MarksServiceImpl();

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
                                if (m.getStatus().toLowerCase().contains("pass") || m.getStatus().toLowerCase().contains("studying")) {

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

                                                    if (k2.getAverageMark() >= failMark || k2.getStatus().toLowerCase().contains("pass")) {
                                                        isPass = true;
                                                        break;
                                                    }
                                                }

                                                if (!isPass) {
                                                    failedRow = new FailPrequisiteModel(tmp, m.getSubjectMarkComponentId().getSubjectId().getId(), m.getSemesterId().getSemester());

                                                    for (SubjectEntity replace : tmp.getSubjectMarkComponentId().getSubjectId().getSubjectEntityList()) {
                                                        List<MarksEntity> replaced = service.getAllMarksByStudentAndSubject(tmp.getStudentId().getId(), replace.getId(), "0");
                                                        if (replaced != null) {
                                                            replaced = SortSemestersByMarks(replaced);
                                                            for (MarksEntity marks : replaced) {
                                                                tmp = marks;
                                                                if (marks.getStatus().toLowerCase().contains("pass")) {
                                                                    isPass = true;
                                                                    break;
                                                                }
                                                            }

                                                            if (!isPass) {
                                                                failedRow = new FailPrequisiteModel(tmp, m.getSubjectMarkComponentId().getSubjectId().getId(), m.getSemesterId().getSemester());
                                                            }
                                                        }
                                                    }

                                                    for (SubjectEntity replace : tmp.getSubjectMarkComponentId().getSubjectId().getSubjectEntityList1()) {
                                                        List<MarksEntity> replaced = service.getAllMarksByStudentAndSubject(tmp.getStudentId().getId(), replace.getId(), "0");
                                                        if (replaced != null) {
                                                            replaced = SortSemestersByMarks(replaced);
                                                            for (MarksEntity marks : replaced) {
                                                                tmp = marks;
                                                                if (marks.getStatus().toLowerCase().contains("pass")) {
                                                                    isPass = true;
                                                                    break;
                                                                }
                                                            }

                                                            if (!isPass) {
                                                                failedRow = new FailPrequisiteModel(tmp, m.getSubjectMarkComponentId().getSubjectId().getId(), m.getSemesterId().getSemester());
                                                            }
                                                        }

                                                        for (SubjectEntity r : replace.getSubjectEntityList()) {
                                                            List<MarksEntity> replaced2 = service.getAllMarksByStudentAndSubject(tmp.getStudentId().getId(), r.getId(), "0");
                                                            if (replaced2 != null) {
                                                                replaced2 = SortSemestersByMarks(replaced2);
                                                                for (MarksEntity marks : replaced2) {
                                                                    tmp = marks;
                                                                    if (marks.getStatus().toLowerCase().contains("pass")) {
                                                                        isPass = true;
                                                                        break;
                                                                    }
                                                                }

                                                                if (!isPass) {
                                                                    failedRow = new FailPrequisiteModel(tmp, m.getSubjectMarkComponentId().getSubjectId().getId(), m.getSemesterId().getSemester());
                                                                }
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

    public static boolean IsFailedSpecial(List<MarksEntity> list, PrequisiteEntity prequisite) {
        ISubjectService subjectService = new SubjectServiceImpl();
        String[] rows = prequisite.getPrequisiteSubs() == null ? prequisite.getNewPrequisiteSubs().split("OR") : prequisite.getPrequisiteSubs().split("OR");
        int totalRow = 0;
        for (String row : rows) {
            row = row.replaceAll("\\(", "").replaceAll("\\)", "").trim();
            String[] cells = row.split(",");
            int totalCell = 0;
            for (String cell : cells) {
                cell = cell.trim();
                SubjectEntity s = subjectService.findSubjectById(cell);
                if (s != null) {
                    List<MarksEntity> m = list.stream().filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId())).collect(Collectors.toList());
                    if (m.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
                        totalCell++;
                    } else {
                        List<SubjectEntity> l1 = s.getSubjectEntityList();
                        boolean pass = false;
                        for (SubjectEntity s1 : l1) {
                            List<MarksEntity> marks = list.stream().filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s1.getId())).collect(Collectors.toList());
                            if (marks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
                                pass = true;
                                totalCell++;
                                break;
                            }
                        }
                        if (!pass) {
                            List<SubjectEntity> l2 = s.getSubjectEntityList1();
                            for (SubjectEntity s1 : l2) {
                                List<MarksEntity> marks = list.stream().filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s1.getId())).collect(Collectors.toList());
                                if (marks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
                                    pass = true;
                                    totalCell++;
                                    break;
                                }
                            }
                            if (!pass) {
                                for (SubjectEntity s1 : l2) {
                                    for (SubjectEntity s2 : s1.getSubjectEntityList()) {
                                        List<MarksEntity> marks = list.stream().filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s2.getId())).collect(Collectors.toList());
                                        if (marks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
                                            pass = true;
                                            totalCell++;
                                            break;
                                        }
                                    }
                                    if (pass) break;
                                }
                            }
                        }
                    }
                }
            }
            if (totalCell == cells.length) {
                totalRow++;
            }
        }

        if (totalRow > 0) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean HasFailedPrequisitesOfOneStudent(List<MarksEntity> list, PrequisiteEntity prequisite) {
        ISubjectService subjectService = new SubjectServiceImpl();

        List<String> allSemesters = Ultilities.SortSemesters(new RealSemesterServiceImpl().getAllSemester())
                .stream()
                .map(c -> c.getSemester().trim())
                .collect(Collectors.toList());

        List<MarksEntity> newList = FilterStudentsOnlyPassAndFailAndStudyiAndNotStartg(list);

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

                        for (SubjectEntity replace : sub.getSubjectEntityList1()) {
                            List<MarksEntity> replaced = map.get(replace.getId());
                            if (replaced != null) {
                                replaced = SortSemestersByMarks(replaced);
                                for (MarksEntity marks : replaced) {
                                    if (marks.getStatus().toLowerCase().contains("pass") || marks.getStatus().toLowerCase().contains("exempt")) {
                                        return false;
                                    }
                                }
                            }

                            for (SubjectEntity rep : replace.getSubjectEntityList()) {
                                List<MarksEntity> reps = map.get(rep.getId());
                                if (reps != null) {
                                    reps = SortSemestersByMarks(reps);
                                    for (MarksEntity marks : reps) {
                                        if (marks.getStatus().toLowerCase().contains("pass") || marks.getStatus().toLowerCase().contains("exempt")) {
                                            return false;
                                        }
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

        return false;
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

        if (searchStr.isEmpty()) return true;

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

        List<SubjectCurriculumEntity> data = new ArrayList<>();
        if (docs.size() > 0) {
//            IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();
//            List<Integer> tmp = new ArrayList<>();
//            tmp.add(student.getId());
//            docs = documentStudentService.getDocumentStudentByByStudentId(tmp);
//            CurriculumEntity curriculumEntity = docs.get(0).getCurriculumId();
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null) {
                    List<SubjectCurriculumEntity> listCur = doc.getCurriculumId().getSubjectCurriculumEntityList();
                    for (SubjectCurriculumEntity c : listCur) {
                        if (listSubjects.stream().anyMatch(a -> a.getId().equals(c.getSubjectId().getId()))) {
                            data.add(c);
                        }
                    }
                }
            }
        }

        data.sort(Comparator.comparingInt(SubjectCurriculumEntity::getOrdinalNumber));

        List<SubjectEntity> result = new ArrayList<>();
        for (SubjectCurriculumEntity c : data) {
            if (!result.contains(c.getSubjectId())) result.add(c.getSubjectId());
        }

        return result;
    }

    public static List<String> getStudentCurriculumSubjects(StudentEntity student) {
        List<String> subs = new ArrayList<>();
//        IStudentService service = new StudentServiceImpl();
//        StudentEntity student = service.findStudentById(studentId);
        if (student != null) {
            List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
            if (!docs.isEmpty()) {
                for (DocumentStudentEntity doc : docs) {
                    if (doc.getCurriculumId() != null) {
                        List<SubjectCurriculumEntity> cursubs = doc.getCurriculumId().getSubjectCurriculumEntityList();
                        for (SubjectCurriculumEntity s : cursubs) {
                            if (!subs.contains(s.getSubjectId().getId())) subs.add(s.getSubjectId().getId());
                        }
                    }
                }
            }
        }

        return subs;
    }

    public static DocumentStudentEntity getStudentLatestDocument(StudentEntity student) {
        List<Integer> tmp = new ArrayList<>();
        IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();
        tmp.add(student.getId());
        List<DocumentStudentEntity> docs = documentStudentService.getDocumentStudentByByStudentId(tmp);
        if (!docs.isEmpty()) {
            return docs.get(0);
        }

        return null;
    }

    public static void GetMenu(ServletContext servletContext, CredentialsEntity user) {
        IDynamicMenuService dynamicMenuService = new DynamicMenuServiceImpl();

        List<DynamicMenuEntity> list = dynamicMenuService.getAllMenu().stream().filter(s -> s.getRole().contains(user.getRole())).collect(Collectors.toList());

        List<DynamicMenuEntity> menuNoFunctionGroup = list.stream().filter(s -> s.getFunctionGroup() == null).collect(Collectors.toList());

        List<DynamicMenuEntity> functionGroup = list.stream().filter(s -> s.getGroupName() != null).distinct().collect(Collectors.toList());
        List<String> groups = new ArrayList<>();
        for (DynamicMenuEntity temp : functionGroup) {
            if (!groups.contains(temp.getGroupName())) {
                groups.add(temp.getGroupName());
            }
        }

        List<DynamicMenuEntity> menu = list.stream().filter(s -> s.getFunctionGroup() != null).collect(Collectors.toList());
//        ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.setAttribute("menu", menu);
        servletContext.setAttribute("functionGroup", groups);
        servletContext.setAttribute("menuNoFunctionGroup", menuNoFunctionGroup);
        servletContext.setAttribute("role", user.getRole());

    }

    public static int GetSemesterIdBeforeThisId(int id) {
        for (int i = 0; i < Global.getSortedList().size(); i++) {
            RealSemesterEntity r = Global.getSortedList().get(i);
            if (r.getId() == id) {
                if (i == 0) {
                    return r.getId();
                } else {
                    RealSemesterEntity rAnother = Global.getSortedList().get(i - 1);
                    return rAnother.getId();
                }
            }
        }

        return -1;
    }

    public static List<SubjectCurriculumEntity> StudentCurriculumSubjects(StudentEntity student) {
        List<SubjectCurriculumEntity> list = new ArrayList<>();
        for (DocumentStudentEntity doc : student.getDocumentStudentEntityList()) {
            if (doc.getCurriculumId() != null && !doc.getCurriculumId().getName().toLowerCase().contains("pc")) {
                list.addAll(doc.getCurriculumId().getSubjectCurriculumEntityList());
            }
        }
        return list;
    }

    public static DocumentStudentEntity GetLatestDoc(List<DocumentStudentEntity> docs) {
        docs.sort(Comparator.comparingLong(c -> {
            DocumentStudentEntity d = (DocumentStudentEntity) c;
            return d.getCreatedDate().getTime();
        }).reversed());
        return docs.get(0);
    }

    public static ResponseEntity<String> sendNotification(String msg, String email, List<ScheduleEntity> listNewSchedule, AndroidPushNotificationsService androidPushNotificationsService, String type) {
        try {

            Gson gson = new Gson();

            NotificationModel notification = new NotificationModel();
            notification.setBody(msg);
            notification.setSound("default");

            FireBaseMessagingModel fireBaseMessaging = new FireBaseMessagingModel();
            fireBaseMessaging.setTo("/topics/" + email);

            List<ScheduleModel> scheduleModelList = new ArrayList<>();
            for (ScheduleEntity schedule : listNewSchedule) {
                ScheduleModel model = new ScheduleModel();
                model.setCourseName(schedule.getCourseId().getSubjectCode());
                model.setDate(schedule.getDateId().getDate());
                model.setRoom(schedule.getRoomId().getName());
                model.setSlot(schedule.getDateId().getSlotId().getSlotName());
                model.setStartTime(schedule.getDateId().getSlotId().getStartTime());
                model.setEndTime(schedule.getDateId().getSlotId().getEndTime());
                model.setLecture(URLEncoder.encode(schedule.getEmpId().getFullName(), "UTF-8"));

                scheduleModelList.add(model);
            }

            FirebaseDataModel data = new FirebaseDataModel();
            data.setNewScheduleList(scheduleModelList);
            data.setType(type);
            fireBaseMessaging.setData(data);

//            NotificationModel notificationModel = new NotificationModel();
//
//            if(type.equals("create")){
//                notificationModel.setTitle("You have new schedule");
//            }else{
//                notificationModel.setTitle("Your schedule has been changed");
//            }
//            notificationModel.setBody( "Your subject " + listNewSchedule.get(0).getCourseId().getSubjectCode() + " has new schedules.");
//            fireBaseMessaging.setNotification(notificationModel);

            HttpEntity<String> request = new HttpEntity<>(gson.toJson(fireBaseMessaging));

            CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);

            String firebaseResponse = pushNotification.get();

            return new ResponseEntity<>(firebaseResponse, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>("Push Notification ERROR!", HttpStatus.BAD_REQUEST);
    }

    public static void isLatestMarkFailOrNot(MarksEntity latestMark, List<MarksEntity> sortedList, LatestFailMark checkModel) {
        RealSemesterEntity tmpSemester = latestMark.getSemesterId();
        //check xem trong một kì có học môn đó 2 lần không (trả nợ ngay trong kì)
        List<MarksEntity> reLearnInSameSemester = sortedList.stream()
                .filter(q -> q.getSemesterId().getId() == tmpSemester.getId())
                .collect(Collectors.toList());
        if (reLearnInSameSemester.size() >= 2) {
            //nếu trong kì có 2 record, pass, fail --> hs đó pass (không được học cải thiện ngay trong kì)
            // nếu có 2 fail --> fail; nếu có 1 pass, 1 fail -> pass
            MarksEntity checkPass = reLearnInSameSemester.stream()
                    .filter(q -> q.getStatus().equalsIgnoreCase(Enums.MarkStatus.PASSED.getValue()))
                    .findFirst().orElse(null);
            if (checkPass == null) {
                checkModel.setFailed(true);
                checkModel.setLatestFailedMark(latestMark);
            }
        } else {
            if (latestMark != null &&
                    latestMark.getStatus().equalsIgnoreCase(Enums.MarkStatus.FAIL.getValue())) {
                checkModel.setFailed(true);
                checkModel.setLatestFailedMark(latestMark);
            }
        }
    }

    public static boolean isLatestMarkFailOrNotVer2(MarksEntity latestMark, List<MarksEntity> sortedList) {
        RealSemesterEntity tmpSemester = latestMark.getSemesterId();
        //check xem trong một kì có học môn đó 2 lần không (trả nợ ngay trong kì)
        List<MarksEntity> reLearnInSameSemester = sortedList.stream()
                .filter(q -> q.getSemesterId().getId() == tmpSemester.getId())
                .collect(Collectors.toList());
        if (reLearnInSameSemester.size() >= 2) {
            //nếu trong kì có 2 record, pass, fail --> hs đó pass (không được học cải thiện ngay trong kì)
            // nếu có 2 fail --> fail; nếu có 1 pass, 1 fail -> pass
            MarksEntity checkPass = reLearnInSameSemester.stream()
                    .filter(q -> q.getStatus().equalsIgnoreCase(Enums.MarkStatus.PASSED.getValue()))
                    .findFirst().orElse(null);
            if (checkPass != null) {
                return false;
            } else {
                return true;
            }
        } else {
            if (latestMark != null &&
                    latestMark.getStatus().equalsIgnoreCase(Enums.MarkStatus.FAIL.getValue())) {
                return true;
            } else {
                return false;
            }
        }
    }

    //
    public static boolean isSubjectFailedPrerequisite(SubjectEntity subject, List<RealSemesterEntity> sortedSemester
            , List<SubjectEntity> allSubjects, List<MarksEntity> allMarks, RealSemesterEntity selectedSemester
            , List<PrequisiteEntity> allPrerequisites) {

        boolean result = false;

        PrequisiteEntity prerequisite = subject.getPrequisiteEntity();
        //----check prerequisite start here------
        if (prerequisite != null) {
            String oldPrerequisite = prerequisite.getPrequisiteSubs();
            String newPrerequisite = prerequisite.getNewPrequisiteSubs();
            if (oldPrerequisite == null && newPrerequisite == null) {
                return result;
            } else {

                boolean isAbleToLearn = false;
                //check year for newPrerequisite
                // if currentSemester < effectionSemester -> we can't check prerequesite
                boolean able4newPrerequisite = true;
                List<String> rows = null;
                if (oldPrerequisite != null && newPrerequisite == null) {
                    rows = Arrays.asList(oldPrerequisite.split("OR"));
                } else if (oldPrerequisite == null && newPrerequisite != null) {
                    String effectSemester = prerequisite.getEffectionSemester();
                    RealSemesterEntity affectSemester = sortedSemester.stream()
                            .filter(q -> q.getSemester().equalsIgnoreCase(effectSemester))
                            .findFirst().orElse(null);

                    int selectedIndex = sortedSemester.indexOf(selectedSemester);
                    int effectIndex = sortedSemester.indexOf(affectSemester);

                    if (selectedIndex < effectIndex) {
                        able4newPrerequisite = false;
                    }

                    rows = Arrays.asList(newPrerequisite.split("OR"));
                }else if (oldPrerequisite != null && newPrerequisite != null){

                    //kiểm tra cả 2 cũ lẫn mới
                    String[] oldSubj = oldPrerequisite.split("OR");
                    String[] newSubj = newPrerequisite.split("OR");
                    rows.addAll(Arrays.asList(oldSubj));
                    rows.addAll(Arrays.asList(newSubj));
                }


                if (able4newPrerequisite) {
                    //check prerequisite of subject
                    rowLoop:
                    for (String row : rows) {
                        row = row.replaceAll("\\(", "")
                                .replaceAll("\\)", "").trim();
                        String[] cells = row.split(",");

                        //count if student learned enough prerequisite subject
                        int numNeedtoPass = cells.length;
                        int countPass = 0;

//                            try{
                        for (String cell : cells) {
                            String subjCode = cell.trim();
                            SubjectEntity cellSubject = allSubjects.stream()
                                    .filter(q -> q.getId().equalsIgnoreCase(subjCode)).findFirst()
                                    .orElse(null);

                            //khởi tạo như thế này để có thể dùng stream filter
                            if (cellSubject != null) {


                                List<SubjectEntity> isReplacedSubjects = new ArrayList<>(cellSubject.getSubjectEntityList1());
                                List<SubjectEntity> replaceSubjects = new ArrayList<>(cellSubject.getSubjectEntityList());

                                List<String> isReplacedSubjectCodes = isReplacedSubjects.stream()
                                        .map(q -> q.getId()).collect(Collectors.toList());
                                List<String> replaceSubjectCodes = replaceSubjects.stream()
                                        .map(q -> q.getId()).collect(Collectors.toList());

                                //mảng chính gồm môn chính [A] và môn thay thế của [A] và môn bị thay thế của [A]
                                //check môn thay thế
                                List<String> mainList = new ArrayList<>();
                                mainList.add(subjCode);
                                mainList.addAll(isReplacedSubjectCodes);
                                mainList.addAll(replaceSubjectCodes);


                                // kiểm tra môn chính, môn thay thế và môn bị thay thế
                                List<MarksEntity> marksList = allMarks.stream()
                                        .filter(q -> mainList.stream()
                                                .anyMatch(a -> a.equalsIgnoreCase(
                                                        q.getSubjectMarkComponentId().getSubjectId().getId())
                                                )
                                        ).collect(Collectors.toList());

                                marksList = Ultilities.SortSemestersByMarks(marksList);

                                if (!marksList.isEmpty()) {

                                    MarksEntity latestMark = marksList.get(marksList.size() - 1);

                                    RealSemesterEntity tmpSemester = latestMark.getSemesterId();

                                    //lấy tất cả điểm trong cùng học kỳ mới nhất,
                                    //  tránh trường hợp học 1 môn 2 lần trong 1 học kỳ mà khi hàm sắp xếp chạy thì
                                    //  2 môn k biết môn nào học trước học sau để lấy điểm cuối cùng
                                    List<MarksEntity> reLearnInSameSemester = marksList.stream()
                                            .filter(q -> q.getSemesterId().getId() == tmpSemester.getId())
                                            .collect(Collectors.toList());


                                    RealSemesterEntity summer2017 = sortedSemester.stream()
                                            .filter(q -> q.getSemester().equalsIgnoreCase("SUMMER2017"))
                                            .findFirst().orElse(null);
                                    int selectedSemesterIndex = sortedSemester.indexOf(selectedSemester);
                                    int summer2017Index = sortedSemester.indexOf(summer2017);

                                    //check xem có điểm nào thỏa dk
                                    MarksEntity isPass = null;
                                    List<SubjectEntity> failSubjs = new ArrayList<>();

                                    //nếu có 2 môn cùng được học trong 1 kì,
                                    //  nếu có record nào pass hoặc thỏa dk điểm tiên quyết thì môn đó
                                    //  được xem là pass tiên quyết (ko phải pass môn học đó- chỉ là đủ dk để học môn tiên quyết),
                                    //  vì học sinh không được dk học cải thiện cùng 1 môn trong 1 học kỳ
                                    //  -> trường hợp pass sau đó học cải thiện rồi bị fail trong cùng 1 kỳ là ko xuất hiện,
                                    //   chỉ có học fail sau đó trả nợ ngay trong kì
                                    if (!reLearnInSameSemester.isEmpty()) {

                                        //1.nếu như học kỳ được chọn <= Summer2017,
                                        //    xét dk qua môn tiên quyết theo trạng thái điểm.
                                        //2.nếu như học kỳ được chọn > Summer2017,
                                        //     xét dk qua  môn tiên quyết lớn hơn hoặc = điểm được quy định trong
                                        //     bảng Prequsite.
                                        if (selectedSemesterIndex <= summer2017Index) {
                                            //nếu trong kì có 2 record, pass, fail --> hs đó pass (không được học cải thiện ngay trong kì)
                                            // nếu có 2 fail --> fail
                                            isPass = reLearnInSameSemester.stream()
                                                    .filter(q -> q.getStatus().equalsIgnoreCase(Enums.MarkStatus.PASSED.getValue())
                                                    ||  q.getStatus().equalsIgnoreCase(Enums.MarkStatus.STUDYING.getValue()))
                                                    .findFirst().orElse(null);

                                        } else {
                                            PrequisiteEntity prerequisite1 = allPrerequisites.stream()
                                                    .filter(q -> q.getSubjectId().equalsIgnoreCase(cellSubject.getId()))
                                                    .findFirst().orElse(null);

                                            String tempSemester = prerequisite1.getEffectionSemester();

                                            Double tmpPassMark;
                                            //xét xem môn này có tồn tại học kỳ áp dụng khác nhau ko
                                            if (tempSemester == null || tempSemester.isEmpty()) {
                                                tmpPassMark = prerequisite1.getFailMark() * 1.0;
                                            } else {
                                                //check xem học kỳ được chọn thỏa dk áp dụng theo học kỳ nào của môn dang xét
                                                RealSemesterEntity affectionSemester = sortedSemester.stream()
                                                        .filter(q -> q.getSemester().equalsIgnoreCase(tempSemester))
                                                        .findFirst().orElse(null);
                                                int affectIndex = sortedSemester.indexOf(affectionSemester);

                                                if (selectedSemesterIndex <= affectIndex) {
                                                    tmpPassMark = prerequisite1.getFailMark() * 1.0;
                                                } else {
                                                    tmpPassMark = prerequisite1.getNewFailMark() * 1.0;
                                                }
                                            }

                                             isPass = reLearnInSameSemester.stream()
                                                    .filter(q -> q.getAverageMark() >= tmpPassMark
                                                            || q.getStatus().equalsIgnoreCase(Enums.MarkStatus.PASSED.getValue())
                                                            ||  q.getStatus().equalsIgnoreCase(Enums.MarkStatus.STUDYING.getValue()))
                                                    .findFirst().orElse(null);
                                        }


                                    }
                                    if (isPass != null) {
                                        ++countPass;
                                    }else{
                                        failSubjs.add(cellSubject);
                                    }

                                }


                            }
                        }
                        if (countPass == numNeedtoPass) {
                            isAbleToLearn = true;
                            break rowLoop;
                        }
//                            }catch(Exception e){
//                                System.out.println(e.getMessage());
//                            }


                    }

                    //check if this subject is able to learn
                    if (isAbleToLearn) {
                        result = false;
                    } else {

                        result = true;
                    }
                }
            }
        }

        return result;
    }


}
