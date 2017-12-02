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
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StudentList {

    @RequestMapping("/studentList")
    public ModelAndView StudentListAll() {
        ModelAndView view = new ModelAndView("StudentList");
        view.addObject("title", "Danh sách sinh viên");

        return view;
    }

    @RequestMapping("/studentList/{studentId}")
    public ModelAndView StudentInfo(@PathVariable("studentId") int studentId) {
        ModelAndView view = new ModelAndView("StudentInfo");
        view.addObject("title", "Thông tin sinh viên");
        view = this.GetStudentInfoData(view, studentId);

        return view;
    }

    @RequestMapping("/studentProcess/{studentId}")
    public ModelAndView StudentInfo2(@PathVariable("studentId") int studentId) {
        ModelAndView view = new ModelAndView("StudentInfo2");
        view.addObject("title", "Điểm quá trình");
        view = this.GetStudentInfoData(view, studentId);

        return view;
    }

    private ModelAndView GetStudentInfoData(ModelAndView view, int studentId) {
        IStudentService studentService = new StudentServiceImpl();
        IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();

        StudentEntity student = studentService.findStudentById(studentId);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        view.addObject("student", student);
        view.addObject("docStudent", Lists.reverse(student.getDocumentStudentEntityList()).get(0));

        view.addObject("gender", student.getGender() == Enums.Gender.MALE.getValue()
                ? Enums.Gender.MALE.getName() : Enums.Gender.FEMALE.getName());
        view.addObject("dateOfBirth", sdf.format(student.getDateOfBirth()));
        view.addObject("program", student.getProgramId() != null ? student.getProgramId().getName() : "N/A");
        CurriculumEntity cur = Lists.reverse(student.getDocumentStudentEntityList()).get(0).getCurriculumId();
        view.addObject("curriculum", cur != null ? cur.getName() : "N/A");

        return view;
    }

    @RequestMapping(value = "/student/edit")
    @ResponseBody
    public JsonObject EditSubject(@RequestParam("sRollNumber") String rollNumber, @RequestParam("sFullName") String fullName,
                                  @RequestParam("sGender") String gender, @RequestParam("sDOB") String dob) {
        JsonObject jsonObj = new JsonObject();
        IStudentService studentService = new StudentServiceImpl();
        try {

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            StudentEntity student = studentService.findStudentByRollNumber(rollNumber);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dateFormat.parse(dob);
            long time = date.getTime();
            Timestamp dateOfBirth = new Timestamp(time);
            student.setDateOfBirth(dateOfBirth);
            student.setDateOfBirth(date);
            student.setFullName(fullName);
            if (gender.equals("Nam")){
                student.setGender(true);
            }else{
                student.setGender(false);
            }
            em.merge(student);
            em.flush();
            em.getTransaction().commit();

                jsonObj.addProperty("success", true);
                jsonObj.addProperty("message", "update Fail");

        } catch (Exception e) {
            Logger.writeLog(e);
            jsonObj.addProperty("false", false);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

    @RequestMapping(value = "/studentList/marks")
    @ResponseBody
    public JsonObject GetStudentMarkList(@RequestParam Map<String, String> params) {
        List<StudentDetailModel> result = new ArrayList<>();
        IRealSemesterService semesterService = new RealSemesterServiceImpl();
        JsonObject jsonObj = new JsonObject();

        int studentId = Integer.parseInt(params.get("studentId"));
        boolean getLatestMarks = Boolean.parseBoolean(params.get("getLatestMarks") != null ? params.get("getLatestMarks") : "false");

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            List<RealSemesterEntity> sortedSemesters = Ultilities.SortSemesters(semesterService.getAllSemester());

            // Get all marks exist in student subject curriculum
            String queryStr = "SELECT m.id, sub.id, sub.name, m.semesterId.semester, sc.subjectCredits, m.averageMark, m.status, sc.termNumber" +
                    " FROM MarksEntity m" +
                    " INNER JOIN SubjectMarkComponentEntity smc ON m.subjectMarkComponentId.id = smc.id" +
                    " INNER JOIN SubjectEntity sub ON smc.subjectId.id = sub.id" +
                    " INNER JOIN MarkComponentEntity mc ON smc.markComponentId.id = mc.id" +
                    " INNER JOIN DocumentStudentEntity ds ON ds.studentId.id = m.studentId.id" +
                    " INNER JOIN SubjectCurriculumEntity sc ON ds.curriculumId.id = sc.curriculumId.id" +
                    " AND ds.curriculumId.programId.id = ds.studentId.programId.id" +
                    " AND smc.subjectId.id = sc.subjectId.id" +
                    " AND mc.name LIKE :markComponentName" +
                    " AND m.studentId.id = :studentId" +
                    " AND m.isActivated = true" +
                    (getLatestMarks ? " AND m.isEnabled = true" : "");
            Query query = em.createQuery(queryStr);
            query.setParameter("markComponentName", "%average%");
            query.setParameter("studentId", studentId);

            List<Object[]> specializedMarkList = query.getResultList();

            if (!specializedMarkList.isEmpty()) {
                List<Integer> markIdList = new ArrayList<>();
                for (Object[] row : specializedMarkList) {
                    int curTerm = (int) row[7];
                    List<MarkModel> curMarkList = null;
                    for (StudentDetailModel studentDetail : result) {
                        if (studentDetail.term == curTerm) {
                            curMarkList = studentDetail.markList;
                        }
                    }

                    if (curMarkList == null) {
                        curMarkList = new ArrayList<>();

                        StudentDetailModel model = new StudentDetailModel();
                        model.term = curTerm;
                        model.markList = curMarkList;
                        result.add(model);
                    }

                    MarkModel markModel = new MarkModel();
                    markModel.setMarkId((Integer) row[0]);
                    markModel.setSubject((String) row[1]);
                    markModel.setSubjectName((String) row[2]);
                    markModel.setSemester((String) row[3]);
                    markModel.setCredits((Integer) row[4]);
                    markModel.setAverageMark((Double) row[5]);
                    markModel.setStatus((String) row[6]);

                    curMarkList.add(markModel);

                    markIdList.add((Integer) row[0]);
                }

                Collections.sort(result, new Comparator<StudentDetailModel>() {
                    @Override
                    public int compare(StudentDetailModel o1, StudentDetailModel o2) {
                        return Integer.compare(o1.term, o2.term);
                    }
                });

                // Get other marks that not exist in student subject curriculum
                queryStr = "SELECT m.id, sub.id, sub.name, m.semesterId.semester, sc.subjectCredits, m.averageMark, m.status" +
                        " FROM MarksEntity m" +
                        " INNER JOIN SubjectMarkComponentEntity smc ON m.subjectMarkComponentId.id = smc.id" +
                        " INNER JOIN SubjectEntity sub ON smc.subjectId.id = sub.id" +
                        " INNER JOIN MarkComponentEntity mc ON smc.markComponentId.id = mc.id" +
                        " INNER JOIN DocumentStudentEntity ds ON ds.studentId.id = m.studentId.id" +
                        " INNER JOIN SubjectCurriculumEntity sc ON ds.curriculumId.id = sc.curriculumId.id" +
                        " AND sc.subjectId.id = sub.id" +
                        " AND mc.name LIKE :markComponentName" +
                        " AND m.studentId.id = :studentId" +
                        " AND m.id NOT IN :sList" +
                        " AND m.isActivated = true" +
                        (getLatestMarks ? " AND m.isEnabled = true" : "");
                query = em.createQuery(queryStr);
                query.setParameter("markComponentName", "%average%");
                query.setParameter("studentId", studentId);
                query.setParameter("sList", markIdList);

                List<Object[]> otherMarkList = query.getResultList();
                if (!otherMarkList.isEmpty()) {
                    List<MarkModel> markList = new ArrayList<>();
                    for (Object[] row : otherMarkList) {
                        MarkModel markModel = new MarkModel();
                        markModel.setMarkId((Integer) row[0]);
                        markModel.setSubject((String) row[1]);
                        markModel.setSubjectName((String) row[2]);
                        markModel.setSemester((String) row[3]);
                        markModel.setCredits((Integer) row[4]);
                        markModel.setAverageMark((Double) row[5]);
                        markModel.setStatus((String) row[6]);

                        markList.add(markModel);
                    }

                    StudentDetailModel studentDetailModel = new StudentDetailModel();
                    studentDetailModel.term = -1;
                    studentDetailModel.markList = markList;
                    result.add(studentDetailModel);
                }
            }

            // Sort data
            for (StudentDetailModel studentDetailModel : result) {
                studentDetailModel.markList.sort(new Comparator<MarkModel>() {
                    @Override
                    public int compare(MarkModel o1, MarkModel o2) {
                        return o1.getSubjectName().compareTo(o2.getSubjectName());
                    }
                }.thenComparingInt(m -> {
                    return sortedSemesters.indexOf(m.getSemester());
                }));
                studentDetailModel.markList = studentDetailModel.markList
                        .stream()
                        .filter(Ultilities.distinctByKey(c -> c.getSubject() + "_" + c.getSemester())).collect(Collectors.toList());
            }

            JsonArray detailList = (JsonArray) new Gson().toJsonTree(result);
            jsonObj.add("detailList", detailList);
            jsonObj.addProperty("success", true);
            em.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
            jsonObj.addProperty("success", false);
        }

        return jsonObj;
    }

    @RequestMapping(value = "/loadStudentList")
    @ResponseBody
    public JsonObject LoadStudentListAll(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            String sSearch = params.get("sSearch");
            int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
            int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
            int iTotalRecords = 0;
            int iTotalDisplayRecords = 0;

            String queryStr;
            // Đếm số lượng sv
            queryStr = "SELECT COUNT(s) FROM StudentEntity s";
            TypedQuery<Integer> queryCounting = em.createQuery(queryStr, Integer.class);
            iTotalRecords = ((Number) queryCounting.getSingleResult()).intValue();

            // Đếm số lượng sv sau khi filter
            if (!sSearch.isEmpty()) {
                queryStr = "SELECT COUNT(s) FROM StudentEntity s" +
                        " WHERE s.rollNumber LIKE :rollNumber OR s.fullName LIKE :fullName";
                queryCounting = em.createQuery(queryStr, Integer.class);
                queryCounting.setParameter("rollNumber", "%" + sSearch + "%");
                queryCounting.setParameter("fullName", "%" + sSearch + "%");
                iTotalDisplayRecords = ((Number) queryCounting.getSingleResult()).intValue();
            } else {
                iTotalDisplayRecords = iTotalRecords;
            }

            // Query danh sách sv
            queryStr = "SELECT s FROM StudentEntity s" +
                    (!sSearch.isEmpty() ? " WHERE s.rollNumber LIKE :rollNumber OR s.fullName LIKE :fullName" : "");
            TypedQuery<StudentEntity> query = em.createQuery(queryStr, StudentEntity.class);
            query.setFirstResult(iDisplayStart);
            query.setMaxResults(iDisplayLength);
            if (!sSearch.isEmpty()) {
                query.setParameter("rollNumber", "%" + sSearch + "%");
                query.setParameter("fullName", "%" + sSearch + "%");
            }
            List<StudentEntity> studentList = query.getResultList();

            List<List<String>> result = new ArrayList<>();
            DocumentStudentEntity ds = null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (StudentEntity std : studentList) {
                List<String> dataList = new ArrayList<String>();
                ds = null;

                dataList.add(std.getRollNumber());
                dataList.add(std.getFullName());
                dataList.add(sdf.format(std.getDateOfBirth()));
                dataList.add(std.getGender() == Enums.Gender.MALE.getValue()
                        ? Enums.Gender.MALE.getName() : Enums.Gender.FEMALE.getName());
                dataList.add(std.getProgramId() != null ? std.getProgramId().getName() : "N/A");
                dataList.add(Lists.reverse(std.getDocumentStudentEntityList()).get(0).getCurriculumId() == null ? "N/A" : Lists.reverse(std.getDocumentStudentEntityList()).get(0).getCurriculumId().getName());
                dataList.add(std.getId() + "");

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

    @RequestMapping(value = "/studentList/getAllMarks", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject GetAllStudentMarks(int studentId) {
        JsonObject jsonObj = new JsonObject();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            // Lấy thông tin sv
            String queryStr = "SELECT s FROM StudentEntity s WHERE s.id = :sId";
            TypedQuery<StudentEntity> queryStudent = em.createQuery(queryStr, StudentEntity.class);
            queryStudent.setParameter("sId", studentId);
            StudentEntity student = queryStudent.getSingleResult();

            StudentMarkModel model = new StudentMarkModel();
            model.setStudentId(studentId);
            model.setStudentName(student.getFullName());
            model.setRollNumber(student.getRollNumber());

            // Lấy danh sách điểm
            queryStr = "SELECT m FROM MarksEntity m WHERE m.isActivated = true and m.studentId.id = :sId";
            TypedQuery<MarksEntity> query = em.createQuery(queryStr, MarksEntity.class);
            query.setParameter("sId", studentId);

            List<MarksEntity> markList = query.getResultList();
            markList = Ultilities.FilterStudentsOnlyPassAndFail(markList);

            List<MarkModel> markListModel = new ArrayList<>();
            for (MarksEntity m : markList) {
                MarkModel data = new MarkModel();
                data.setSemester(m.getSemesterId().getSemester());
                data.setSubject(m.getSubjectMarkComponentId() != null ? m.getSubjectMarkComponentId().getSubjectId().getId() : "N/A");
                data.setStatus(m.getStatus());
                data.setAverageMark(m.getAverageMark());

                markListModel.add(data);
            }
            model.setMarkList(markListModel);

            String result = new Gson().toJson(model);

            jsonObj.addProperty("success", true);
            jsonObj.addProperty("studentMarkDetail", result);
            em.close();
        } catch (Exception e) {
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("error", e.getMessage());
        }

        return jsonObj;
    }

    private class StudentDetailModel {
        public int term;
        public List<MarkModel> markList;
    }
}


