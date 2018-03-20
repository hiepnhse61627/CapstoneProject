package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.enums.SubjectTypeEnum;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GraduateController {
    IProgramService programService = new ProgramServiceImpl();
    IRealSemesterService semesterService = new RealSemesterServiceImpl();
    IMarksService markService = new MarksServiceImpl();
    IStudentService studentService = new StudentServiceImpl();
    ISubjectCurriculumService subjectCurriculumService = new SubjectCurriculumServiceImpl();

    // home page
    @RequestMapping("/graduate")
    public ModelAndView Index(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to /graduate");
        ModelAndView view = new ModelAndView("StudentGraduate");
        view.addObject("title", "Danh sách xét tốt nghiệp");

        List<ProgramEntity> programList = programService.getAllPrograms();
        List<RealSemesterEntity> semesterList = semesterService.getAllSemester();
        semesterList = Ultilities.SortSemesters(semesterList);
        semesterList = Lists.reverse(semesterList);

        view.addObject("programList", programList);
        view.addObject("semesterList", semesterList);

        return view;
    }

    // middle thing to split into 3 other method to process, all return 2 dimentional list string
    @RequestMapping("/processgraduate")
    @ResponseBody
    public JsonObject GetGraduateStudents(@RequestParam Map<String, String> params) {
        JsonObject obj = new JsonObject();
        List<List<String>> data = new ArrayList<>();

        String type = params.get("type");

//        final String sSearch = params.get("sSearch");

//        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
//        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
//        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));

        try {
            // RollNumber, FullName, TotalCredits, TotalSpecializedCredits
            List<List<String>> studentList;
            if (type.equals("Graduate")) {
                studentList = processGraduate2(params);
            } else if (type.equals("OJT")) {
                studentList = proccessOJT2(params);
            } else {
                studentList = processCapstone2(params);
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(studentList);
            obj.add("aaData", aaData);
//            obj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return obj;
    }

    //has fixed
    // get graduate students
    public List<List<String>> processGraduate(Map<String, String> params) {
        List<List<String>> data = new ArrayList<>();

        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));
        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));


        // get list semester to current semesterId
        List<RealSemesterEntity> semesters = getToCurrentSemester(semesterId);
        Set<Integer> semesterIds = semesters.stream().map(s -> s.getId()).collect(Collectors.toSet());

        int previousSemesterId = Ultilities.GetSemesterIdBeforeThisId(semesterId);
        List<StudentEntity> studentEntityList;
        if (programId < 0) {
            studentEntityList = studentService.findAllStudents();
        } else {
            studentEntityList = studentService.getStudentBySemesterIdAndProgram(previousSemesterId, programId);

        }
        // filter student in term 9 and so on
        studentEntityList = studentEntityList.stream().filter(s -> isCapstone(s, previousSemesterId)).collect(Collectors.toList());


        //loại những học sinh đã tốt nghiệp ra, chỉ add những sinh viên chưa tốt nghiệp tại kì đang xét
        List<StudentEntity> filteredList = new ArrayList<>();
        for (StudentEntity studentEntity : studentEntityList) {
            List<StudentStatusEntity> studentStatusEntities = new ArrayList<>(studentEntity.getStudentStatusEntityList());
            //lấy ra status của sinh viên
            List<StudentStatusEntity> tempStatus = studentStatusEntities.stream()
                    .filter(q -> q.getSemesterId().getId() == semesterId && q.getStatus().equalsIgnoreCase("G"))
                    .collect(Collectors.toList());

            //nếu như học sinh này chưa tốt nghiệp ở kì chỉ định thì add vào mảng để mốt xét
            if (tempStatus.isEmpty()) {
                filteredList.add(studentEntity);
            }

        }

        for (StudentEntity student : filteredList) {
            List<DocumentStudentEntity> documentStudentEntityList = student.getDocumentStudentEntityList();
            Map<SubjectEntity, Integer> subjectsCredits = processCreditsForSubject(documentStudentEntityList);
            // get mark list from student
            List<MarksEntity> marksEntityList = student.getMarksEntityList();
            // filter passed marks
            List<MarksEntity> passedMarks = new ArrayList<>();
            for (MarksEntity marksEntity : marksEntityList) {
                if ((marksEntity.getStatus().toLowerCase().contains("pass") || marksEntity.getStatus().toLowerCase().contains("exempt"))
                        && (semesterIds.contains(marksEntity.getSemesterId().getId()))) {
                    passedMarks.add(marksEntity);
                }
            }
            // distinct passed Marks
            List<MarksEntity> distinctMarks = new ArrayList<>();
            for (MarksEntity mark : passedMarks) {
                if (!distinctMarks.stream().anyMatch(d -> d.getStudentId().getRollNumber().equalsIgnoreCase(mark.getStudentId().getRollNumber())
                        && d.getSubjectMarkComponentId().getSubjectId().getId().equalsIgnoreCase(mark.getSubjectMarkComponentId().getSubjectId().getId()))) {
                    distinctMarks.add(mark);
                }
            }
            // calculate student credits if SYB was passed
            int studentCredits = 0;
            boolean passedFlag = false; // flag shows that a student have a subject that has 0 credits and not passed
            if (!subjectsCredits.containsValue(0)) {
                passedFlag = true;
            }
            for (MarksEntity marksEntity : distinctMarks) {
                SubjectEntity subject = marksEntity.getSubjectMarkComponentId().getSubjectId();
                if (!passedFlag) {
                    if ((subjectsCredits.get(subject) != null) && (subjectsCredits.get(subject) == 0)) {
                        passedFlag = true; // passed
                    }
                }
                studentCredits += subjectsCredits.get(subject) != null && subject.getType() != SubjectTypeEnum.OJT.getId() ? subjectsCredits.get(subject) : 0;

                if (subjectsCredits.get(subject) != null && student.getId() == 69667) {
                    System.out.println(subject.getId() + "_" + subjectsCredits.get(subject) + "_" + studentCredits);
                }
            }

            int specializedCredits = student.getProgramId().getSpecializedCredits();
            if (isGraduate) {
                if ((studentCredits >= specializedCredits) && (passedFlag)) {
                    List<String> t = new ArrayList<>();
                    t.add(student.getRollNumber());
                    t.add(student.getFullName());
                    t.add(String.valueOf(student.getTerm()));
                    t.add(String.valueOf(studentCredits));
                    t.add(String.valueOf(specializedCredits));
                    t.add(String.valueOf(student.getId()));
                    data.add(t);
                }
            } else {
                if ((studentCredits < specializedCredits) || (!passedFlag)) {
                    List<String> t = new ArrayList<>();
                    t.add(student.getRollNumber());
                    t.add(student.getFullName());
                    t.add(String.valueOf(student.getTerm()));
                    t.add(String.valueOf(studentCredits));
                    t.add(String.valueOf(specializedCredits));
                    t.add(String.valueOf(student.getId()));
                    data.add(t);
                }
            }
        }

        return data;
    }

    public List<List<String>> processGraduate2(Map<String, String> params) {
        List<List<String>> data = new ArrayList<>();

        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));
        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));


        int previousSemesterId = Ultilities.GetSemesterIdBeforeThisId(semesterId);
        List<StudentEntity> studentEntityList;
        if (programId < 0) {
            studentEntityList = studentService.findAllStudents();
        } else {
            studentEntityList = studentService.getStudentBySemesterIdAndProgram(previousSemesterId, programId);

        }
        // filter student in term 9 and so on
        studentEntityList = studentEntityList.stream().filter(s -> isCapstone(s, previousSemesterId)).collect(Collectors.toList());


        //loại những học sinh đã tốt nghiệp ra, chỉ add những sinh viên chưa tốt nghiệp tại kì đang xét
        List<StudentEntity> filteredList = new ArrayList<>();
        for (StudentEntity studentEntity : studentEntityList) {
            List<StudentStatusEntity> studentStatusEntities = new ArrayList<>(studentEntity.getStudentStatusEntityList());
            //lấy ra status của sinh viên
            List<StudentStatusEntity> tempStatus = studentStatusEntities.stream()
                    .filter(q -> q.getSemesterId().getId() == semesterId && q.getStatus().equalsIgnoreCase("G"))
                    .collect(Collectors.toList());

            //nếu như học sinh này chưa tốt nghiệp ở kì chỉ định thì add vào mảng để mốt xét
            if (tempStatus.isEmpty()) {
                filteredList.add(studentEntity);
            }

        }

        MarksServiceImpl marksService = new MarksServiceImpl();

        //lấy tất cả điểm ở kỳ được ở trước kì được chọn để xét duyệt, kì được chọn là semesterId
        List<MarksEntity> allMarks = marksService.getMarkByConditions(previousSemesterId, null, -1);

        loopStudents:
        for (StudentEntity student : filteredList) {
//            List<DocumentStudentEntity> documentStudentEntityList = student.getDocumentStudentEntityList();
//            Map<SubjectEntity, Integer> subjectsCredits = processCreditsForSubject(documentStudentEntityList);

            if (student.getRollNumber().equalsIgnoreCase("SE61778")) {
                System.out.println("bug");
            }
            // get mark list of student
            List<MarksEntity> studentMarksList = allMarks.stream()
                    .filter(q -> q.getStudentId().getId() == student.getId())
                    .collect(Collectors.toList());
            List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
            List<StudentEntity> uncheckable = new ArrayList<>();

            //tất cả các môn trong khung chương trình
            List<SubjectCurriculumEntity> subjects = new ArrayList<>();
            //tín chỉ chuyên ngành
            int specializedCredits = 0;
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                    CurriculumEntity curriculum = doc.getCurriculumId();
                    //học sinh liên thông sẽ không được xét
                    if (curriculum.getProgramId().getName().contains("lt")) {
                        //add những sinh viên không được xét vào 1 mảng và tiếp tục vòng lặp xét sinh viên
                        uncheckable.add(student);
                        continue loopStudents;
                    }
                    specializedCredits += curriculum.getSpecializedCredits();
                    List<SubjectCurriculumEntity> list = curriculum.getSubjectCurriculumEntityList();
                    for (SubjectCurriculumEntity s : list) {
                        if (!subjects.contains(s)) {
                            subjects.add(s);
                        }
                    }
                }
            }


            // tính tín chỉ tích lũy của sv
            int studentCredits = 0;

            //biến cờ check xem sinh viên có pass hết môn Chuyên ngành không
            boolean passedFlag = true;

            List<SubjectEntity> failSubjs = new ArrayList<>();
            //duyệt tất cả những môn có trong khung chương trình
            subjectCurriculumLoop:
            for (SubjectCurriculumEntity subjectCurriculum : subjects) {
                SubjectEntity subject = subjectCurriculum.getSubjectId();

                //lấy ra môn bị thay thế của môn A, A thay thế B, -> lấy B
                List<SubjectEntity> isReplacedSubject = subject.getSubjectEntityList1();
                //lấy ra môn bị thay thế của môn A, C thay thế A, -> lấy C
                List<SubjectEntity> replacedSubject = subject.getSubjectEntityList1();
                //mảng này chứa tất cả môn thay thế và môn chính
                List<SubjectEntity> checkSubjects = new ArrayList<>();
                checkSubjects.add(subject);
                checkSubjects.addAll(isReplacedSubject);
                checkSubjects.addAll(replacedSubject);

                List<MarksEntity> tempMarks = studentMarksList.stream().filter(q -> checkSubjects.stream()
                        .anyMatch(c -> c.getId().equalsIgnoreCase(q.getSubjectMarkComponentId().getSubjectId().getId()))
                ).collect(Collectors.toList());

                //sắp xếp thứ tự điểm theo kì thấp -> cao
                List<MarksEntity> sortedList = Ultilities.SortSemestersByMarks(tempMarks);

                if (!sortedList.isEmpty()) {
                    MarksEntity latestMark = sortedList.get(sortedList.size() - 1);
                    boolean isFailed = Ultilities.isLatestMarkFailOrNotVer2(latestMark, sortedList);
                    if (!isFailed) {
                        studentCredits += subjectCurriculum.getSubjectCredits();
                    } else {
                        failSubjs.add(subject);
                        passedFlag = false;
                    }
                }

            }

            if (isGraduate) {
                if ((studentCredits >= specializedCredits) && (passedFlag)) {
                    List<String> t = new ArrayList<>();
                    t.add(student.getRollNumber());
                    t.add(student.getFullName());
                    t.add(String.valueOf(student.getTerm()));
                    t.add(String.valueOf(studentCredits));
                    t.add(String.valueOf(specializedCredits));
                    t.add(String.valueOf(student.getId()));
                    data.add(t);
                }
            } else {
                if ((studentCredits < specializedCredits) || (!passedFlag)) {
                    List<String> t = new ArrayList<>();
                    t.add(student.getRollNumber());
                    t.add(student.getFullName());
                    t.add(String.valueOf(student.getTerm()));
                    t.add(String.valueOf(studentCredits));
                    t.add(String.valueOf(specializedCredits));
                    t.add(String.valueOf(student.getId()));
                    data.add(t);
                }
            }
        }

        return data;
    }
//    private List<SubjectEntity> getSubjectsInCurriculumns(List<DocumentStudentEntity> documentStudentEntityList) {
//        List<SubjectEntity> subjectEntityList = new ArrayList<>();
//        if (documentStudentEntityList != null && !documentStudentEntityList.isEmpty()) {
//            for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
//                CurriculumEntity curriculumEntity = documentStudentEntity.getCurriculumId();
//                List<SubjectCurriculumEntity> subjectCurriculumEntityList = subjectCurriculumService.getSubjectCurriculums(curriculumEntity.getId());
//                List<SubjectEntity> subjects = subjectCurriculumEntityList.stream().filter(s -> s.getTermNumber() > 0).map(s -> s.getSubjectId()).collect(Collectors.toList());
//
//                subjectEntityList.addAll(subjects);
//            }
//        }
//        return subjectEntityList;
//    }

//    private Integer countCreditsInCurriculumn(List<DocumentStudentEntity> documentStudentEntityList) {
//        int credits = 0;
//        if (documentStudentEntityList != null && !documentStudentEntityList.isEmpty()) {
//            for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
//                CurriculumEntity curriculumEntity = documentStudentEntity.getCurriculumId();
//                List<SubjectCurriculumEntity> subjectCurriculumEntityList = subjectCurriculumService.getSubjectCurriculums(curriculumEntity.getId());
//                subjectCurriculumEntityList = subjectCurriculumEntityList.stream().filter(s -> s.getTermNumber() >= 0).collect(Collectors.toList());
//
//                for (SubjectCurriculumEntity subjectCurriculumEntity : subjectCurriculumEntityList) {
//                    credits += subjectCurriculumEntity.getSubjectCredits();
//                }
//            }
//        }
//        return credits;
//    }

    /**
     * [This method processes (sort all semesters then iterate over the list, add semester to result list until reaching the current semester)
     * and returns list semesters from the beginning to current semester]
     *
     * @param currentSemesterId
     * @return listResult
     * @author HiepNH
     * @DateCreated 28/10/2017
     **/
    private List<RealSemesterEntity> getToCurrentSemester(Integer currentSemesterId) {
        List<RealSemesterEntity> semesters = semesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
        List<RealSemesterEntity> listResult = new ArrayList<>();
        for (RealSemesterEntity semester : semesters) {
            listResult.add(semester);
            if (semester.getId() == currentSemesterId) {
                break;
            }
        }
        return listResult;
    }

    public Map<SubjectEntity, Integer> processCreditsForSubject(List<DocumentStudentEntity> documentStudentEntityList) {
        Map<SubjectEntity, Integer> map = new HashMap<>();
        if (documentStudentEntityList != null && !documentStudentEntityList.isEmpty()) {
            for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
                CurriculumEntity curriculumEntity = documentStudentEntity.getCurriculumId();
                List<SubjectCurriculumEntity> subjectCurriculumEntityList =
                        new ArrayList<>(curriculumEntity.getSubjectCurriculumEntityList());

                //loại môn trong PC
                subjectCurriculumEntityList = subjectCurriculumEntityList.stream()
                        .filter(s -> s.getTermNumber() >= 0).collect(Collectors.toList());

                for (SubjectCurriculumEntity subjectCurriculumEntity : subjectCurriculumEntityList) {
                    SubjectEntity subjectEntity = subjectCurriculumEntity.getSubjectId();
                    Integer subjectCredits = subjectCurriculumEntity.getSubjectCredits();
                    map.put(subjectEntity, subjectCredits);
                    List<SubjectEntity> replacesInTheRight = subjectEntity.getSubjectEntityList();
                    if (replacesInTheRight != null && !replacesInTheRight.isEmpty()) {
                        for (SubjectEntity rightReplace : replacesInTheRight) {
                            map.put(rightReplace, subjectCredits);
                        }
                    }
                    List<SubjectEntity> replacesInTheLeft = subjectEntity.getSubjectEntityList1();
                    if (replacesInTheLeft != null && !replacesInTheLeft.isEmpty()) {
                        for (SubjectEntity leftReplace : replacesInTheLeft) {
                            map.put(leftReplace, subjectCredits);

                            for (SubjectEntity rightOfLeftReplace : leftReplace.getSubjectEntityList()) {
                                map.put(rightOfLeftReplace, subjectCredits);
                            }
                        }
                    }
                }
            }
        }

        return map;
    }

    // get OJT students
    public List<List<String>> proccessOJT(Map<String, String> params) {
        List<List<String>> data = new ArrayList<>();

        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));

        IRealSemesterService service = new RealSemesterServiceImpl();
        RealSemesterEntity semester = service.findSemesterById(semesterId);

//        List<RealSemesterEntity> semesterList = semesterService.getAllSemester();
//        semesterList = Ultilities.SortSemesters(semesterList);
//        semesterList = Lists.reverse(semesterList);


        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));
        int previousSemesterId = Ultilities.GetSemesterIdBeforeThisId(semester.getId());
        List<StudentEntity> students;
        if (programId < 0) {
            students = studentService.findStudentsBySemesterId(Ultilities.GetSemesterIdBeforeThisId(semester.getId()));
        } else {
            students = studentService.getStudentBySemesterIdAndProgram
                    (previousSemesterId, programId);
//            students = students.stream().filter(q -> q.getProgramId().getId() == programId).collect(Collectors.toList());
        }
//        EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
//        EntityManager em = fac.createEntityManager();


        IMarksService marksService = new MarksServiceImpl();
        List<MarksEntity> totalMarks = marksService.getMarkByConditions(previousSemesterId, null, -1);
        totalMarks = Ultilities.SortSemestersByMarks(totalMarks);

        students = students.stream().filter(c -> isOJT(c, previousSemesterId)).collect(Collectors.toList());
//        students = students.stream().filter(c -> c.getTerm() >= 5).collect(Collectors.toList());

//         lấy danh sách điểm những thằng đã opass hoặc đang học ojt
//        List<MarksEntity> map = em.createQuery("SELECT a FROM MarksEntity a WHERE a.isActivated = true " +
//                "AND a.subjectMarkComponentId.subjectId.type = 1 AND (LOWER(a.status) LIKE '%studying%' " +
//                "OR LOWER(a.status) LIKE '%pass%')", MarksEntity.class)
//                .getResultList()
//                .stream()
//                .filter(Ultilities.distinctByKey(c -> c.getStudentId().getId()))
//                .collect(Collectors.toList());

        //lấy danh sách điểm những thằng đã opass hoặc đang học ojt
        List<StudentEntity> map = marksService.getOjtStudentsFromSelectedSemesterAndBeforeFromMarks(previousSemesterId);


        IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();

        int i = 1;
        for (StudentEntity student : students) {

            System.out.println(i + " - " + students.size());

            if (student.getRollNumber().equals("SE61107")) {
                System.out.println();
            }

            List<SubjectCurriculumEntity> subjects = new ArrayList<>();
//            List<SubjectCurriculumEntity> processedSub = new ArrayList<>();


            int ojt = -1;
//            List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
            List<DocumentStudentEntity> docs = documentStudentService.getDocumentStudentListByStudentId(student.getId());
//            List<String> tmp = new ArrayList<>();
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                    List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
                    for (SubjectCurriculumEntity s : list) {
                        if (!subjects.contains(s)) {
                            subjects.add(s);
                            if (s.getSubjectId().getType() == SubjectTypeEnum.OJT.getId()) {
                                ojt = s.getTermNumber();
//                                break;
//                                tmp.add(s.getSubjectId().getId());
                            }
                        }
                    }
                }
            }

            boolean req = false;
            // loại những sinh viên đã có điểm hoặc đang học Ojt
//            if (map.stream().anyMatch(c -> c.getStudentId().getId() == student.getId())) {
//                req = true;
//            }
            if (map.stream().anyMatch(q -> q.getId() == student.getId())) {
                req = true;
            }

            if (!req) {
                List<SubjectCurriculumEntity> processedSub = new ArrayList<>();
                for (SubjectCurriculumEntity c : subjects) {
                    if (ojt > 0) {
                        if (c.getTermNumber() >= 0 && c.getTermNumber() < ojt) {
                            processedSub.add(c);
                        }
                    } else {
                        if (c.getTermNumber() >= 0) {
                            processedSub.add(c);
                        }
                    }
                }

                int required = 0;

                for (SubjectCurriculumEntity s : processedSub) {
                    if (s.getSubjectCredits() != null) {
                        required += s.getSubjectCredits();
                    }
                }

                int percent = student.getProgramId().getOjt();


//                int tongtinchi = student.getPassCredits();
                int tongtinchi = 0;
                List<String> tmp = processedSub.stream().map(c -> c.getSubjectId().getId()).distinct().collect(Collectors.toList());
//                List<MarksEntity> marks = marksService.getMarkByConditions(Ultilities.GetSemesterIdBeforeThisId(semester.getId()), tmp, student.getId());
//                marks = Ultilities.SortSemestersByMarks(marks);
                List<MarksEntity> marks = totalMarks
                        .stream()
                        .filter(c -> c.getStudentId().getId() == student.getId())
                        .filter(c -> tmp.stream().anyMatch(a -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(a)))
                        .collect(Collectors.toList());
                for (SubjectCurriculumEntity subject : processedSub) {
                    List<MarksEntity> subMarks = marks.stream().filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(subject.getSubjectId().getId())).collect(Collectors.toList());
                    if (subMarks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
                        tongtinchi += subject.getSubjectCredits();
                    } else {
                        boolean hasAdded = false;
                        for (SubjectEntity sub1 : subject.getSubjectId().getSubjectEntityList()) {
                            List<MarksEntity> subMarks1 = marks.stream().filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(sub1.getId())).collect(Collectors.toList());
                            if (subMarks1.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
                                tongtinchi += subject.getSubjectCredits();
                                hasAdded = true;
                                break;
                            }
                        }
                        if (!hasAdded) {
                            for (SubjectEntity sub1 : subject.getSubjectId().getSubjectEntityList1()) {
                                List<MarksEntity> subMarks1 = marks.stream().filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(sub1.getId())).collect(Collectors.toList());
                                if (subMarks1.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
                                    tongtinchi += subject.getSubjectCredits();
//                                    hasAdded = true;
                                    break;
                                }

                                for (SubjectEntity sub2 : sub1.getSubjectEntityList()) {
                                    List<MarksEntity> subMarks2 = marks.stream().filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(sub2.getId())).collect(Collectors.toList());
                                    if (subMarks2.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
                                        tongtinchi += subject.getSubjectCredits();
                                        hasAdded = true;
                                        break;
                                    }
                                }

                                if (hasAdded) break;
                            }
                        }
                    }
                }

                List<String> t = new ArrayList<>();
                t.add(student.getRollNumber());
                t.add(student.getFullName());
                t.add(String.valueOf(student.getTerm()));
                t.add(String.valueOf(tongtinchi));
                t.add(String.valueOf((int) ((required * percent * 1.0) / 100)));
                t.add(String.valueOf(student.getId()));

                if (isGraduate) {
                    if (tongtinchi >= (int) ((required * percent * 1.0) / 100)) {
                        data.add(t);
                    }
                } else {
                    if (tongtinchi < (int) ((required * percent * 1.0) / 100)) {
                        data.add(t);
                    }
                }
            } else {
                System.out.println("dang hoc hoac da pass");
            }

            i++;
        }

        return data;
    }

    public List<List<String>> proccessOJT2(Map<String, String> params) {
        List<List<String>> data = new ArrayList<>();

        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));

        IRealSemesterService service = new RealSemesterServiceImpl();
        RealSemesterEntity semester = service.findSemesterById(semesterId);


        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));
        int previousSemesterId = Ultilities.GetSemesterIdBeforeThisId(semester.getId());
        List<StudentEntity> students;
        if (programId < 0) {
            students = studentService.findStudentsBySemesterId(Ultilities.GetSemesterIdBeforeThisId(semester.getId()));
        } else {
            students = studentService.getStudentBySemesterIdAndProgram
                    (previousSemesterId, programId);
//            students = students.stream().filter(q -> q.getProgramId().getId() == programId).collect(Collectors.toList());
        }
//        EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
//        EntityManager em = fac.createEntityManager();


        IMarksService marksService = new MarksServiceImpl();
        // lấy điểm theo trước semester được chọn
        List<MarksEntity> totalMarks = marksService.getMarkByConditions(previousSemesterId, null, -1);
        totalMarks = Ultilities.SortSemestersByMarks(totalMarks);

        students = students.stream().filter(c -> isOJT(c, previousSemesterId)).collect(Collectors.toList());

        //lấy danh sách điểm những sinh viên đã pass hoặc đang học ojt theo kì trước kì được chọn
        List<StudentEntity> map = marksService.getOjtStudentsFromSelectedSemesterAndBeforeFromMarks(previousSemesterId);


        IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();

        int i = 1;
        for (StudentEntity student : students) {

            System.out.println(i + " - " + students.size());

            if (student.getRollNumber().equalsIgnoreCase("SE61576")) {
                System.out.println("bug");
            }


            List<SubjectCurriculumEntity> subjects = new ArrayList<>();


            int ojt = 6;
            //tổng tín chỉ yêu cầu để đi Ojt
            int required = 0;

            //lấy tín kì mà sinh viên đi OJT
            List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                    CurriculumEntity curriculum = doc.getCurriculumId();
//                    List<SubjectCurriculumEntity> list = curriculum.getSubjectCurriculumEntityList();
//                    for (SubjectCurriculumEntity s : list) {
//                        if (s.getSubjectId().getType() == SubjectTypeEnum.OJT.getId()) {
//                            ojt = s.getTermNumber();
//                            break;
//                        }
//                    }

                    //code mới cho việc lấy kì sinh viên được đi ojt
                    Integer tmpOjt = curriculum.getOjtTerm();
                    if (tmpOjt != null) {
                        ojt = tmpOjt;
                        break;
                    }
                }
            }

            //tính tín chỉ Chuyên Ngành
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                    CurriculumEntity curriculum = doc.getCurriculumId();
                    List<SubjectCurriculumEntity> list = curriculum.getSubjectCurriculumEntityList();
                    for (SubjectCurriculumEntity s : list) {
                        if (!subjects.contains(s) && s.getTermNumber() < ojt) {
                            subjects.add(s);
                            required += s.getSubjectCredits();
                        }
                    }
                }
            }


            boolean req = false;
            // loại những sinh viên đã có điểm hoặc đang học Ojt
            if (map.stream().anyMatch(q -> q.getId() == student.getId())) {
                req = true;
            }

            if (!req) {
                List<SubjectCurriculumEntity> processedSub = new ArrayList<>();
                for (SubjectCurriculumEntity c : subjects) {
                    if (ojt > 0) {
                        if (c.getTermNumber() >= 0 && c.getTermNumber() < ojt) {
                            processedSub.add(c);
                        }
                    } else {
                        if (c.getTermNumber() >= 0) {
                            processedSub.add(c);
                        }
                    }
                }

                int percent = student.getProgramId().getOjt();


                int tongtinchi = 0;
                //lấy ra tất cả những subjectId
                List<String> tmp = processedSub.stream().map(c -> c.getSubjectId().getId()).distinct().collect(Collectors.toList());
                List<MarksEntity> allStudentMarks = totalMarks
                        .stream()
                        .filter(c -> c.getStudentId().getId() == student.getId())
                        .filter(c -> tmp.stream().anyMatch(a -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(a)))
                        .collect(Collectors.toList());

                //tính tổng tín chỉ của sinh viên
                for (SubjectCurriculumEntity subjectCurriculum : processedSub) {
                    SubjectEntity itemSubject = subjectCurriculum.getSubjectId();
                    //contains main subject and all of it replace subject
                    List<SubjectEntity> checkList = new ArrayList<>();
                    checkList.add(itemSubject);
                    //exclude vovinam subject out
                    if (!itemSubject.getId().contains("vov")) {
                        checkList.addAll(itemSubject.getSubjectEntityList());
                        checkList.addAll(itemSubject.getSubjectEntityList1());
                        //lấy hết tất cả điểm của môn chính và môn thay thế của nó để kiểm tra xem đã pass chưa
                        List<MarksEntity> marks = allStudentMarks.stream().filter(q -> checkList.stream()
                                .anyMatch(c -> c.getId().equalsIgnoreCase(q.getSubjectMarkComponentId().getSubjectId().getId())))
                                .collect(Collectors.toList());
                        //sort by semester
                        marks = Ultilities.SortSemestersByMarks(marks);
                        if (!marks.isEmpty()) {
                            MarksEntity latestMark = marks.get(marks.size() - 1);
                            boolean isFail = Ultilities.isLatestMarkFailOrNotVer2(latestMark, marks);
                            if (!isFail) {
                                tongtinchi += subjectCurriculum.getSubjectCredits();
                            }
                        }
                    }
                }

                List<String> t = new ArrayList<>();
                t.add(student.getRollNumber());
                t.add(student.getFullName());
                t.add(String.valueOf(student.getTerm()));
                t.add(String.valueOf(tongtinchi));
                t.add(String.valueOf((int) ((required * percent * 1.0) / 100)));
                t.add(String.valueOf(student.getId()));

                //xét lấy những sinh viên có thể đi ojt hoặc k thể đi ojt
                if (isGraduate) {
                    if (tongtinchi >= (int) ((required * percent * 1.0) / 100)) {
                        data.add(t);
                    }
                } else {
                    if (tongtinchi < (int) ((required * percent * 1.0) / 100)) {
                        data.add(t);
                    }
                }
            } else {
                System.out.println("dang hoc hoac da pass");
            }

            i++;
        }

        return data;
    }

    // finda all atudents match OJT term
    private boolean isOJT(StudentEntity student, int previousSemesterId) {
        int ojt = 6;
        List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
        for (DocumentStudentEntity doc : docs) {
            if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
//                List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
//                for (SubjectCurriculumEntity s : list) {
////                    if (s.getSubjectId().getType() == SubjectTypeEnum.OJT.getId()) {
////                        ojt = s.getTermNumber();
////                        break;
////                    }
////                }
                CurriculumEntity curriculum = doc.getCurriculumId();
                Integer tmpTerm = curriculum.getOjtTerm();
                if (tmpTerm != null) {
                    ojt = tmpTerm;
                    break;
                }
            }
        }


        //convert to double 4 comparison
        double require = ojt * 1.0;
        StudentStatusEntity studentStatus = null;
        List<StudentStatusEntity> statusList = student.getStudentStatusEntityList();

        for (StudentStatusEntity status : statusList) {
            if (status.getSemesterId().getId() == previousSemesterId) {
                studentStatus = status;
            }
        }

        double studentTerm;
        try {
            if (studentStatus != null)
                studentTerm = Double.parseDouble(studentStatus.getTerm());
            else
                studentTerm = -1;

        } catch (Exception ex) {
            studentTerm = -1;
        }

        //studentTerm + 1  == selectedSemester : chọn danh sách sinh viên đi trong hk Spring2018
        // --> sinh viên đó sẽ được đi vào Spring2018 --> lấy trạng thái kì trước đó là Fall2017 để check
        if (studentTerm + 1 >= require) {
            return true;
        } else {
            return false;
        }
    }

    // finda all atudents match Capstone term
    private boolean isCapstone(StudentEntity student, int previousSemesterId) {
        int capstone = 9;
        List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
        for (DocumentStudentEntity doc : docs) {
            if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
                for (SubjectCurriculumEntity s : list) {
                    if (s.getSubjectId().getType() == SubjectTypeEnum.Capstone.getId()) {
                        capstone = s.getTermNumber();
                        break;
                    }
                }
            }
        }

        //convert to double 4 comparison
        double require = capstone * 1.0;
        StudentStatusEntity studentStatus = null;
        List<StudentStatusEntity> statusList = student.getStudentStatusEntityList();

        //get student status ở kì được chọn
        for (StudentStatusEntity status : statusList) {
            if (status.getSemesterId().getId() == previousSemesterId) {
                studentStatus = status;
            }
        }

        double studentTerm;
        // kiểm tra xem kì được chọn của học sinh có record hay không, tránh parse null
        try {
            if (studentStatus != null)
                studentTerm = Double.parseDouble(studentStatus.getTerm());
            else
                studentTerm = -1;

        } catch (Exception ex) {
            studentTerm = -1;
        }

        //studentTerm + 1  == selectedSemester : chọn danh sách sinh viên đi trong hk Spring2018
        // --> sinh viên đó sẽ được đi vào Spring2018 --> lấy trạng thái kì trước đó là Fall2017 để check
        if (studentTerm + 1 >= require) {
            return true;
        } else {
            return false;
        }
    }


    // get all records of capstone students
    public List<List<String>> processCapstone(Map<String, String> params) {
        List<List<String>> data = new ArrayList<>();

        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));

        IRealSemesterService service = new RealSemesterServiceImpl();
        RealSemesterEntity semester = service.findSemesterById(semesterId);

        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));
        int previousSemesterId = Ultilities.GetSemesterIdBeforeThisId(semester.getId());
        List<StudentEntity> students;
        if (programId < 0) {
            students = studentService.findAllStudents();
        } else {
            students = studentService.getStudentBySemesterIdAndProgram(previousSemesterId, programId);
        }

        EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = fac.createEntityManager();

//        IMarksService marksService = new MarksServiceImpl();

        students = students.stream().filter(c -> isCapstone(c, previousSemesterId)).collect(Collectors.toList());
//        students = students.stream().filter(c -> c.getTerm() >= 5).collect(Collectors.toList());

        //query for students already pass or learning capstone
        Query alreadyCapstone = em.createQuery("SELECT a FROM MarksEntity a WHERE a.isActivated = true " +
                "AND a.subjectMarkComponentId.subjectId.type = 2 " +
                "AND (LOWER(a.status) LIKE '%studying%' OR LOWER(a.status) LIKE '%pass%')" +
                "AND a.semesterId.id = :selectedSemester", MarksEntity.class);
        alreadyCapstone.setParameter("selectedSemester", previousSemesterId);

        List<MarksEntity> map = alreadyCapstone.getResultList();
        map = map.stream().filter(Ultilities.distinctByKey(c -> c.getStudentId().getId()))
                .collect(Collectors.toList());

        List<MarksEntity> hasOJT = em.createQuery("SELECT a FROM MarksEntity a WHERE a.isActivated = true " +
                "AND a.subjectMarkComponentId.subjectId.type = 1 AND LOWER(a.status) LIKE '%pass%'", MarksEntity.class)
                .getResultList()
                .stream()
                .filter(Ultilities.distinctByKey(c -> c.getStudentId().getId()))
                .collect(Collectors.toList());

        IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();

        int i = 1;
        for (StudentEntity student : students) {

            System.out.println(i + " - " + students.size());

            if (student.getRollNumber().equals("SE61261")) {
                System.out.println();
            }

            List<SubjectCurriculumEntity> subjects = new ArrayList<>();

            SubjectEntity capstoneSubject = null;
            int ojtCredits = 0;
            List<DocumentStudentEntity> docs = documentStudentService.getDocumentStudentListByStudentId(student.getId());
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                    List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
                    for (SubjectCurriculumEntity s : list) {
                        if (!subjects.contains(s)) {
                            subjects.add(s);
                            if (s.getSubjectId().getType() == SubjectTypeEnum.OJT.getId()) {
                                ojtCredits = s.getSubjectCredits();
                            }
                            if (s.getSubjectId().getType() == SubjectTypeEnum.Capstone.getId()) {
                                capstoneSubject = s.getSubjectId();
                                break;
                            }
                        }
                    }
                }
            }

            boolean req = false;
            if (map.stream().anyMatch(c -> c.getStudentId().getId() == student.getId())) {
                req = true;
            }

            if (!req) {
                int required = student.getProgramId().getSpecializedCredits();

                int percent = student.getProgramId().getCapstone();

                int tongtinchi = student.getPassCredits();
                if (hasOJT.stream().anyMatch(c -> c.getStudentId().getId() == student.getId())) {
                    tongtinchi = tongtinchi - ojtCredits;
                }

                List<String> t = new ArrayList<>();
                t.add(student.getRollNumber());
                t.add(student.getFullName());
                t.add(String.valueOf(student.getTerm()));
                t.add(String.valueOf(tongtinchi));
                t.add(String.valueOf((int) ((required * percent * 1.0) / 100)));
                t.add(String.valueOf(student.getId()));

                if (isGraduate) {
                    ISubjectService subjectService = new SubjectServiceImpl();
                    if (tongtinchi >= (int) ((required * percent * 1.0) / 100)) {
                        List<String> processedData = new ArrayList<>();
                        if (capstoneSubject != null && capstoneSubject.getPrequisiteEntity() != null) {
                            String preSubs = capstoneSubject.getPrequisiteEntity().getPrequisiteSubs();
                            String[] rows = preSubs == null ? (capstoneSubject.getPrequisiteEntity().getNewPrequisiteSubs() == null ? new String[0] : capstoneSubject.getPrequisiteEntity().getNewPrequisiteSubs().split("OR")) : preSubs.split("OR");
                            for (String row : rows) {
                                row = row.replaceAll("\\(", "").replaceAll("\\)", "").trim();
                                String[] cells = row.split(",");
                                for (String cell : cells) {
                                    cell = cell.trim();
                                    SubjectEntity c = subjectService.findSubjectById(cell);
                                    if (c != null) {
                                        processedData.add(cell);

                                        if (!c.getSubjectEntityList().isEmpty()) {
                                            for (SubjectEntity replaces : c.getSubjectEntityList()) {
                                                processedData.add(replaces.getId());
                                            }
                                        }
                                        if (!c.getSubjectEntityList1().isEmpty()) {
                                            for (SubjectEntity replaces : c.getSubjectEntityList1()) {
                                                processedData.add(replaces.getId());

                                                for (SubjectEntity r : replaces.getSubjectEntityList()) {
                                                    processedData.add(r.getId());
                                                }
                                            }
                                        }
                                    }
                                }
                                if (!capstoneSubject.getSubjectEntityList().isEmpty()) {
                                    for (SubjectEntity replaces : capstoneSubject.getSubjectEntityList()) {
                                        processedData.add(replaces.getId());
                                    }
                                }
                                if (!capstoneSubject.getSubjectEntityList1().isEmpty()) {
                                    for (SubjectEntity replaces : capstoneSubject.getSubjectEntityList1()) {
                                        processedData.add(replaces.getId());

                                        for (SubjectEntity r : replaces.getSubjectEntityList()) {
                                            processedData.add(r.getId());
                                        }
                                    }
                                }
                            }
                        } else {
                            System.out.println("khong check dc do an => cho vao` de sau nay tinh");
//                            data.add(t);
                        }

                        boolean failed = false;
                        processedData = processedData.stream().distinct().collect(Collectors.toList());
                        if (!processedData.isEmpty()) {
                            List<String> finalProcessedData = processedData;
                            List<MarksEntity> list3 = markService.getAllMarksByStudent(student.getId())
                                    .stream()
                                    .filter(c -> finalProcessedData.stream().anyMatch(b -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(b)))
                                    .collect(Collectors.toList());
                            failed = Ultilities.IsFailedSpecial(list3, capstoneSubject.getPrequisiteEntity());
                        }

                        if (!failed) {
                            data.add(t);
                        }
//                        data.add(t);
                    }
                } else {
                    if (tongtinchi < (int) ((required * percent * 1.0) / 100)) {
                        data.add(t);
                    }
                }
            } else {
                System.out.println("dang hoc hoac da pass");
            }

            i++;
        }

        return data;
    }

    public List<List<String>> processCapstone2(Map<String, String> params) {
        List<List<String>> data = new ArrayList<>();

        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));

        IRealSemesterService service = new RealSemesterServiceImpl();
        RealSemesterEntity semester = service.findSemesterById(semesterId);

        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));
        int previousSemesterId = Ultilities.GetSemesterIdBeforeThisId(semester.getId());
        List<StudentEntity> students;
        if (programId < 0) {
            students = studentService.findAllStudents();
        } else {
            students = studentService.getStudentBySemesterIdAndProgram(previousSemesterId, programId);
        }

        EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = fac.createEntityManager();


        students = students.stream().filter(c -> isCapstone(c, previousSemesterId)).collect(Collectors.toList());
//        students = students.stream().filter(c -> c.getTerm() >= 5).collect(Collectors.toList());

        //query for students already pass or learning capstone
        List<StudentEntity> alreadyCapstone = markService.getCapstoneStudentsBeforeSelectedSemesterFromMarks(semesterId);

        List<StudentEntity> hasOJT = markService.getOjtStudentsBeforeSelectedSemesterFromMarks(semesterId);

        IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();

        int i = 1;

        MarksServiceImpl marksService = new MarksServiceImpl();
        SubjectServiceImpl subjectService = new SubjectServiceImpl();
        List<RealSemesterEntity> sortedSemester = Global.getSortedList();
        List<SubjectEntity> allSubjects = subjectService.getAllSubjects();
        List<MarksEntity> totalMarks = marksService.getMarkByConditions(previousSemesterId, null, -1);

        PrerequisiteServiceImpl prerequisiteService = new PrerequisiteServiceImpl();
        List<PrequisiteEntity> allPrerequisiteEntityList = prerequisiteService.getAllPrerequisite();

        List<StudentEntity> uncheckable = new ArrayList<>();
        loopStudents:
        for (StudentEntity student : students) {

            System.out.println(i + " - " + students.size());
            if (i == 41) {
                System.out.println("bug");
            }

            List<SubjectCurriculumEntity> subjects = new ArrayList<>();

            SubjectEntity capstoneSubject = null;
            int ojtCredits = 0;
            List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
            //lấy tín chỉ chuyên ngành
            int required = 0;
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                    CurriculumEntity curriculum = doc.getCurriculumId();
                    //học sinh liên thông sẽ không được xét
                    if (curriculum.getProgramId().getName().contains("lt")) {
                        //add những sinh viên không được xét vào 1 mảng và tiếp tục vòng lặp xét sinh viên
                        uncheckable.add(student);
                        continue loopStudents;
                    }
                    required += curriculum.getSpecializedCredits();
                    List<SubjectCurriculumEntity> list = curriculum.getSubjectCurriculumEntityList();
                    for (SubjectCurriculumEntity s : list) {

                        if (!subjects.contains(s)) {
                            subjects.add(s);
                            if (s.getSubjectId().getType() == SubjectTypeEnum.OJT.getId()) {
                                ojtCredits = s.getSubjectCredits();
                            }
                            if (s.getSubjectId().getType() == SubjectTypeEnum.Capstone.getId()) {
                                capstoneSubject = s.getSubjectId();
//                                break;
                            }
                        }
                    }
                }
            }

            //kiểm tra xem sinh viên đã pass hoặc đang học capstone chưa (nếu rồi thì next)
            boolean req = false;
            if (alreadyCapstone.stream().anyMatch(c -> c.getId() == student.getId())) {
                req = true;
            }

            if (!req) {

                int percent = student.getProgramId().getCapstone();

                List<MarksEntity> allStudentMarks = totalMarks.stream()
                        .filter(q -> q.getStudentId().getId() == student.getId()).collect(Collectors.toList());


                List<SubjectEntity> failSubjs = new ArrayList<>();

                //!****Tính lại tổng tín chỉ ở đây ****!
                //get all passed credit of student
                int tongtinchi = 0;
                for (SubjectCurriculumEntity subjectCurriculum : subjects) {
                    SubjectEntity itemSubject = subjectCurriculum.getSubjectId();
                    //contains main subject and all of it replace subject
                    List<SubjectEntity> checkList = new ArrayList<>();
                    checkList.add(itemSubject);
                    //exclude vovinam subject out
                    if (!itemSubject.getId().contains("vov")) {
                        checkList.addAll(itemSubject.getSubjectEntityList());
                        checkList.addAll(itemSubject.getSubjectEntityList1());
                        //lấy hết tất cả điểm của môn chính và môn thay thế của nó để kiểm tra xem đã pass chưa
                        List<MarksEntity> marks = allStudentMarks.stream().filter(q -> checkList.stream()
                                .anyMatch(c -> c.getId().equalsIgnoreCase(q.getSubjectMarkComponentId().getSubjectId().getId())))
                                .collect(Collectors.toList());
                        //sort by semester
                        marks = Ultilities.SortSemestersByMarks(marks);
                        if (!marks.isEmpty()) {
                            MarksEntity latestMark = marks.get(marks.size() - 1);
                            boolean isFail = Ultilities.isLatestMarkFailOrNotVer2(latestMark, marks);
                            if (!isFail) {
                                tongtinchi += subjectCurriculum.getSubjectCredits();
                            } else {
                                failSubjs.add(itemSubject);
                            }
                        } else {
                            failSubjs.add(itemSubject);
                        }
                    }
                }

                //code cũ tính tổng tín chỉ
//                int tongtinchi = student.getPassCredits();

                //remove Ojt credit
                tongtinchi -= ojtCredits;

                required -= ojtCredits;

                //check if student has learn ojt
                boolean hasLearnedOjt = false;
                if (hasOJT.stream().anyMatch(c -> c.getId() == student.getId())) {
                    hasLearnedOjt = true;
                }

                List<String> t = new ArrayList<>();
                t.add(student.getRollNumber());
                t.add(student.getFullName());
                t.add(String.valueOf(student.getTerm()));
                t.add(String.valueOf(tongtinchi));
                t.add(String.valueOf((int) ((required * percent * 1.0) / 100)));
                t.add(String.valueOf(student.getId()));

                if (student.getRollNumber().equalsIgnoreCase("SE61494")) {
                    System.out.println("bug");
                }

                if (isGraduate) {
                    //check if student has taken Ojt
                    if (hasLearnedOjt) {
                        if (tongtinchi >= (int) ((required * percent * 1.0) / 100)) {
                            if (capstoneSubject != null && capstoneSubject.getPrequisiteEntity() != null) {

                                //check fail prerequesite for Capstone
                                boolean isFailed = Ultilities.isSubjectFailedPrerequisite(capstoneSubject,
                                        sortedSemester, allSubjects, allStudentMarks, semester, allPrerequisiteEntityList);

                                if (!isFailed) {
                                    data.add(t);
                                }
                            } else {
                                System.out.println("khong check dc do an => cho vao` de sau nay tinh");
                            }
                        }
                    }
                } else {
                    if (tongtinchi < (int) ((required * percent * 1.0) / 100)) {
                        data.add(t);
                    } else {
                        if (capstoneSubject != null) {
                            boolean isFailed = Ultilities.isSubjectFailedPrerequisite(capstoneSubject,
                                    sortedSemester, allSubjects, allStudentMarks, semester, allPrerequisiteEntityList);

                            if (isFailed) {
                                data.add(t);
                            }
                        }
                    }
                }
            } else {
                System.out.println("dang hoc hoac da pass");
            }

            i++;
        }

        return data;
    }

    //dùng cho việc update lại Ojt term = 6, dùng 1 lần duy nhất
//    @RequestMapping(value = "/updateOjtTerm", method = RequestMethod.POST)
//    @ResponseBody
//    public JsonObject goUpdateOjtTerm() {
//        JsonObject jsonObj = new JsonObject();
//
//        try {
//            CurriculumServiceImpl curriculumService = new CurriculumServiceImpl();
//            List<CurriculumEntity> allCurriculum = curriculumService.getAllCurriculums();
//
//            int i = 1;
//            for (CurriculumEntity curriculum :
//                    allCurriculum) {
//
//                if (!curriculum.getName().contains("pc")) {
//
//                    List<SubjectCurriculumEntity> subjectCurriculumEntityList = curriculum.getSubjectCurriculumEntityList();
//
//                    //loại curriculum của Ojt và Cn hẹp
//                    List<SubjectCurriculumEntity> tmp = subjectCurriculumEntityList.stream().filter(q -> q.getSubjectId().getType() == 1 || q.getSubjectId().getType() == 2)
//                            .collect(Collectors.toList());
//                    if (tmp.isEmpty()) {
//                        curriculum.setOjtTerm(6);
//                        curriculumService.updateCurriculum(curriculum);
//                    }
//                }
//                System.out.println("Done -" + i);
//                i++;
//            }
//
//            jsonObj.addProperty("success", true);
//            jsonObj.addProperty("message", "Done");
//        }catch(Exception e){
//            jsonObj.addProperty("success", false);
//            jsonObj.addProperty("message", "Bug");
//            e.printStackTrace();
//        }
//
//        return jsonObj;
//    }

    //dùng cho việc update lại Student term hiện tại = 6, dùng 1 lần duy nhất
    @RequestMapping(value = "/updateStudentTerm", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goupdateStudentTerm() {
        JsonObject jsonObj = new JsonObject();

        try {
            StudentStatusServiceImpl studentStatusService = new StudentStatusServiceImpl();
            //lấy của kì FALl2017
            List<StudentStatusEntity> studentStatusList = studentStatusService.getStudentStatusBySemesterId(28);
            StudentServiceImpl studentService = new StudentServiceImpl();
            int i = 1;
            for (StudentStatusEntity studentStatus :
                    studentStatusList) {

                //term đúng
                String rightTerm = studentStatus.getTerm();
                if (rightTerm != null) {
                    Integer currentTerm = null;
                    if (rightTerm.contains("ENG6")) {
                        currentTerm = 0;
                    } else if (rightTerm.contains("ENG5")) {
                        currentTerm = -1;
                    } else if (rightTerm.contains("ENG4")) {
                        currentTerm = -2;
                    } else if (rightTerm.contains("ENG3")) {
                        currentTerm = -3;
                    } else if (rightTerm.contains("ENG2")) {
                        currentTerm = -4;
                    }

                    if (currentTerm == null) {
                        try {
                            currentTerm = (int) Double.parseDouble(rightTerm);

                        } catch (NumberFormatException ex) {
                            System.out.println(studentStatus.getStudentId() + " - " + rightTerm);
                        }
                    }

                    StudentEntity student = studentStatus.getStudentId();
                    student.setTerm(currentTerm);
                    studentService.myUpdateStudent(student);
                }
            }

            jsonObj.addProperty("success", true);
            jsonObj.addProperty("message", "Done");
        } catch (Exception e) {
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("message", "Bug");
            e.printStackTrace();
        }

        return jsonObj;
    }


}


