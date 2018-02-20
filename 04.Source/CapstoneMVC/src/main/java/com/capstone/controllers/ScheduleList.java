package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.Logger;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.*;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.capstone.models.Ultilities.sendNotification;

@Controller
public class ScheduleList {

    IRoomService roomService = new RoomServiceImpl();

    IScheduleService scheduleService = new ScheduleServiceImpl();

    ISlotService slotService = new SlotServiceImpl();

    IDaySlotService daySlotService = new DaySlotServiceImpl();

    ICourseStudentService courseStudentService = new CourseStudentServiceImpl();

    IEmployeeService employeeService = new EmployeeServiceImpl();


    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    @RequestMapping("/scheduleList")
    public ModelAndView ScheduleListAll() {
        ModelAndView view = new ModelAndView("ScheduleList");
        view.addObject("title", "Danh sách lịch học");

        List<RoomEntity> rooms = roomService.findAllRooms();
        view.addObject("rooms", rooms);

        List<EmployeeEntity> emps = employeeService.findAllEmployees();
        view.addObject("employees", emps);

        List<SlotEntity> slots = slotService.findAllSlots();
        view.addObject("slots", slots);

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

                dataList.add(schedule.getId() + "");
                dataList.add(schedule.getCourseId().getSubjectCode());
                dataList.add(schedule.getGroupName());
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


    // edit course return json success or not
    @RequestMapping(value = "/schedule/edit")
    @ResponseBody
    public JsonObject EditSchedule(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        Map<StudentEntity, List<ScheduleEntity>> studentsMap = new HashMap<>();

        try {


            Gson gson = new Gson();
//            Type type = new TypeToken<List<String>>() {}.getType();
//            List<String> dayOfWeekList = gson.fromJson(params.get("dayOfWeekList"), type);
//
//            SimpleDateFormat format1=new SimpleDateFormat("dd/MM/yyyy");
//            Date dt1=format1.parse(params.get("startDate"));
//            DateFormat format2=new SimpleDateFormat("E");
//            String finalDay=format2.format(dt1);

            Type type2 = new TypeToken<List<String>>() {
            }.getType();
            List<String> slots = gson.fromJson(params.get("slots"), type2);

            for (String aSlotString : slots) {
                SlotEntity aSlot = slotService.findSlotsByName(aSlotString).get(0);
                DaySlotEntity aDaySlot = daySlotService.findDaySlotByDateAndSlot(params.get("startDate"), aSlot);
                List<EmployeeEntity> lectures = employeeService.findEmployeesByFullName(params.get("lecture"));
                List<RoomEntity> rooms = roomService.findRoomsByName(params.get("room"));

                if (aDaySlot == null) {
                    aDaySlot = new DaySlotEntity();
                    aDaySlot.setSlotId(aSlot);
                    aDaySlot.setDate(params.get("startDate"));
                } else {
                    ScheduleEntity existingSchedule = scheduleService.findScheduleByDateSlotAndGroupName(aDaySlot, params.get("clazz"));
                    if (existingSchedule != null) {
                        jsonObj.addProperty("false", false);
                        jsonObj.addProperty("message", "Lớp này đã có lịch học vào thời gian này");
                        return jsonObj;
                    } else if (lectures != null && lectures.size() > 0) {
                        existingSchedule = scheduleService.findScheduleByDateSlotAndLecture(aDaySlot, lectures.get(0));
                        if (existingSchedule != null) {
                            jsonObj.addProperty("false", false);
                            jsonObj.addProperty("message", "GV đã có lịch dạy vào thời gian này");
                            return jsonObj;
                        } else {
                            if (rooms != null && rooms.size() > 0) {
                                existingSchedule = scheduleService.findScheduleByDateSlotAndRoom(aDaySlot, rooms.get(0));
                                if (existingSchedule != null) {
                                    jsonObj.addProperty("false", false);
                                    jsonObj.addProperty("message", "Phòng đã có lớp học vào thời gian này");
                                    return jsonObj;
                                }
                            }
                        }
                    }
                }

                ScheduleEntity model = scheduleService.findScheduleById(Integer.parseInt(params.get("scheduleId")));

                model.setDateId(aDaySlot);

                if (rooms != null && rooms.size() > 0) {
                    model.setRoomId(rooms.get(0));
                }

                if (lectures != null && lectures.size() > 0) {
                    model.setEmpId(lectures.get(0));
                }

                scheduleService.updateSchedule(model);
                jsonObj.addProperty("success", true);

                List<ScheduleEntity> scheduleEntities = new ArrayList<>();
                scheduleEntities.add(model);

                List<CourseStudentEntity> courseStudentEntityList = courseStudentService.findCourseStudentByGroupNameAndCourse(model.getGroupName(), model.getCourseId());
                if (courseStudentEntityList != null) {
                    for (CourseStudentEntity courseStudentEntity : courseStudentEntityList) {
                        List<ScheduleEntity> studentSchedule = new ArrayList<>();
                        StudentEntity aStudent = courseStudentEntity.getStudentId();
                        if (studentsMap.get(aStudent) == null) {
                            studentsMap.put(aStudent, new ArrayList<ScheduleEntity>());
                        }
                        studentSchedule = studentsMap.get(aStudent);
                        studentSchedule = new ArrayList<>(studentSchedule);

                        ScheduleEntity tmp2 = studentSchedule.stream().filter(q -> q.getRoomId().getId() == model.getRoomId().getId()
                                && q.getDateId().getId() == model.getDateId().getId()).findFirst().orElse(null);

                        if (tmp2 == null) {
                            studentSchedule.add(model);
                        }

                        studentsMap.put(aStudent, studentSchedule);
                    }
                }

                for (StudentEntity key : studentsMap.keySet()) {
                    sendNotification("Your schedule has been changed", key.getEmail().substring(0, key.getEmail().indexOf("@")), studentsMap.get(key), androidPushNotificationsService);
                }

                Ultilities.sendNotification("Your schedule has been changed", model.getEmpId().getEmailEDU().substring(0, model.getEmpId().getEmailEDU().indexOf("@")), scheduleEntities, androidPushNotificationsService);
            }


        } catch (Exception e) {
            Logger.writeLog(e);
            jsonObj.addProperty("false", false);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

}


