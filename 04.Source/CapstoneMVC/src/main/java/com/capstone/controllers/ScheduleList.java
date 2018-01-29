package com.capstone.controllers;

import com.capstone.entities.EmployeeEntity;
import com.capstone.entities.ScheduleEntity;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ScheduleList {

    @RequestMapping("/scheduleList")
    public ModelAndView ScheduleListAll() {
        ModelAndView view = new ModelAndView("ScheduleList");
        view.addObject("title", "Danh sách lịch học");

        return view;
    }

    @RequestMapping(value = "/loadScheduleList")
    @ResponseBody
    public JsonObject LoadScheduleListAll(@RequestParam Map<String, String> params) {
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
            // Đếm số lượng lịch học
            queryStr = "SELECT COUNT(s) FROM ScheduleEntity s";
            TypedQuery<Integer> queryCounting = em.createQuery(queryStr, Integer.class);
            iTotalRecords = ((Number) queryCounting.getSingleResult()).intValue();

            // Đếm số lượng lịch học sau khi filter
            if (!sSearch.isEmpty()) {
                queryStr = "SELECT COUNT(*)\n" +
                        "  FROM Schedule s\n" +
                        "   INNER JOIN Course c ON s.CourseId=c.Id\n" +
                        "    INNER JOIN  Day_Slot d ON s.DateId=d.Id " +
                        "    INNER JOIN  Employee e ON s.EmpId=e.Id " +
                        "WHERE (s.CourseId = c.Id AND c.SubjectCode LIKE '%" + sSearch + "%') OR \n" +
                        " (s.DateId = d.Id AND d.Date LIKE '%" + sSearch + "%') OR" +
                        " (s.EmpId = e.Id AND e.FullName LIKE '%" + sSearch + "%')";
                Query queryCounting2 = em.createNativeQuery(queryStr);
                iTotalDisplayRecords = ((Number) queryCounting2.getSingleResult()).intValue();
            } else {
                iTotalDisplayRecords = iTotalRecords;
            }

            // Query danh sách lịch học
            queryStr = "SELECT s FROM ScheduleEntity s" +
                    (sSearch.isEmpty() ? "" :
                            "   INNER JOIN CourseEntity c ON s.courseId.id=c.id" +
                                    "    INNER JOIN  DaySlotEntity d ON s.dateId.id=d.id " +
                                    "    INNER JOIN  EmployeeEntity e ON s.empId.id=e.id " +
                                    "WHERE (s.courseId.id = c.id AND c.subjectCode LIKE '%" + sSearch + "%') OR \n" +
                                    " (s.dateId.id = d.id AND d.date LIKE '%" + sSearch + "%') OR" +
                                    "(s.empId.id = e.id AND e.fullName LIKE '%" + sSearch + "%')");
            TypedQuery<ScheduleEntity> query = em.createQuery(queryStr, ScheduleEntity.class);
            query.setFirstResult(iDisplayStart);
            query.setMaxResults(iDisplayLength);

            List<ScheduleEntity> scheduleList = query.getResultList();

            List<List<String>> result = new ArrayList<>();
            for (ScheduleEntity schedule : scheduleList) {
                List<String> dataList = new ArrayList<String>();

                dataList.add(schedule.getCourseId().getSubjectCode());
                dataList.add(schedule.getDateId().getDate());
                dataList.add(schedule.getDateId().getSlotId().getSlotName());
                dataList.add(schedule.getRoomId().getName());
                dataList.add(schedule.getEmpId().getFullName());

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

}


