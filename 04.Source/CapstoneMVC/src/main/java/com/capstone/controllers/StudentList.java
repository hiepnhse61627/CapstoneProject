package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.models.MarkModel;
import com.capstone.models.StudentMarkModel;
import com.capstone.models.Ultilities;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class StudentList {

    @RequestMapping("/studentList")
    public ModelAndView StudentListAll() {
        ModelAndView view = new ModelAndView("StudentList");
        view.addObject("title", "Danh sách sinh viên");

        return view;
    }

    @RequestMapping(value = "/loadStudentList")
    @ResponseBody
    public JsonObject LoadStudentListAll(@RequestParam Map<String, String> params) {
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
            // Đếm số lượng sv
            queryStr = "SELECT COUNT(s) FROM StudentEntity s";
            TypedQuery<Integer> queryCounting = em.createQuery(queryStr, Integer.class);
            iTotalRecords = ((Number) queryCounting.getSingleResult()).intValue();

            // Query danh sách sv
            queryStr = "SELECT s FROM StudentEntity s";
            if (!sSearch.isEmpty()) {
                queryStr += " WHERE s.rollNumber LIKE :sRollNum OR s.fullName LIKE :sName";
            }

            TypedQuery<StudentEntity> query = em.createQuery(queryStr, StudentEntity.class);
//                    .setFirstResult(iDisplayStart)
//                    .setMaxResults(iDisplayLength);

            if (!sSearch.isEmpty()) {
                query.setParameter("sRollNum", "%" + sSearch + "%");
                query.setParameter("sName", "%" + sSearch + "%");
            }

            List<StudentEntity> studentList = query.getResultList();
            iTotalDisplayRecords = studentList.size();
            List<List<String>> result = new ArrayList<>();
            studentList = studentList.stream().skip(iDisplayStart).limit(iDisplayLength).collect(Collectors.toList());

            for (StudentEntity std : studentList) {
                List<String> dataList = new ArrayList<String>() {{
                    add(std.getRollNumber());
                    add(std.getFullName());
                    add(std.getId() + "");
                }};
                result.add(dataList);
            }

            JsonArray aaData = (JsonArray) new Gson()
                    .toJsonTree(result, new TypeToken<List<List<String>>>() {}.getType());

            jsonObj.addProperty("iTotalRecords", iTotalRecords);
//            jsonObj.addProperty("iTotalDisplayRecords", iTotalRecords);
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
            queryStr = "SELECT m FROM MarksEntity m WHERE m.studentId.id = :sId";
            TypedQuery<MarksEntity> query = em.createQuery(queryStr, MarksEntity.class);
            query.setParameter("sId", studentId);

            List<MarksEntity> markList = query.getResultList();
            markList = Ultilities.SortMarkBySemester(markList);

            List<MarkModel> markListModel = new ArrayList<>();
            for (MarksEntity m : markList) {
                MarkModel data = new MarkModel();
                data.setSemester(m.getSemesterId().getSemester());
                data.setSubject(m.getSubjectMarkComponentId() != null ? m.getSubjectMarkComponentId().getSubjectId().getId() : "N/A");
                data.setClass1(m.getCourseId().getClass1());
                data.setStatus(m.getStatus());
                data.setAverageMark(m.getAverageMark());

                markListModel.add(data);
            }
            model.setMarkList(markListModel);

            String result = new Gson().toJson(model);

            jsonObj.addProperty("success", true);
            jsonObj.addProperty("studentMarkDetail", result);
        } catch (Exception e) {
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("error", e.getMessage());
        }

        return jsonObj;
    }

}
