package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GoodStudentController {

    @RequestMapping("/goodStudent")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("GoodStudent");
        IRealSemesterService semesterService = new RealSemesterServiceImpl();

        List<RealSemesterEntity> semesterList = semesterService.getAllSemester();
        semesterList = Ultilities.SortSemesters(semesterList);
        view.addObject("semesterList", semesterList);

        view.addObject("title", "Danh sách sinh viên giỏi");

        return view;
    }

    @RequestMapping("/goodStudent/getStudentList")
    @ResponseBody
    public JsonObject GetGoodStudentDataTable(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();
        IMarksService marksService = new MarksServiceImpl();
        IStudentService studentService = new StudentServiceImpl();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            int semesterId = Integer.parseInt(params.get("semesterId"));
            int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
            int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
            String sSearch = params.get("sSearch").trim();

            List<GoodStudentModel> studentList = new ArrayList<>();
            List<Integer> studentIdList;

            // Get studentId list in a semester and average mark of all subjects >= 8
            String queryStr = "SELECT m.studentId.id" +
                    " FROM MarksEntity m, SubjectMarkComponentEntity smc, MarkComponentEntity mc" +
                    " WHERE m.semesterId.id = :semesterId AND m.status LIKE :status AND m.subjectMarkComponentId.id = smc.id" +
                    " AND smc.markComponentId.id = mc.id AND mc.name LIKE :markComponentName" +
                    " AND (m.studentId.rollNumber LIKE :rollNumber OR m.studentId.fullName LIKE :fullName)" +
                    " GROUP BY m.studentId.id" +
                    " HAVING (SUM(m.averageMark) / COUNT(m) >= 8) ORDER BY m.studentId.id";
            Query queryStudentId = em.createQuery(queryStr);
            queryStudentId.setParameter("semesterId", semesterId);
            queryStudentId.setParameter("status", "%pass%");
            queryStudentId.setParameter("markComponentName", "%average%");
            queryStudentId.setParameter("rollNumber", "%" + sSearch + "%");
            queryStudentId.setParameter("fullName", "%" + sSearch + "%");
            studentIdList = queryStudentId.getResultList();

            if (!studentIdList.isEmpty()) {
                // Get lastest curriculumId List
                queryStr = "SELECT DISTINCT ds.curriculumId.id FROM DocumentStudentEntity ds WHERE ds.studentId.id IN :sList" +
                        " AND ds.createdDate = (SELECT MAX(tDS.createdDate) FROM DocumentStudentEntity tDS WHERE tDS.id = ds.id)";
                Query queryDocStudent = em.createQuery(queryStr);
                queryDocStudent.setParameter("sList", studentIdList);
                List<Integer> curriculumIdList = queryDocStudent.getResultList();

                // Get SubjectCurricumlum List
                queryStr = "SELECT sc FROM SubjectCurriculumEntity sc WHERE sc.curriculumId.id IN :sList";
                TypedQuery<SubjectCurriculumEntity> querySubjectCurriculum =
                        em.createQuery(queryStr, SubjectCurriculumEntity.class);
                querySubjectCurriculum.setParameter("sList", curriculumIdList);
                List<SubjectCurriculumEntity> subjectCurriculumList = querySubjectCurriculum.getResultList();

                // Change SubjectCurriculum List to HashMap
                Map<Integer, List<SubjectCurriculumEntity>> subjectCurriculumMap = new HashMap<>();
                for (SubjectCurriculumEntity sc: subjectCurriculumList) {
                    int currentCurriId = sc.getCurriculumId().getId();
                    if (subjectCurriculumMap.get(currentCurriId) != null) {
                        subjectCurriculumMap.get(currentCurriId).add(sc);
                    } else {
                        List<SubjectCurriculumEntity> newList = new ArrayList<>();
                        newList.add(sc);
                        subjectCurriculumMap.put(currentCurriId, newList);
                    }
                }

                String idStr = "(" + studentIdList.get(0);
                for (int i = 1; i < studentIdList.size(); i++) {
                    idStr += "," + studentIdList.get(i);
                }
                idStr += ")";

                // Get student detail
                String sqlStr = "SELECT m.StudentId, s.RollNumber, s.FullName, m.Id AS MarkId, smc.SubjectId, m.AverageMark, ds.CurriculumId, sc.TermNumber" +
                        " FROM Marks m INNER JOIN Student s ON s.Id = m.StudentId" +
                        " INNER JOIN Subject_MarkComponent smc ON m.SubjectMarkComponentId = smc.Id" +
                        " INNER JOIN MarkComponent mc ON smc.MarkComponentId = mc.Id" +
                        " INNER JOIN Document_Student ds ON m.StudentId = ds.StudentId" +
                        " INNER JOIN Subject_Curriculum sc ON ds.CurriculumId = sc.CurriculumId AND sc.SubjectId = smc.SubjectId" +
                        " AND m.SemesterId = ? AND m.StudentId IN " + idStr +
                        " AND m.Status LIKE '%pass%' AND mc.Name LIKE '%average%'" +
                        " ORDER BY m.StudentId, sc.TermNumber";
                Query queryDetail = em.createNativeQuery(sqlStr);
                queryDetail.setParameter(1, semesterId);
                List<Object[]> studentDetailList = queryDetail.getResultList();

                for (int curStudentId : studentIdList) {
                    GoodStudentModel studentModel = new GoodStudentModel();
                    studentModel.setStudentId(curStudentId);
                    // Set data for student
                    int currentTerm = 0;
                    int currentCurriculum = 0;
                    String rollNumber = "";
                    String fullName = "";
                    for (Object[] stDetail : studentDetailList) {
                        int studentId = (int) stDetail[0];
                        if (curStudentId == studentId) {
                            GoodStudentMarkModel markModel = new GoodStudentMarkModel();
                            rollNumber = stDetail[1].toString();
                            fullName = stDetail[2].toString();
                            markModel.setMarkId((int) stDetail[3]);
                            markModel.setSubjectId(stDetail[4].toString());
                            markModel.setMark((double) stDetail[5]);
                            currentCurriculum = (int) stDetail[6];
                            currentTerm = (int) stDetail[7]; // term sort tăng dần, term cuối cùng = kỳ đang học

                            studentModel.getMarkList().add(markModel);
                        }
                    }
                    studentModel.setRollNumber(rollNumber);
                    studentModel.setFullName(fullName);
                    studentModel.setCurrentTerm(currentTerm);
                    studentModel.setCurriculumId(currentCurriculum);

                    if (currentCurriculum > 0) {
                        List<String> subjectIds = new ArrayList<>();
                        for (SubjectCurriculumEntity sc : subjectCurriculumMap.get(currentCurriculum)) {
                            if (sc.getTermNumber() == currentTerm) {
                                studentModel.getSubjectCurriculumList().add(sc);
                                subjectIds.add(sc.getSubjectId().getId());
                            }
                        }

                        // validate student
                        boolean isValidated = true;
                        queryStr = "SELECT COUNT(m) FROM MarksEntity m" +
                                " INNER JOIN SubjectMarkComponentEntity smc" +
                                " ON m.subjectMarkComponentId.id = smc.id AND smc.subjectId.id NOT IN :sList" +
                                " AND m.studentId.id = :studentId AND m.semesterId.id = :semesterId";
                        Query queryCountOtherSubjectInTerm = em.createQuery(queryStr);
                        queryCountOtherSubjectInTerm.setParameter("sList", subjectIds);
                        queryCountOtherSubjectInTerm.setParameter("studentId", curStudentId);
                        queryCountOtherSubjectInTerm.setParameter("semesterId", semesterId);
                        int numOfOtherSubjectsInTerm = ((Number) queryCountOtherSubjectInTerm.getFirstResult()).intValue();

                        queryStr = "SELECT COUNT(m) FROM MarksEntity m" +
                                " INNER JOIN SubjectMarkComponentEntity smc" +
                                " ON m.subjectMarkComponentId.id = smc.id AND smc.subjectId.id IN :sList" +
                                " AND m.studentId.id = :studentId";
                        Query queryCountSubjectsStudyMoreThanOne = em.createQuery(queryStr);
                        queryCountSubjectsStudyMoreThanOne.setParameter("sList", subjectIds);
                        queryCountSubjectsStudyMoreThanOne.setParameter("studentId", curStudentId);
                        int numOfStudying = ((Number) queryCountSubjectsStudyMoreThanOne.getFirstResult()).intValue();

                        if (studentModel.getMarkList().size() != studentModel.getSubjectCurriculumList().size()
                                || numOfOtherSubjectsInTerm > 0 || numOfStudying > studentModel.getMarkList().size()) {
                            isValidated = false;
                        } else {
                            for (SubjectCurriculumEntity sc : studentModel.getSubjectCurriculumList()) {
                                boolean isFound = false;
                                for (GoodStudentMarkModel mark : studentModel.getMarkList()) {
                                    if (sc.getSubjectId().getId().equalsIgnoreCase(mark.getSubjectId())) {
                                        isFound = true;
                                        break;
                                    }
                                }

                                if (!isFound) {
                                    isValidated = false;
                                    break;
                                }
                            }
                        }

                        if (isValidated) {
                            studentList.add(studentModel);
                        }
                    }

                }
            }

            List<GoodStudentModel> finalList = studentList.stream()
                    .skip(iDisplayStart).limit(iDisplayLength).collect(Collectors.toList());
            List<List<String>> result = new ArrayList<>();
            for (GoodStudentModel st : finalList) {
                List<String> list = new ArrayList<>();
                list.add(st.getRollNumber());
                list.add(st.getFullName());
                list.add(calculateAverage(st.getMarkList()) + "");

                result.add(list);
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            jsonObject.addProperty("iTotalRecords", studentList.size());
            jsonObject.addProperty("iTotalDisplayRecords", studentList.size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return jsonObject;
    }

    private double calculateAverage(List<GoodStudentMarkModel> markList) {
        double result = 0;
        if (!markList.isEmpty()) {
            for (GoodStudentMarkModel mark : markList) {
                result += mark.getMark();
            }

            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.FLOOR);
            result = Math.round(result / markList.size() * 10.0) / 10.0;
        }

        return result;
    }

}
