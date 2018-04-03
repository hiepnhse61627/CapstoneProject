package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.enums.SubjectTypeEnum;
import com.capstone.exporters.ExportExcelGraduatedStudentsImpl;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;


@Controller
public class GraduateController {


    IProgramService programService = new ProgramServiceImpl();
    IRealSemesterService semesterService = new RealSemesterServiceImpl();
    IMarksService markService = new MarksServiceImpl();
    IStudentService studentService = new StudentServiceImpl();
    ISubjectCurriculumService subjectCurriculumService = new SubjectCurriculumServiceImpl();
    StudentStatusServiceImpl studentStatusService = new StudentStatusServiceImpl();

    // home page
    @RequestMapping("/graduate")
    public ModelAndView Index(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

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
//            if (studentEntity.getRollNumber().equalsIgnoreCase("SE61726")) {
//                System.out.println("bug");
//            }
            List<StudentStatusEntity> studentStatusEntities = new ArrayList<>(studentEntity.getStudentStatusEntityList());
            //lấy ra status của sinh viên
            List<StudentStatusEntity> tempStatus = studentStatusEntities.stream()
                    .filter(q -> q.getSemesterId().getId() == previousSemesterId && q.getStatus().equalsIgnoreCase("G"))
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

//            if (student.getRollNumber().equalsIgnoreCase("SE61726")) {
//                System.out.println("bug");
//            }
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
            //subject notStart hoặc fail
            List<SubjectEntity> failSubjs = new ArrayList<>();
            int specialRequiredSubjectCredit = 0;

            //duyệt tất cả những môn có trong khung chương trình
            subjectCurriculumLoop:
            for (SubjectCurriculumEntity subjectCurriculum : subjects) {
                SubjectEntity subject = subjectCurriculum.getSubjectId();
                if (subjectCurriculum.isRequired()) {
                    specialRequiredSubjectCredit += subjectCurriculum.getSubjectCredits();
                }

                //mảng này chứa tất cả môn thay thế và môn chính
                List<SubjectEntity> checkSubjects = Ultilities.findBackAndForwardReplacementSubject(subject);


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
                    } else if (isFailed && subjectCurriculum.isRequired()) {
                        failSubjs.add(subject);
                        passedFlag = false;
                    }
                }

            }

            //trừ tín chỉ Ojt
            specializedCredits -= 10;
            studentCredits -= 10;
            specialRequiredSubjectCredit -=10;


            if (isGraduate) {
                if ((studentCredits >= specialRequiredSubjectCredit) && (passedFlag)) {
                    List<String> t = new ArrayList<>();
                    t.add(student.getRollNumber());
                    t.add(student.getFullName());
                    t.add(String.valueOf(student.getTerm()));
                    t.add(String.valueOf(studentCredits));
                    t.add(String.valueOf(specialRequiredSubjectCredit));
                    t.add(String.valueOf(student.getId()));
                    data.add(t);
                }
            } else {
                if ((studentCredits < specialRequiredSubjectCredit) || (!passedFlag)) {
                    List<String> t = new ArrayList<>();
                    t.add(student.getRollNumber());
                    t.add(student.getFullName());
                    t.add(String.valueOf(student.getTerm()));
                    t.add(String.valueOf(studentCredits));
                    t.add(String.valueOf(specialRequiredSubjectCredit));
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
        try {


            int programId = Integer.parseInt(params.get("programId"));
            int semesterId = Integer.parseInt(params.get("semesterId"));

            IRealSemesterService service = new RealSemesterServiceImpl();
            RealSemesterEntity semester = service.findSemesterById(semesterId);
            IMarksService marksService = new MarksServiceImpl();


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


            // lấy điểm theo trước semester được chọn
            List<MarksEntity> totalMarks = marksService.getMarkByConditions(previousSemesterId, null, -1);
            totalMarks = Ultilities.SortSemestersByMarks(totalMarks);

            students = students.stream().filter(c -> isOJT(c, previousSemesterId)).collect(Collectors.toList());

            //lấy danh sách điểm những sinh viên đã pass hoặc đang học ojt theo kì trước kì được chọn
            List<StudentEntity> map = marksService.getOjtStudentsFromSelectedSemesterAndBeforeFromMarks(previousSemesterId);

            int i = 1;
            for (StudentEntity student : students) {

                System.out.println(i + " - " + students.size());

                if (student.getRollNumber().equalsIgnoreCase("SE61576")) {
                    System.out.println("bug");
                }
                if (i == 522 || i == 533) {
                    System.out.println("bug");
                }

                //kì mặc định đi Ojt (phòng hờ trường hợp curriculum k có kì đi ojt)
                int ojt = Enums.SpecialTerm.OJTTERM.getValue();
                //tổng tín chỉ yêu cầu để đi Ojt
                int required = 0;

                List<SubjectCurriculumEntity> subjects = new ArrayList<>();

                //lấy tín kì mà sinh viên đi OJT
                List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
                for (DocumentStudentEntity doc : docs) {
                    if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                        CurriculumEntity curriculum = doc.getCurriculumId();

                        //tính tín chỉ Chuyên Ngành, ko tính vovinam (nếu có trong khung)
                        List<SubjectCurriculumEntity> list = curriculum.getSubjectCurriculumEntityList();
                        for (SubjectCurriculumEntity s : list) {
                            if (!subjects.contains(s) && s.getTermNumber() < ojt
                                    && !s.getSubjectId().getId().contains("vov")) {
                                subjects.add(s);
                                required += s.getSubjectCredits();
                            }
                        }

                        //lấy kì đi Ojt
                        Integer tmpOjt = curriculum.getOjtTerm();
                        if (tmpOjt != null) {
                            ojt = tmpOjt;
                        }
                    }
                }


                boolean req = false;
                // loại những sinh viên đã có điểm hoặc đang học Ojt
                if (map.stream().anyMatch(q -> q.getId() == student.getId())) {
                    req = true;
                }
                if (!req) {

                    //tổng số % * tín chỉ chuyên ngành trước
                    int percent = student.getProgramId().getOjt();

                    //tín chỉ tích lũy
                    int tongtinchi = 0;
                    //lấy ra tất cả những subjectId
                    List<String> tmp = subjects.stream().map(c -> c.getSubjectId().getId()).distinct().collect(Collectors.toList());
                    List<MarksEntity> studentMarks = totalMarks
                            .stream()
                            .filter(c -> c.getStudentId().getId() == student.getId())
                            .filter(c -> tmp.stream().anyMatch(a -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(a)))
                            .collect(Collectors.toList());

                    //tính tổng tín chỉ tích lũy của sinh viên
                    for (SubjectCurriculumEntity subjectCurriculum : subjects) {
                        SubjectEntity itemSubject = subjectCurriculum.getSubjectId();

                        //contains main subject and all of it replace subject
                        List<SubjectEntity> checkList = Ultilities.findBackAndForwardReplacementSubject(itemSubject);

                        //lấy hết tất cả điểm của môn chính và môn thay thế của nó để kiểm tra xem đã pass chưa
                        List<MarksEntity> marks = studentMarks.stream().filter(q -> checkList.stream()
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

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }

    // finda all atudents match OJT term
    private boolean isOJT(StudentEntity student, int previousSemesterId) {
        int ojt = Enums.SpecialTerm.OJTTERM.getValue();
        List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
        for (DocumentStudentEntity doc : docs) {
            if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
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
        int capstone = Enums.SpecialTerm.CAPSTONETERM.getValue();
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
        MarksServiceImpl marksService = new MarksServiceImpl();
        SubjectServiceImpl subjectService = new SubjectServiceImpl();
        PrerequisiteServiceImpl prerequisiteService = new PrerequisiteServiceImpl();

        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));
        int previousSemesterId = Ultilities.GetSemesterIdBeforeThisId(semester.getId());
        List<StudentEntity> students;
        if (programId < 0) {
            students = studentService.findAllStudents();
        } else {
            students = studentService.getStudentBySemesterIdAndProgram(previousSemesterId, programId);
        }

        students = students.stream().filter(c -> isCapstone(c, previousSemesterId)).collect(Collectors.toList());

        //query for students already pass or learning capstone
        List<StudentEntity> alreadyCapstone = markService.getCapstoneStudentsBeforeSelectedSemesterFromMarks(semesterId);
        List<StudentEntity> hasOJT = markService.getOjtStudentsBeforeSelectedSemesterFromMarks(semesterId);


        List<RealSemesterEntity> sortedSemester = Global.getSortedList();
        List<SubjectEntity> allSubjects = subjectService.getAllSubjects();
        List<MarksEntity> totalMarks = marksService.getMarkByConditions(previousSemesterId, null, -1);
        List<PrequisiteEntity> allPrerequisiteEntityList = prerequisiteService.getAllPrerequisite();
        List<StudentEntity> uncheckable = new ArrayList<>();

        int i = 1;
        loopStudents:
        for (StudentEntity student : students) {

            System.out.println(i + " - " + students.size());
            if (student.getRollNumber().equalsIgnoreCase("SE61822")) {
                System.out.println("bug");
            }

            List<SubjectCurriculumEntity> subjects = new ArrayList<>();
            List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();

            SubjectEntity capstoneSubject = null;
            int capstoneTerm = Enums.SpecialTerm.CAPSTONETERM.getValue();
            int ojtCredits = 0;

            //lấy tín chỉ chuyên ngành
            int required = 0;
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                    CurriculumEntity curriculum = doc.getCurriculumId();
                    //học sinh liên thông sẽ không được xét
                    //add những sinh viên không được xét vào 1 mảng và tiếp tục vòng lặp xét sinh viên
                    if (curriculum.getProgramId().getName().contains("lt")) {
                        uncheckable.add(student);
                        continue loopStudents;
                    }
                    List<SubjectCurriculumEntity> list = curriculum.getSubjectCurriculumEntityList();
                    //chỉ lấy những subject không nằm trong kì chung với kì làm capstone
                    for (SubjectCurriculumEntity s : list) {
                        if (!subjects.contains(s) && s.getTermNumber() < capstoneTerm) {
                            required += s.getSubjectCredits();
                            subjects.add(s);
                            if (s.getSubjectId().getType() == SubjectTypeEnum.OJT.getId()) {
                                ojtCredits = s.getSubjectCredits();
                            }
                        }
                        if (s.getSubjectId().getType() == Enums.SubjectType.CAPSTONE.getValue()) {
                            capstoneSubject = s.getSubjectId();
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
                List<MarksEntity> studentMarks = totalMarks.stream()
                        .filter(q -> q.getStudentId().getId() == student.getId()).collect(Collectors.toList());
                List<SubjectEntity> failSubjs = new ArrayList<>();


                //tính tổng tín chỉ tích lũy
                int tongtinchi = 0;
                for (SubjectCurriculumEntity subjectCurriculum : subjects) {
                    SubjectEntity itemSubject = subjectCurriculum.getSubjectId();

                    //exclude vovinam subject out
                    if (!itemSubject.getId().contains("vov") && subjectCurriculum.getTermNumber() < capstoneTerm) {

                        //contains main subject and all of it replace subject
                        List<SubjectEntity> checkList = Ultilities.findBackAndForwardReplacementSubject(itemSubject);

                        //lấy hết tất cả điểm của môn chính và môn thay thế của nó để kiểm tra xem đã pass chưa
                        List<MarksEntity> marks = studentMarks.stream().filter(q -> checkList.stream()
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
                                        sortedSemester, allSubjects, studentMarks, semester, allPrerequisiteEntityList);

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
                                    sortedSemester, allSubjects, studentMarks, semester, allPrerequisiteEntityList);

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


    @RequestMapping(value = "/sendGraduateStudent", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> SendEmail(Map<String, String> params, HttpServletRequest request,
                                          @RequestParam("username") String username,
                                          @RequestParam("token") String token, @RequestParam("name") String name,
                                          @RequestParam("programId") String programId,
                                          @RequestParam("semesterId") String semesterId,
                                          @RequestParam("notSendStudents") List<String> notSendStudents) {
        Ultilities.logUserAction("Send emails graduate");

        Callable<JsonObject> callable = () -> {
            JsonObject obj = new JsonObject();


            try {
                HttpSession session = request.getSession();
                List<StudentAndMark> data = (List<StudentAndMark>)
                        session.getAttribute(Enums.GraduateVariable.GRADUATE_LIST.getValue());
                //thesisName<MSSV, List<Tên đồ án tiếng việt, tên đồ án tiếng anh>>
                HashMap<String, List<String>> thesisNames = (HashMap<String, List<String>>)
                        session.getAttribute(Enums.GraduateVariable.ThesisName_List.getValue());
                //giấy tờ cần thiết để xét duyệt tốt nghiệp
                HashMap<String, RequiredDocuments> requiredDocuments = (HashMap<String, RequiredDocuments>)
                        session.getAttribute(Enums.GraduateVariable.Required_Documents.getValue());

                int requestProgramId = Integer.parseInt(programId);
                int requestSemesterId = Integer.parseInt(semesterId);

                Integer currentProgramId = (Integer) request.getSession()
                        .getAttribute(Enums.GraduateVariable.PROGRAM_ID.getValue());
                Integer currentSemesterId = (Integer) request.getSession()
                        .getAttribute(Enums.GraduateVariable.SEMESTER_ID.getValue());

                //lấy data sinh viên đủ dk tốt nghiệp
                if (data == null || currentProgramId == null || currentSemesterId == null
                        || currentProgramId != requestProgramId
                        || currentSemesterId != requestSemesterId) {
                    data = processData2(params, requestSemesterId, requestProgramId);

                    //set lên session nếu chưa có
                    session
                            .setAttribute(Enums.GraduateVariable.PROGRAM_ID.getValue(), requestProgramId);
                    session
                            .setAttribute(Enums.GraduateVariable.SEMESTER_ID.getValue(), requestSemesterId);
                    session
                            .setAttribute(Enums.GraduateVariable.GRADUATE_LIST.getValue(), data);
                }
                //set tên đồ án
                if (thesisNames != null && !thesisNames.isEmpty()) {
                    for (StudentAndMark item : data) {
                        StudentEntity student = item.getStudent();
                        //List<Tên đồ án tiếng việt, tên đồ án tiếng anh>
                        List<String> names = thesisNames.get(student.getRollNumber());
                        if (names != null) {
                            item.setEngThesisName(names.get(0));
                            item.setVnThesisName(names.get(1));
                        }
                    }
                }
                //set giấy tờ cần thiết cho tốt nghiệp
                if (requiredDocuments != null && !requiredDocuments.isEmpty()) {
                    for (StudentAndMark item : data) {
                        StudentEntity student = item.getStudent();
                        RequiredDocuments rd = requiredDocuments.get(student.getRollNumber());
                        item.setHighschoolGraduate(rd.hasHighschoolGraduate());
                        item.setBirthRecords(rd.hasBirthRecords());
                        item.setIdCard(rd.hasIdCard());
                        item.setDueDate(item.getDueDate());
                        item.setGraduateTime(item.getGraduateTime());
                    }
                }


                OAuth2Authenticator.initialize();
                SMTPTransport smtpTransport = OAuth2Authenticator.connectToSmtp("smtp.gmail.com", 587, username, token, true);

                //khởi tạo Marshaller
                JAXBContext jc = JAXBContext.newInstance(StudentAndMark.class);
                Marshaller mar = jc.createMarshaller();
                mar.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
                mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                String location = GraduateController.class.getProtectionDomain().getCodeSource().getLocation().getPath();

                String realPath = location.substring(0, location.indexOf("classes")) + "MailTemplate/";

                loopData:
                for (StudentAndMark item : data) {
                    StudentEntity student = item.getStudent();
                    //nếu sinh viên có trong mảng (sinh viên không được gửi) thì không gửi mail cho sinh viên đó
                    if (notSendStudents.contains(student.getRollNumber())) {
                        continue loopData;
                    }
                    String email = student.getEmail();
                    Session oAuth2session = OAuth2Authenticator.getSession();
                    MimeMessage mimeMessage = new MimeMessage(oAuth2session);
                    Address toAddress = new InternetAddress(email);
                    Address fromAddress = new InternetAddress(username, name, "utf-8");

                    //biến data thành xml
                    StringWriter sw = new StringWriter();
                    mar.marshal(item, sw);
                    String xmlStr = sw.toString();

                    //khởi tạo transformer và định dạng template
                    TransformerFactory tf = TransformerFactory.newInstance();
                    File f = new File(realPath + "graduate_mail.xsl");
                    if (!f.exists()) {
                        System.out.println("not exist");
                    }
                    StreamSource xslt = new StreamSource(realPath + "graduate_mail.xsl");
                    Transformer trans = tf.newTransformer(xslt);

                    //đọc xml thành stream source
                    StreamSource xml = new StreamSource(new StringReader(xmlStr));

                    //khởi tạo outputStream để đọc html
                    StringWriter sw2 = new StringWriter();
                    StreamResult outStream = new StreamResult(sw2);

                    //transform xml thành html
                    trans.transform(xml, outStream);

                    //kết quả sau khi apply xml, stylesheet thành html
                    String html = sw2.toString();


                    String msg = html;
                    mimeMessage.setContent(msg, "text/html; charset=UTF-8");
                    mimeMessage.setFrom(fromAddress);
                    mimeMessage.setRecipient(Message.RecipientType.TO, toAddress);
                    mimeMessage.setSubject("[FUG-HCM] Bảng điểm học tập và xét tốt nghiệp", "utf-8");
                    smtpTransport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                }


                obj.addProperty("success", true);
            } catch (Exception e) {
                e.printStackTrace();
                obj.addProperty("success", false);
                obj.addProperty("msg", e.getMessage());
                e.printStackTrace();
            }

            return obj;
        };


        return callable;
    }


    private List<StudentAndMark> processData2(Map<String, String> params, int semesterId, int programId) {
        List<StudentAndMark> resultMap = new ArrayList<>();

        List<StudentEntity> studentEntityList;
        if (programId < 0) {
            studentEntityList = studentService.findAllStudents();
        } else {
            studentEntityList = studentService.getStudentBySemesterIdAndProgram(semesterId, programId);
        }
//
        List<StudentEntity> filteredStudents = new ArrayList<>();
        List<StudentStatusEntity> allStatus = studentStatusService.getStudentStatusBySemesterId(semesterId);

        for (StudentEntity student : studentEntityList) {
            List<StudentStatusEntity> filterStatus = allStatus.stream().filter(q -> q.getStudentId().getId() == student.getId()
                    && q.getStatus().equalsIgnoreCase(Enums.StudentStatus.HOCDI.getValue()))
                    .collect(Collectors.toList());

            //nếu sinh viên
            if (!filterStatus.isEmpty()) {
                filteredStudents.add(student);
            }
        }

        //lay danh sach status roi stream filter

        //use 4 test
//        List<StudentEntity> a = studentService.findAllStudents();
//        List<StudentEntity> temp = a.stream().filter(q -> q.getRollNumber().equalsIgnoreCase("SE61822")
//                || q.getRollNumber().equalsIgnoreCase("SE62094")
//                || q.getRollNumber().equalsIgnoreCase("SE62137")
//        ).collect(Collectors.toList());
//        filteredStudents.addAll(temp);

        System.out.println(filteredStudents.size() + " students");
        int i = 1;
        for (StudentEntity student : filteredStudents) {
            boolean failFlag = false;
            List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
            List<MarksEntity> allMarks = new ArrayList<>(student.getMarksEntityList());
            List<MarkCreditTermModel> finalMarks = new ArrayList<>();
            List<SubjectCurriculumEntity> subjectCurriculumList = new ArrayList<>();

            for (DocumentStudentEntity docStudent : docs) {
                CurriculumEntity curriculum = docStudent.getCurriculumId();
                subjectCurriculumList.addAll(curriculum.getSubjectCurriculumEntityList());
            }

            for (SubjectCurriculumEntity subjectCurriculum : subjectCurriculumList) {
                SubjectEntity subject = subjectCurriculum.getSubjectId();

                //mảng này chứa tất cả môn thay thế và môn chính
                List<SubjectEntity> checkSubjects = Ultilities.findBackAndForwardReplacementSubject(subject);
                List<MarksEntity> filteredMarks = allMarks.stream().filter(q -> checkSubjects.stream()
                        .anyMatch(c -> c.getId()
                                .equalsIgnoreCase(q.getSubjectMarkComponentId().getSubjectId().getId())))
                        .collect(Collectors.toList());
                List<MarksEntity> sortedMarks = Ultilities.SortSemestersByMarks(filteredMarks);

                //get latest mark
                if (!sortedMarks.isEmpty()) {
                    MarksEntity latestMark = sortedMarks.get(sortedMarks.size() - 1);
                    RealSemesterEntity tmpSemester = latestMark.getSemesterId();

                    //check xem trong một kì có học môn đó 2 lần không (trả nợ ngay trong kì)
                    List<MarksEntity> reLearnInSameSemester = sortedMarks.stream()
                            .filter(q -> q.getSemesterId().getId() == tmpSemester.getId())
                            .collect(Collectors.toList());

                    //nếu trong kì có 2 record, pass, fail --> hs đó pass (không được học cải thiện ngay trong kì)
                    // nếu có 2 fail --> fail; nếu có 1 pass, 1 fail -> pass
                    MarksEntity passMark = reLearnInSameSemester.stream()
                            .filter(q -> q.getStatus().equalsIgnoreCase(Enums.MarkStatus.PASSED.getValue()))
                            .findFirst().orElse(null);

                    if (passMark != null) {
                        finalMarks.add(new MarkCreditTermModel(passMark,
                                subjectCurriculum.getSubjectCredits(),
                                subjectCurriculum.getTermNumber() * 1.0));
                        failFlag = false;
                    } else {
                        //loại ra khỏi danh sách có thể tốt nghiệp
                        failFlag = true;
                        break;
                    }
                }

            } //end of subjectCurriculum loop

            if (!failFlag) {
                Collections.sort(finalMarks, new MarkCreditTermModelComparator());
                resultMap.add(new StudentAndMark(finalMarks, student));
            }
            System.out.println(i + " - " + filteredStudents.size());
            i++;
        }

        return resultMap;
    }

    //lấy sinh viên đủ dk tốt nghiệp
    @RequestMapping(value = "/getStudentGraduate4Mail", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goGetStudentGraduate(Map<String, String> params, HttpServletRequest request,
                                           @RequestParam("semesterId") int semesterId,
                                           @RequestParam("programId") int programId) {
        JsonObject data = new JsonObject();
        try {

            HttpSession session = request.getSession();
            List<StudentAndMark> list = processData2(params, semesterId, programId);
            session.setAttribute(Enums.GraduateVariable.PROGRAM_ID.getValue(), programId);
            session.setAttribute(Enums.GraduateVariable.SEMESTER_ID.getValue(), semesterId);
            session.setAttribute(Enums.GraduateVariable.GRADUATE_LIST.getValue(), list);

            List<String> result = new ArrayList<>();
            for (StudentAndMark item : list) {
                StudentEntity student = item.getStudent();
                result.add(student.getRollNumber());
            }
            JsonArray resultData = (JsonArray) new Gson().toJsonTree(result);
            data.addProperty("success", true);
            data.addProperty("message", "Tìm thành công");
            data.add("studentList", resultData);
        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("success", false);
            data.addProperty("message", e.getMessage());
        }
        return data;
    }


    // simulate change semester page
    @RequestMapping("/testMailTemplate")
    public ModelAndView ChangeSemester(HttpServletRequest request) {
//        if (!Ultilities.checkUserAuthorize(request)) {
//            return Ultilities.returnDeniedPage();
//        }
        //loggin user action
//        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView view = new ModelAndView("TestPage");


//        view.addObject("title", "Set semester");
//        view.addObject("semesters", Global.getSortedList());
//        view.addObject("temporarySemester", Global.getTemporarySemester().getId());
//        view.addObject("currentSemester", Global.getCurrentSemester().getId());
        return view;
    }


}


