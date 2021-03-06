package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.ChangeCurriculumModel;
import com.capstone.models.Logger;
import com.capstone.models.MarkModel;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.scene.control.TextFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebParam;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/managerrole")
public class ManagerController {

    @RequestMapping("/changecurriculum")
    public ModelAndView Index(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView view = new ModelAndView("ChangeCurriculum");
        view.addObject("title", "Đổi ngành");
        IStudentService studentService = new StudentServiceImpl();
        List<StudentEntity> list = studentService.findAllStudents();
        view.addObject("students", list);
        ICurriculumService curriculumService = new CurriculumServiceImpl();
        List<CurriculumEntity> list2 = curriculumService.getAllCurriculums();
        list2.sort(Comparator.comparing(c -> c.getName()));
        view.addObject("curs", list2);
        return view;
    }

    @RequestMapping("/studentCurriculumDetail")
    public ModelAndView studentCurriculumDetail(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView view = new ModelAndView("StudentCurriculumDetail");
        view.addObject("title", "Đổi ngành");
        IStudentService studentService = new StudentServiceImpl();
        List<StudentEntity> list = studentService.findAllStudentsWithoutCurChange();
        view.addObject("studentsFilter", list);
        ICurriculumService curriculumService = new CurriculumServiceImpl();
        List<CurriculumEntity> list2 = curriculumService.getAllCurriculums();
        list2.sort(Comparator.comparing(c -> c.getProgramId().getName() + "_" + c.getName()));
        view.addObject("curs", list2);
        return view;
    }

    @RequestMapping("/averageStudentInClass")
    public ModelAndView AverageClass(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView view = new ModelAndView("AverageStudentInClass");
        view.addObject("title", "Sĩ số trung bình lớp môn học theo kỳ");

        IRealSemesterService semesterService = new RealSemesterServiceImpl();
        List<RealSemesterEntity> semesters = semesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
        view.addObject("semesters", semesters);

        return view;
    }

    @RequestMapping("/averageSubject")
    public ModelAndView AverageSubject(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView view = new ModelAndView("AverageSubject");
        view.addObject("title", "Sĩ số trung bình môn đã học trên một sinh viên");

        IProgramService programService = new ProgramServiceImpl();
        List<ProgramEntity> programs = programService.getAllPrograms();
        view.addObject("programs", programs);

        return view;
    }

    @RequestMapping("/getDetailInfo")
    @ResponseBody
    public JsonObject GetDetailInfo(@RequestParam(value = "stuId") int stuId, @RequestParam Map<String, String> params) {
        JsonObject json = new JsonObject();
        try {

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();
            int studentId = stuId;

            String queryStr;
            if (stuId == -1) {
                queryStr = "select distinct s.RollNumber,s.FullName,sm.SubjectId,sb.Name, m.IsActivated " +
                        "from Marks m, Student s, Subject_MarkComponent sm, Subject sb " +
                        "where m.StudentId = s.Id and m.Status = 'Passed' and m.IsActivated = 0 " +
                        "and sm.Id = m.SubjectMarkComponentId AND sb.Id = sm.SubjectId";

                Query query = em.createNativeQuery(queryStr);
                List<Object[]> searchList = query.getResultList();
                List<List<String>> result = new ArrayList<>();
                for (Object[] data : searchList) {
                    List<String> row = new ArrayList<String>();
                    row.add(data[0].toString());
                    row.add(data[1].toString());
                    row.add(data[2].toString());
                    row.add(data[3].toString());
                    row.add(data[4].toString());
                    result.add(row);
                }

                JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

                json.addProperty("iTotalRecords", searchList.size());
                json.addProperty("iTotalDisplayRecords", searchList.size());
                json.add("aaData", aaData);
                json.addProperty("sEcho", params.get("sEcho"));
            } else {
                queryStr = "select distinct s.RollNumber, s.FullName,sm.SubjectId,sb.Name, m.IsActivated " +
                        "from Marks m, Student s, Subject_MarkComponent sm, Subject sb " +
                        "where m.StudentId = s.Id and m.Status = 'Passed' and m.IsActivated = 0 " +
                        "and sm.Id = m.SubjectMarkComponentId AND sb.Id = sm.SubjectId and s.Id = ?";
                Query query = em.createNativeQuery(queryStr);
                query.setParameter(1, studentId);

                List<Object[]> searchList = query.getResultList();
                List<List<String>> result = new ArrayList<>();
                for (Object[] data : searchList) {
                    List<String> row = new ArrayList<String>();
                    row.add(data[0].toString());
                    row.add(data[1].toString());
                    row.add(data[2].toString());
                    row.add(data[3].toString());
                    row.add(data[4].toString());
                    result.add(row);
                }
                JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

                json.addProperty("iTotalRecords", searchList.size());
                json.addProperty("iTotalDisplayRecords", searchList.size());
                json.add("aaData", aaData);
                json.addProperty("sEcho", params.get("sEcho"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @RequestMapping("/getinfo")
    @ResponseBody
    public JsonObject GetInfo(@RequestParam(value = "stuId") int stuId) {
        JsonObject result = new JsonObject();

        try {
            IStudentService studentService = new StudentServiceImpl();
            StudentEntity student = studentService.findStudentById(stuId);
            result.addProperty("info", student.getProgramId().getName());
            List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
            docs = docs.stream().sorted(Comparator.comparing(c -> c.getCreatedDate())).collect(Collectors.toList());
            docs = Lists.reverse(docs);
            result.addProperty("khung", docs.get(0).getCurriculumId().getName());
            result.addProperty("curriculum", docs.get(0).getCurriculumId().getId());
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return result;
    }

    @RequestMapping("/getdocuments")
    @ResponseBody
    public JsonObject GetDocuments(@RequestParam Map<String, String> params) {
        int stuId = Integer.parseInt(params.get("stuId"));
        JsonObject result = new JsonObject();

        try {
            IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();
            List<Integer> list = new ArrayList<>();
            list.add(stuId);
            List<DocumentStudentEntity> docs = documentStudentService.getDocumentStudentByByStudentId(list);
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            docs = docs.stream().sorted(Comparator.comparing(c -> c.getCreatedDate())).collect(Collectors.toList());
            docs = Lists.reverse(docs);

            List<List<String>> parent = new ArrayList<>();
            if (!docs.isEmpty()) {
                docs.forEach(c -> {
                    List<String> tmp = new ArrayList<>();
                    tmp.add(c.getCurriculumId() == null ? "N/A" : c.getCurriculumId().getName());
                    tmp.add(c.getDocumentId().getCode() + " - " + c.getDocumentId().getDocTypeId().getName());
                    tmp.add(c.getCreatedDate() == null ? "N/A" : df.format(c.getCreatedDate()));
                    parent.add(tmp);
                });
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(parent);

            result.addProperty("iTotalRecords", parent.size());
            result.addProperty("iTotalDisplayRecords", parent.size());
            result.add("aaData", aaData);
            result.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return result;
    }

    @RequestMapping("/averageStudentInClass/getList")
    @ResponseBody
    public JsonObject GetAverageStudentInClassForDataTable(@RequestParam Map<String, String> params) {
        IMarksService marksService = new MarksServiceImpl();
        IRealSemesterService semesterService = new RealSemesterServiceImpl();
        JsonObject jsonObj = new JsonObject();

        int semesterId = Integer.parseInt(params.get("semesterId"));
        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
        String sSearch = params.get("sSearch");

        try {
            List<RealSemesterEntity> semesterList = Ultilities.SortSemesters(semesterService.getAllSemester());
            Map<Integer, String> semesterNameMap = new HashMap<>();
            final Map<Integer, Integer> semesterPositionMap = new HashMap<>();
            for (int i = 0; i < semesterList.size(); ++i) {
                RealSemesterEntity curSemester = semesterList.get(i);
                semesterNameMap.put(curSemester.getId(), curSemester.getSemester());
                semesterPositionMap.put(curSemester.getId(), i);
            }

            // Sorted by semester, then by subjectCode
            List<Object[]> searchList = marksService.getTotalStudentsGroupBySemesterAndSubject(semesterId);
            searchList.sort(Comparator.comparingInt(a -> {
                Object[] arr = (Object[]) a;
                return semesterPositionMap.get(arr[0]);
            }).thenComparing(new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    Object[] arr1 = (Object[]) o1;
                    Object[] arr2 = (Object[]) o2;
                    return arr1[1].toString().compareTo(arr2[1].toString());
                }
            }));
            List<Object[]> filterList = searchList.stream().filter(s -> Ultilities.containsIgnoreCase(s[1].toString(), sSearch))
                    .collect(Collectors.toList());

            List<List<String>> result = new ArrayList<>();
            List<Object[]> displayList = filterList.stream().skip(iDisplayStart)
                    .limit(iDisplayLength).collect(Collectors.toList());
            for (Object[] arr : displayList) {
                List<String> row = new ArrayList<>();

                int semesId = (int) arr[0];
                String subjectName = arr[1].toString();
                long numOfStudent = (long) arr[2];

                row.add(semesterNameMap.get(semesId));
                row.add(subjectName);
                row.add(numOfStudent + "");
                row.add(Math.ceil(numOfStudent / 25.0) + ""); // Average classes are created
                result.add(row);
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            jsonObj.addProperty("iTotalRecords", searchList.size());
            jsonObj.addProperty("iTotalDisplayRecords", filterList.size());
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return jsonObj;
    }

    @RequestMapping("/averageSubject/getList")
    @ResponseBody
    public JsonObject GetAverageSubjectForDataTable(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        IProgramService programService = new ProgramServiceImpl();
        IMarksService marksService = new MarksServiceImpl();

        int programId = Integer.parseInt(params.get("programId"));
        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));

        try {
            List<List<String>> dataList = marksService.getAverageSubjectLearnedByStudent(programId);
            List<List<String>> result = dataList.stream().skip(iDisplayStart)
                    .limit(iDisplayLength).collect(Collectors.toList());

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            jsonObj.addProperty("iTotalRecords", dataList.size());
            jsonObj.addProperty("iTotalDisplayRecords", dataList.size());
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return jsonObj;
    }

    public List<SubjectEntity> GetCurrentCurriculumSubjects(int curId) {
        List<SubjectEntity> result = new ArrayList<>();
        try {
            ICurriculumService curriculumService = new CurriculumServiceImpl();
            CurriculumEntity cur = curriculumService.getCurriculumById(curId);
            if (cur != null) {
                List<SubjectCurriculumEntity> list = cur.getSubjectCurriculumEntityList();
                for (SubjectCurriculumEntity en : list) {
                    if (!result.contains(en.getSubjectId())) {
                        result.add(en.getSubjectId());
                    }
                }
            }
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return result;
    }

    @RequestMapping("/getcurrent")
    @ResponseBody
    public JsonObject GetCurrent(@RequestParam int curId, @RequestParam int newId, @RequestParam int stuId) {
        JsonObject result = new JsonObject();

        try {
            List<SubjectEntity> subs = this.GetCurrentCurriculumSubjects(curId);
            List<SubjectEntity> newSubs = this.GetCurrentCurriculumSubjects(newId);

            List<SubjectEntity> common = new ArrayList<>();

            for (SubjectEntity s : subs) {
                if (newSubs.stream().anyMatch(c -> c.getId().equals(s.getId()))) {
                    if (!common.contains(s)) common.add(s);
                }
            }

            for (SubjectEntity s : newSubs) {
                if (subs.stream().anyMatch(c -> c.getId().equals(s.getId()))) {
                    if (!common.contains(s)) common.add(s);
                }
            }

            List<ChangeCurriculumModel> model = GetModel(stuId, common);


            JsonArray aaData = (JsonArray) new Gson().toJsonTree(model);

            result.add("data", aaData);
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return result;
    }

    @RequestMapping("/getnew")
    @ResponseBody
    public JsonObject GetNew(@RequestParam int curId, @RequestParam int newId, @RequestParam int stuId) {
        JsonObject result = new JsonObject();

        try {
            List<SubjectEntity> subs = this.GetCurrentCurriculumSubjects(curId);
            List<SubjectEntity> newSubs = this.GetCurrentCurriculumSubjects(newId);

            List<SubjectEntity> notcommon = new ArrayList<>();
            for (SubjectEntity s : subs) {
                if (!newSubs.stream().anyMatch(c -> c.getId().equals(s.getId()))) {
                    if (!notcommon.contains(s)) notcommon.add(s);
                }
            }

            for (SubjectEntity s : newSubs) {
                if (!subs.stream().anyMatch(c -> c.getId().equals(s.getId()))) {
                    if (!notcommon.contains(s)) notcommon.add(s);
                }
            }

            List<ChangeCurriculumModel> model = GetModel(stuId, notcommon);

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(model);

            result.add("data", aaData);
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return result;
    }

    @RequestMapping("/getother")
    @ResponseBody
    public JsonObject GetOther(@RequestParam int stuId, @RequestParam int curId, @RequestParam int newId) {
        JsonObject result = new JsonObject();

        try {
            IStudentService studentService = new StudentServiceImpl();

            StudentEntity stu = studentService.findStudentById(stuId);
            List<MarksEntity> list = stu.getMarksEntityList();
            List<SubjectEntity> subs = this.GetCurrentCurriculumSubjects(curId);
            List<SubjectEntity> newSubs = this.GetCurrentCurriculumSubjects(newId);

            // chung va khong chung
            List<SubjectEntity> common = new ArrayList<>();
            for (SubjectEntity s : subs) {
                if (newSubs.stream().anyMatch(c -> c.getId().equals(s.getId()))) {
                    if (!common.contains(s)) common.add(s);
                }
            }
            for (SubjectEntity s : newSubs) {
                if (subs.stream().anyMatch(c -> c.getId().equals(s.getId()))) {
                    if (!common.contains(s)) common.add(s);
                }
            }
            for (SubjectEntity s : subs) {
                if (!newSubs.stream().anyMatch(c -> c.getId().equals(s.getId()))) {
                    if (!common.contains(s)) common.add(s);
                }
            }
            for (SubjectEntity s : newSubs) {
                if (!subs.stream().anyMatch(c -> c.getId().equals(s.getId()))) {
                    if (!common.contains(s)) common.add(s);
                }
            }

            // còn lại
            List<SubjectEntity> others = new ArrayList<>();
            for (MarksEntity mark : list) {
                if (!common.stream().anyMatch(c -> c.getId().equals(mark.getSubjectMarkComponentId().getSubjectId().getId()))) {
                    if (!others.contains(mark.getSubjectMarkComponentId().getSubjectId()))
                        others.add(mark.getSubjectMarkComponentId().getSubjectId());
                }
            }

            List<ChangeCurriculumModel> model = GetModel(stuId, others);

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(model);

            result.add("data", aaData);
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return result;
    }

    private List<ChangeCurriculumModel> GetModel(int stuId, List<SubjectEntity> list) {
        IStudentService studentService = new StudentServiceImpl();
        StudentEntity student = studentService.findStudentById(stuId);
        List<MarksEntity> marks = student.getMarksEntityList();
//        List<MarksEntity> filterMarks = marks.stream().filter(a -> a.getStatus().toLowerCase().contains("pass") || a.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
        List<MarksEntity> filterMarks = marks;

        List<ChangeCurriculumModel> model = list
                .stream()
                .map(c -> new ChangeCurriculumModel(c.getId(), filterMarks
                        .stream()
                        .filter(a -> a.getSubjectMarkComponentId().getSubjectId().getId().equals(c.getId()))
                        .map(a -> new MarkModel(a.getId(), a.getSemesterId().getSemester(), a.getIsActivated(), a.getAverageMark()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());

        // thsy thế
        for (SubjectEntity sub : list) {
            List<SubjectEntity> replaces = sub.getSubjectEntityList();
            if (!replaces.isEmpty()) {
                List<ChangeCurriculumModel> l = replaces
                        .stream()
                        .map(c -> new ChangeCurriculumModel(c.getId() + " (thay thế)", filterMarks
                                .stream()
                                .filter(a -> a.getSubjectMarkComponentId().getSubjectId().getId().equals(c.getId()))
                                .map(a -> new MarkModel(a.getId(), a.getSemesterId().getSemester(), a.getIsActivated(), a.getAverageMark()))
                                .collect(Collectors.toList())))
                        .collect(Collectors.toList());
                model.addAll(l);
            }
            List<SubjectEntity> replaces2 = sub.getSubjectEntityList1();
            if (!replaces2.isEmpty()) {
                for (SubjectEntity sub2 : replaces2) {
                    List<SubjectEntity> replaces3 = sub2.getSubjectEntityList();
                    if (!replaces3.isEmpty()) {
                        List<ChangeCurriculumModel> n = replaces3
                                .stream()
                                .map(c -> new ChangeCurriculumModel(c.getId() + " (thay thế)", filterMarks
                                        .stream()
                                        .filter(a -> a.getSubjectMarkComponentId().getSubjectId().getId().equals(c.getId()))
                                        .map(a -> new MarkModel(a.getId(), a.getSemesterId().getSemester(), a.getIsActivated(), a.getAverageMark()))
                                        .collect(Collectors.toList())))
                                .collect(Collectors.toList());
                        model.addAll(n);
                    }
                }
            }
        }

        model = model.stream()
                .filter(c -> c.getData().size() > 0)
                .filter(Ultilities.distinctByKey(c -> c.getSubjectCode()))
                .collect(Collectors.toList());

        return model;
    }

    @RequestMapping("/change")
    @ResponseBody
    public JsonObject Change(@RequestParam int stuId,
                             @RequestParam int curId,
                             @RequestParam int newId,
                             @RequestParam String data) {
        JsonObject result = new JsonObject();

        try {
            Gson gson = new Gson();
            List<String> curList = gson.fromJson(data, new TypeToken<List<String>>() {
            }.getType());

            IStudentService studentService = new StudentServiceImpl();
            ICurriculumService curriculumService = new CurriculumServiceImpl();
            IDocumentService documentService = new DocumentServiceImpl();
            IDocTypeService docTypeService = new DocTypeServiceImpl();

            DocumentEntity document;
            DocTypeEntity docType = docTypeService.findDocType("Chuyển ngành");
            if (docType != null) {
                document = documentService.getDocumentByDocTypeId(docType.getId());
                if (document == null) {
                    document = new DocumentEntity();
                    document.setDocTypeId(docType);
                    document.setCode("111111");

                    documentService.createDocument(document);
                }
            } else {
                docType = new DocTypeEntity();
                docType.setName("Chuyển ngành");
                docTypeService.createDocType(docType);

                document = new DocumentEntity();
                document.setDocTypeId(docType);
                document.setCode("111111");

                documentService.createDocument(document);
            }

            StudentEntity stu = studentService.findStudentById(stuId);
            Ultilities.logUserAction("Chuyển ngành cho sinh viên");
            CurriculumEntity newCur = curriculumService.getCurriculumById(newId);

            DocumentStudentEntity doc = new DocumentStudentEntity();
            doc.setStudentId(stu);
            doc.setCurriculumId(newCur);
            doc.setDocumentId(document);
            doc.setDocumentId(documentService.getDocumentById(2));
            doc.setCreatedDate(Calendar.getInstance().getTime());
            stu.getDocumentStudentEntityList().add(doc);

            List<MarksEntity> marks = stu.getMarksEntityList();
            for (MarksEntity mark : marks) {
                if (curList.stream().anyMatch(c -> Integer.parseInt(c) == mark.getId())) {
                    mark.setIsActivated(false);
                    System.out.println(mark.getId() + " has been disabled!");
                } else {
                    mark.setIsActivated(true);
                }
            }

            studentService.saveStudent(stu);

            result.addProperty("success", true);
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
            result.addProperty("success", false);
            result.addProperty("msg", e.getMessage());
        }

        return result;
    }

}
