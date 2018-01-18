package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class EmployeeList {

    @RequestMapping("/employeeList")
    public ModelAndView EmployeeListAll() {
        ModelAndView view = new ModelAndView("EmployeeList");
        view.addObject("title", "Danh sách giảng viên");

        return view;
    }

//    @RequestMapping("/studentList/{studentId}")
//    public ModelAndView StudentInfo(@PathVariable("studentId") int studentId) {
//        ModelAndView view = new ModelAndView("StudentInfo");
//        view.addObject("title", "Thông tin sinh viên");
//        view = this.GetStudentInfoData(view, studentId);
//
//        return view;
//    }
//
//    @RequestMapping("/studentProcess/{studentId}")
//    public ModelAndView StudentInfo2(@PathVariable("studentId") int studentId) {
//        ModelAndView view = new ModelAndView("StudentInfo2");
//        view.addObject("title", "Điểm quá trình");
//        view = this.GetStudentInfoData(view, studentId);
//
//        return view;
//    }

//    private ModelAndView GetStudentInfoData(ModelAndView view, int studentId) {
//        IStudentService studentService = new StudentServiceImpl();
//        IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();
//        IStudentStatusService studentStatusService = new StudentStatusServiceImpl();
//
//        StudentEntity student = studentService.findStudentById(studentId);
//
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        view.addObject("student", student);
//        view.addObject("docStudent", Lists.reverse(student.getDocumentStudentEntityList()).get(0));
//
//        view.addObject("gender", student.getGender() == Enums.Gender.MALE.getValue()
//                ? Enums.Gender.MALE.getName() : Enums.Gender.FEMALE.getName());
//        view.addObject("dateOfBirth", sdf.format(student.getDateOfBirth()));
//        view.addObject("program", student.getProgramId() != null ? student.getProgramId().getName() : "N/A");
//        CurriculumEntity cur = Lists.reverse(student.getDocumentStudentEntityList()).get(0).getCurriculumId();
//        view.addObject("curriculum", cur != null ? cur.getName() : "N/A");
//
//        // Giaa lap hoc ky
//        RealSemesterEntity gialap = Global.getTemporarySemester();
//
//        StudentStatusEntity studentStatusEntity = studentStatusService.getStudentStatusBySemesterIdAndStudentId(gialap.getId(), studentId);
//        String studentStatus = studentStatusEntity != null ? studentStatusEntity.getStatus() : "N/A";
//        view.addObject("status", studentStatus);
//
//        return view;
//    }

//    @RequestMapping(value = "/student/edit")
//    @ResponseBody
//    public JsonObject EditSubject(@RequestParam("sRollNumber") String rollNumber, @RequestParam("sFullName") String fullName,
//                                  @RequestParam("sGender") String gender, @RequestParam("sDOB") String dob
//            , @RequestParam("sTermNumber") String term) {
//        JsonObject jsonObj = new JsonObject();
//        IStudentService studentService = new StudentServiceImpl();
//        try {
//
//            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
//            EntityManager em = emf.createEntityManager();
//            em.getTransaction().begin();
//            StudentEntity student = studentService.findStudentByRollNumber(rollNumber);
//
//            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//            Date date = dateFormat.parse(dob);
//            long time = date.getTime();
//            Timestamp dateOfBirth = new Timestamp(time);
//            student.setDateOfBirth(dateOfBirth);
//            student.setDateOfBirth(date);
//            student.setFullName(fullName);
//            student.setTerm(Integer.valueOf(term));
//            if (gender.equals("Nam")){
//                student.setGender(true);
//            }else{
//                student.setGender(false);
//            }
//            em.merge(student);
//            em.flush();
//            em.getTransaction().commit();
//
//                jsonObj.addProperty("success", true);
//                jsonObj.addProperty("message", "update Fail");
//
//        } catch (Exception e) {
//            Logger.writeLog(e);
//            jsonObj.addProperty("false", false);
//            jsonObj.addProperty("message", e.getMessage());
//        }
//
//        return jsonObj;
//    }

//    @RequestMapping(value = "/studentList/marks")
//    @ResponseBody
//    public JsonObject GetStudentMarkList(@RequestParam Map<String, String> params) {
//        List<StudentDetailModel> result = new ArrayList<>();
//        IRealSemesterService semesterService = new RealSemesterServiceImpl();
//        JsonObject jsonObj = new JsonObject();
//
//        int studentId = Integer.parseInt(params.get("studentId"));
//        boolean getLatestMarks = Boolean.parseBoolean(params.get("getLatestMarks") != null ? params.get("getLatestMarks") : "false");
//
//        try {
//            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
//            EntityManager em = emf.createEntityManager();
//
//            List<RealSemesterEntity> sortedSemesters = Ultilities.SortSemesters(semesterService.getAllSemester());
//
//            // Get all marks exist in student subject curriculum
//            String queryStr = "SELECT m.id, sub.id, sub.name, m.semesterId.semester, sc.subjectCredits, m.averageMark, m.status, sc.termNumber" +
//                    " FROM MarksEntity m" +
//                    " INNER JOIN SubjectMarkComponentEntity smc ON m.subjectMarkComponentId.id = smc.id" +
//                    " INNER JOIN SubjectEntity sub ON smc.subjectId.id = sub.id" +
//                    " INNER JOIN MarkComponentEntity mc ON smc.markComponentId.id = mc.id" +
//                    " INNER JOIN DocumentStudentEntity ds ON ds.studentId.id = m.studentId.id" +
//                    " INNER JOIN SubjectCurriculumEntity sc ON ds.curriculumId.id = sc.curriculumId.id" +
//                    " AND ds.curriculumId.programId.id = ds.studentId.programId.id" +
//                    " AND smc.subjectId.id = sc.subjectId.id" +
//                    " AND mc.name LIKE :markComponentName" +
//                    " AND m.studentId.id = :studentId" +
//                    " AND m.isActivated = true";Query query = em.createQuery(queryStr);
//            query.setParameter("markComponentName", "%average%");
//            query.setParameter("studentId", studentId);
//
//            List<Object[]> specializedMarkList = query.getResultList();
//
//            if (!specializedMarkList.isEmpty()) {
//                List<Integer> markIdList = new ArrayList<>();
//                for (Object[] row : specializedMarkList) {
//                    int curTerm = (int) row[7];
//                    List<MarkModel> curMarkList = null;
//                    for (StudentDetailModel studentDetail : result) {
//                        if (studentDetail.term == curTerm) {
//                            curMarkList = studentDetail.markList;
//                        }
//                    }
//
//                    if (curMarkList == null) {
//                        curMarkList = new ArrayList<>();
//
//                        StudentDetailModel model = new StudentDetailModel();
//                        model.term = curTerm;
//                        model.markList = curMarkList;
//                        result.add(model);
//                    }
//
//                    MarkModel markModel = new MarkModel();
//                    markModel.setMarkId((Integer) row[0]);
//                    markModel.setSubject((String) row[1]);
//                    markModel.setSubjectName((String) row[2]);
//                    markModel.setSemester((String) row[3]);
//                    markModel.setCredits((Integer) row[4]);
//                    markModel.setAverageMark((Double) row[5]);
//                    markModel.setStatus((String) row[6]);
//
//                    curMarkList.add(markModel);
//
//                    markIdList.add((Integer) row[0]);
//                }
//
//                Collections.sort(result, new Comparator<StudentDetailModel>() {
//                    @Override
//                    public int compare(StudentDetailModel o1, StudentDetailModel o2) {
//                        return Integer.compare(o1.term, o2.term);
//                    }
//                });
//
//                // Get other marks that not exist in student subject curriculum
//                queryStr = "SELECT m.id, sub.id, sub.name, m.semesterId.semester, sc.subjectCredits, m.averageMark, m.status" +
//                        " FROM MarksEntity m" +
//                        " INNER JOIN SubjectMarkComponentEntity smc ON m.subjectMarkComponentId.id = smc.id" +
//                        " INNER JOIN SubjectEntity sub ON smc.subjectId.id = sub.id" +
//                        " INNER JOIN MarkComponentEntity mc ON smc.markComponentId.id = mc.id" +
//                        " INNER JOIN DocumentStudentEntity ds ON ds.studentId.id = m.studentId.id" +
//                        " INNER JOIN SubjectCurriculumEntity sc ON ds.curriculumId.id = sc.curriculumId.id" +
//                        " AND sc.subjectId.id = sub.id" +
//                        " AND mc.name LIKE :markComponentName" +
//                        " AND m.studentId.id = :studentId" +
//                        " AND m.id NOT IN :sList" +
//                        " AND m.isActivated = true";
//                query = em.createQuery(queryStr);
//                query.setParameter("markComponentName", "%average%");
//                query.setParameter("studentId", studentId);
//                query.setParameter("sList", markIdList);
//
//                List<Object[]> otherMarkList = query.getResultList();
//                if (!otherMarkList.isEmpty()) {
//                    List<MarkModel> markList = new ArrayList<>();
//                    for (Object[] row : otherMarkList) {
//                        MarkModel markModel = new MarkModel();
//                        markModel.setMarkId((Integer) row[0]);
//                        markModel.setSubject((String) row[1]);
//                        markModel.setSubjectName((String) row[2]);
//                        markModel.setSemester((String) row[3]);
//                        markModel.setCredits((Integer) row[4]);
//                        markModel.setAverageMark((Double) row[5]);
//                        markModel.setStatus((String) row[6]);
//
//                        markList.add(markModel);
//                    }
//
//                    StudentDetailModel studentDetailModel = new StudentDetailModel();
//                    studentDetailModel.term = -1;
//                    studentDetailModel.markList = markList;
//                    result.add(studentDetailModel);
//                }
//            }
//
//            ArrayList<String> seasons = new ArrayList<String>() {{
//                add("spring");
//                add("summer");
//                add("fall");
//                add("n/a");
//            }};
//
//            // Sort data
//            for (StudentDetailModel studentDetailModel : result) {
//                studentDetailModel.markList.sort(Comparator
//                        .comparing(MarkModel::getSubject)
//                        .thenComparingInt(c -> {
//                            if (c.getSemester().equalsIgnoreCase("n/a")) return 0;
//                            String removewhite = c.getSemester().replaceAll("\\s+", "");
//                            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
//                            Pattern pattern = Pattern.compile("^\\D*(\\d)");
//                            Matcher matcher = pattern.matcher(removeline);
//                            matcher.find();
//                            return Integer.parseInt(removeline.substring(matcher.start(1), removeline.length()));
//                        }).thenComparingInt(a -> {
//                            if (a.getSemester().equalsIgnoreCase("n/a")) return seasons.indexOf("n/a");
//                            String removewhite = a.getSemester().replaceAll("\\s+", "");
//                            String removeline = removewhite.substring(0, removewhite.indexOf("_") < 0 ? removewhite.length() : removewhite.indexOf("_"));
//                            Pattern pattern = Pattern.compile("^\\D*(\\d)");
//                            Matcher matcher = pattern.matcher(removeline);
//                            matcher.find();
//                            String season = removeline.substring(0, matcher.start(1)).toLowerCase();
//                            return seasons.indexOf(season);
//                        }));
//
//                List<String> subs = studentDetailModel.markList
//                        .stream()
//                        .map(c -> c.getSubject())
//                        .distinct()
//                        .collect(Collectors.toList());
//                for (String sub : subs) {
//                    List<MarkModel> l = studentDetailModel.markList
//                            .stream()
//                            .filter(c -> c.getSubject().equals(sub))
//                            .collect(Collectors.toList());
//                    if (l.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
//                        studentDetailModel.markList.removeIf(c -> c.getSubject().equals(sub) && !c.getStatus().toLowerCase().contains("pass") && !c.getStatus().toLowerCase().contains("exempt"));
//                    } else {
//                        studentDetailModel.markList.removeIf(c -> l.indexOf(c) != -1 && l.indexOf(c) < l.size() - 1);
//                    }
//                }
//            }
//
//            IMarksService marksService = new MarksServiceImpl();
//            ISubjectService service = new SubjectServiceImpl();
//            for (StudentDetailModel studentDetailModel : result) {
//                for (MarkModel model : studentDetailModel.markList) {
//                    if (!model.getStatus().toLowerCase().contains("pass") && !model.getStatus().toLowerCase().contains("exempt")) {
//                        SubjectEntity s = service.findSubjectById(model.getSubject());
//                        boolean pass = false;
//
//                        for (SubjectEntity replacer : s.getSubjectEntityList()) {
//                            List<MarksEntity> list = marksService.getMarkByConditions(-1, Arrays.asList(replacer.getId()), studentId);
//                            for (MarksEntity m : list) {
//                                if (m.getStatus().toLowerCase().contains("pass") || m.getStatus().toLowerCase().contains("exempt")) {
//                                    model.setAverageMark(m.getAverageMark());
//                                    model.setSemester(m.getSemesterId().getSemester());
//                                    model.setStatus(m.getStatus());
//                                    model.setSubject(m.getSubjectMarkComponentId().getSubjectId().getId());
//                                    model.setMarkId(m.getId());
//                                    model.setSubjectName(m.getSubjectMarkComponentId().getSubjectId().getName() + " <font color='red'>(thay thế)</font>");
//                                    pass = true;
//                                    break;
//                                }
//                            }
//
//                            if (pass) {
//                                break;
//                            }
//                        }
//
//                        if (!pass) {
//                            for (SubjectEntity replacer : s.getSubjectEntityList1()) {
//                                List<MarksEntity> list = marksService.getMarkByConditions(-1, Arrays.asList(replacer.getId()), studentId);
//                                for (MarksEntity m : list) {
//                                    if (m.getStatus().toLowerCase().contains("pass") || m.getStatus().toLowerCase().contains("exempt")) {
//                                        model.setAverageMark(m.getAverageMark());
//                                        model.setSemester(m.getSemesterId().getSemester());
//                                        model.setStatus(m.getStatus());
//                                        model.setSubject(m.getSubjectMarkComponentId().getSubjectId().getId());
//                                        model.setMarkId(m.getId());
//                                        model.setSubjectName(m.getSubjectMarkComponentId().getSubjectId().getName() + " <font color='red'>(thay thế)</font>");
//                                        pass = true;
//                                        break;
//                                    }
//                                }
//
//                                if (pass) {
//                                    break;
//                                } else {
//                                    for (SubjectEntity replacer2 : replacer.getSubjectEntityList()) {
//                                        List<MarksEntity> list2 = marksService.getMarkByConditions(-1, Arrays.asList(replacer2.getId()), studentId);
//                                        for (MarksEntity m : list2) {
//                                            if (m.getStatus().toLowerCase().contains("pass") || m.getStatus().toLowerCase().contains("exempt")) {
//                                                model.setAverageMark(m.getAverageMark());
//                                                model.setSemester(m.getSemesterId().getSemester());
//                                                model.setStatus(m.getStatus());
//                                                model.setSubject(m.getSubjectMarkComponentId().getSubjectId().getId());
//                                                model.setMarkId(m.getId());
//                                                model.setSubjectName(m.getSubjectMarkComponentId().getSubjectId().getName() + " <font color='red'>(thay thế)</font>");
//                                                pass = true;
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            JsonArray detailList = (JsonArray) new Gson().toJsonTree(result);
//            jsonObj.add("detailList", detailList);
//            jsonObj.addProperty("success", true);
//            em.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Logger.writeLog(e);
//            jsonObj.addProperty("success", false);
//        }
//
//        return jsonObj;
//    }

    @RequestMapping(value = "/loadEmployeeList")
    @ResponseBody
    public JsonObject LoadEmployeeListAll(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            String sSearch = params.get("sSearch");
            int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
            int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
            int iTotalRecords = 0;
            int iTotalDisplayRecords = 0;

            String queryStr;
            // Đếm số lượng gv
            queryStr = "SELECT COUNT(s) FROM EmployeeEntity s";
            TypedQuery<Integer> queryCounting = em.createQuery(queryStr, Integer.class);
            iTotalRecords = ((Number) queryCounting.getSingleResult()).intValue();

            // Đếm số lượng gv sau khi filter
            if (!sSearch.isEmpty()) {
                queryStr = "SELECT COUNT(s) FROM EmployeeEntity s" +
                        " WHERE s.code LIKE :code OR s.fullName LIKE :fullName";
                queryCounting = em.createQuery(queryStr, Integer.class);
                queryCounting.setParameter("code", "%" + sSearch + "%");
                queryCounting.setParameter("fullName", "%" + sSearch + "%");
                iTotalDisplayRecords = ((Number) queryCounting.getSingleResult()).intValue();
            } else {
                iTotalDisplayRecords = iTotalRecords;
            }

            // Query danh sách gv
            queryStr = "SELECT s FROM EmployeeEntity s" +
                    (!sSearch.isEmpty() ? " WHERE s.code LIKE :code OR s.fullName LIKE :fullName" : "");
            TypedQuery<EmployeeEntity> query = em.createQuery(queryStr, EmployeeEntity.class);
            query.setFirstResult(iDisplayStart);
            query.setMaxResults(iDisplayLength);
            if (!sSearch.isEmpty()) {
                query.setParameter("code", "%" + sSearch + "%");
                query.setParameter("fullName", "%" + sSearch + "%");
            }
            List<EmployeeEntity> employeeList = query.getResultList();

            List<List<String>> result = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (EmployeeEntity emp : employeeList) {
                List<String> dataList = new ArrayList<String>();

                dataList.add(emp.getCode());
                dataList.add(emp.getFullName());
                dataList.add(emp.getDateOfBirth());
                dataList.add(emp.getPosition());
                dataList.add(emp.getPhone());
                dataList.add(emp.getEmailFE());
                dataList.add(emp.getId() + "");

                result.add(dataList);
            }

            JsonArray aaData = (JsonArray) new Gson()
                    .toJsonTree(result, new TypeToken<List<List<String>>>() {
                    }.getType());

            jsonObj.addProperty("iTotalRecords", iTotalRecords);
            jsonObj.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

//    @RequestMapping(value = "/studentList/getAllMarks", method = RequestMethod.POST)
//    @ResponseBody
//    public JsonObject GetAllStudentMarks(int studentId) {
//        JsonObject jsonObj = new JsonObject();
//
//        try {
//            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
//            EntityManager em = emf.createEntityManager();
//
//            // Lấy thông tin sv
//            String queryStr = "SELECT s FROM StudentEntity s WHERE s.id = :sId";
//            TypedQuery<StudentEntity> queryStudent = em.createQuery(queryStr, StudentEntity.class);
//            queryStudent.setParameter("sId", studentId);
//            StudentEntity student = queryStudent.getSingleResult();
//
//            StudentMarkModel model = new StudentMarkModel();
//            model.setStudentId(studentId);
//            model.setStudentName(student.getFullName());
//            model.setRollNumber(student.getRollNumber());
//
//            // Lấy danh sách điểm
//            queryStr = "SELECT m FROM MarksEntity m WHERE m.isActivated = true and m.studentId.id = :sId";
//            TypedQuery<MarksEntity> query = em.createQuery(queryStr, MarksEntity.class);
//            query.setParameter("sId", studentId);
//
//            List<MarksEntity> markList = query.getResultList();
//            markList = Ultilities.FilterStudentsOnlyPassAndFail(markList);
//
//            List<MarkModel> markListModel = new ArrayList<>();
//            for (MarksEntity m : markList) {
//                MarkModel data = new MarkModel();
//                data.setSemester(m.getSemesterId().getSemester());
//                data.setSubject(m.getSubjectMarkComponentId() != null ? m.getSubjectMarkComponentId().getSubjectId().getId() : "N/A");
//                data.setStatus(m.getStatus());
//                data.setAverageMark(m.getAverageMark());
//
//                markListModel.add(data);
//            }
//            model.setMarkList(markListModel);
//
//            String result = new Gson().toJson(model);
//
//            jsonObj.addProperty("success", true);
//            jsonObj.addProperty("studentMarkDetail", result);
//            em.close();
//        } catch (Exception e) {
//            jsonObj.addProperty("success", false);
//            jsonObj.addProperty("error", e.getMessage());
//        }
//
//        return jsonObj;
//    }

//    private class StudentDetailModel {
//        public int term;
//        public List<MarkModel> markList;
//    }
}


