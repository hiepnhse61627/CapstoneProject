package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.DatatableModel;
import com.capstone.models.Logger;
import com.capstone.models.MobileUserModel;
import com.capstone.models.ScheduleModel;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class CourseStudentController {

    @Autowired
    ServletContext context;

    ICourseStudentService courseStudentService = new CourseStudentServiceImpl();

    IScheduleService scheduleService = new ScheduleServiceImpl();
//    @RequestMapping("/course")
//    public ModelAndView CoursePage() {
//        ModelAndView view = new ModelAndView("CoursePage");
//        view.addObject("title", "Danh sách khóa học");
//
//        ISubjectService subjectService = new SubjectServiceImpl();
//        view.addObject("subjects", subjectService.getAllSubjects());
//
//        return view;
//    }

    @RequestMapping(value = "/getScheduleStudent", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject getScheduleStudentByEmail(@RequestBody String body) {
        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(body);
        String email = obj.get("email").getAsString();
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            String queryStr = "SELECT s FROM StudentEntity s" +
                    " WHERE s.email LIKE :email";
            Query query = em.createQuery(queryStr);
            query.setParameter("email", "%" + email + "%");

            StudentEntity std = (StudentEntity) query.getSingleResult();

            List<CourseStudentEntity> courseStudentEntities = courseStudentService.findCourseStudentByStudent(std);
            List<ScheduleEntity> allScheduleList = new ArrayList<>();

            Gson gson = new Gson();
            obj = new JsonObject();

            HashSet<String> groupNameList = new HashSet();
            HashMap<CourseEntity, String>courseAndGroupNameMap = new HashMap<>();


            if(courseStudentEntities!=null && courseStudentEntities.size()>0){
                for(CourseStudentEntity courseStudent : courseStudentEntities){
//                    groupNameList.add(courseStudent.getGroupName());
                    courseAndGroupNameMap.put(courseStudent.getCourseId(),courseStudent.getGroupName());
                }

//                for(String groupName : groupNameList){
//                    List<ScheduleEntity> courseScheduleEntityList = scheduleService.findScheduleByGroupName(groupName);
//                    allScheduleList.addAll(courseScheduleEntityList);
//                }

                for(CourseEntity aCourse : courseAndGroupNameMap.keySet()){
                    List<ScheduleEntity> courseScheduleEntityList = scheduleService.findScheduleByGroupNameAndCourse(courseAndGroupNameMap.get(aCourse), aCourse);
                    allScheduleList.addAll(courseScheduleEntityList);
                }
            }

            List<ScheduleModel> scheduleModelList = new ArrayList<>();
            for (ScheduleEntity schedule : allScheduleList) {
                ScheduleModel model = new ScheduleModel();
                model.setCourseName(schedule.getCourseId().getSubjectCode());
                model.setDate(schedule.getDateId().getDate());
                model.setRoom(schedule.getRoomId().getName());
                model.setSlot(schedule.getDateId().getSlotId().getSlotName());
                model.setStartTime(schedule.getDateId().getSlotId().getStartTime());
                model.setEndTime(schedule.getDateId().getSlotId().getEndTime());
                if(schedule.getEmpId() != null){
                    model.setLecture(schedule.getEmpId().getFullName());
                }else{
                    model.setLecture(null);
                }
                scheduleModelList.add(model);
            }

            MobileUserModel user = new MobileUserModel();
            user.setCode(std.getRollNumber());
            user.setId(std.getId());
            user.setName(std.getFullName());
            user.setEmailEDU(std.getEmail());
            user.setPosition("SV");

            obj.add("user", parser.parse(gson.toJson(user)));

            Collections.sort(scheduleModelList, new Comparator<ScheduleModel>() {
                @Override
                public int compare(ScheduleModel o1, ScheduleModel o2) {
                    try {
                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        Date aDate = df.parse(o1.getDate());
                        Date aDate2 = df.parse(o2.getDate());
                        if (aDate.compareTo(aDate2) > 0) {
                            return 1;
                        }

                        if (aDate.compareTo(aDate2) == 0) {
                            String slot1 = o1.getSlot();
                            String slot2 = o2.getSlot();

                            if (slot1.compareTo(slot2) > 0) {
                                return 1;
                            }
                        }
                        return -1;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

            obj.add("scheduleList", parser.parse(gson.toJson(scheduleModelList)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

}
