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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.*;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.capstone.models.Ultilities.sendNotification;

@Controller
public class ScheduleList {

    IRoomService roomService = new RoomServiceImpl();

    IScheduleService scheduleService = new ScheduleServiceImpl();

    ISlotService slotService = new SlotServiceImpl();

    IDaySlotService daySlotService = new DaySlotServiceImpl();

    ICourseStudentService courseStudentService = new CourseStudentServiceImpl();

    ICourseService courseService = new CourseServiceImpl();

    IEmployeeService employeeService = new EmployeeServiceImpl();

    IRealSemesterService realSemesterService = new RealSemesterServiceImpl();

    ISubjectService subjectService = new SubjectServiceImpl();

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    @RequestMapping("/scheduleList")
    public ModelAndView ScheduleListAll() {
        ModelAndView view = new ModelAndView("ScheduleList");
        view.addObject("title", "Danh sách lịch dạy");

        List<SubjectEntity> subjects = subjectService.getAllSubjects();
        view.addObject("subjects", subjects);

        List<RoomEntity> rooms = roomService.findAllRooms();
        view.addObject("rooms", rooms);

        Set listCapacity = new HashSet();
        for (RoomEntity room : rooms) {
            listCapacity.add(room.getCapacity());
        }
        view.addObject("capacity", listCapacity);

        List<EmployeeEntity> emps = employeeService.findAllEmployees();
        view.addObject("employees", emps);

        List<SlotEntity> slots = slotService.findAllSlots();
        view.addObject("slots", slots);

        List<RealSemesterEntity> semesters = realSemesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);

        view.addObject("semesters", semesters);

        return view;
    }

    @RequestMapping("/scheduleChangeStatistic")
    public ModelAndView ScheduleChangeStatistic() {
        ModelAndView view = new ModelAndView("ScheduleChangeStatistic");
        view.addObject("title", "Danh sách đổi lịch");

        List<EmployeeEntity> emps = employeeService.findAllEmployees();
        view.addObject("employees", emps);

        return view;
    }

    @RequestMapping(value = "/scheduleChangeStatistic/get")
    @ResponseBody
    public JsonObject LoadScheduleChangeAll(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();

        try {
            Integer lectureId = null;
            String startDate = params.get("startDate");
            String endDate = params.get("endDate");

            if (!params.get("lecture").equals("")) {
                lectureId = Integer.parseInt(params.get("lecture"));
            }

            List<ScheduleEntity> scheduleList = scheduleService.findScheduleByLectureHaveParentSchedule(lectureId);

            if (!startDate.equals(endDate)) {
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                List<ScheduleEntity> removeList = new ArrayList<>();
                for (ScheduleEntity aSchedule : scheduleList) {
                    Date aDate = format.parse(aSchedule.getDateId().getDate());
                    if (aDate.before(format.parse(startDate)) || aDate.after(format.parse(endDate))) {
                        removeList.add(aSchedule);
                    }
                }

                scheduleList.removeAll(removeList);

            }

            List<List<String>> result = new ArrayList<>();
            for (ScheduleEntity schedule : scheduleList) {
                List<String> dataList = new ArrayList<String>();
                ScheduleEntity parentSchedule = scheduleService.findScheduleById(schedule.getParentScheduleId());
                dataList.add(schedule.getId() + "");
                dataList.add(schedule.getCourseId().getSubjectCode());

                if (schedule.getGroupName() != null) {
                    dataList.add(schedule.getGroupName());
                } else {
                    dataList.add("");
                }

                if (schedule.getDateId() != null) {
                    dataList.add(schedule.getDateId().getDate());
                    dataList.add(schedule.getDateId().getSlotId().getSlotName());
                } else {
                    dataList.add("");
                    dataList.add("");
                }

                if (schedule.getRoomId() != null) {
                    dataList.add(schedule.getRoomId().getName());
                } else {
                    dataList.add("");
                }

                if (schedule.getEmpId() != null) {
                    dataList.add(schedule.getEmpId().getFullName());
                } else {
                    dataList.add("");
                }

                dataList.add(parentSchedule.getDateId().getSlotId().getSlotName() + ", ngày " + parentSchedule.getDateId().getDate() + ", " +
                        parentSchedule.getEmpId().getEmailEDU().substring(0, parentSchedule.getEmpId().getEmailEDU().indexOf("@")));

                result.add(dataList);
            }

            Gson gson = new Gson();
            JsonArray array = (JsonArray) gson.toJsonTree(result);

            jsonObj.add("aaData", array);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
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
            queryStr = "SELECT COUNT(s) FROM ScheduleEntity s WHERE s.isActive IS NULL OR s.isActive = 'true'";
            TypedQuery<Integer> queryCounting = em.createQuery(queryStr, Integer.class);
            iTotalRecords = ((Number) queryCounting.getSingleResult()).intValue();

            // Đếm số lượng lịch học sau khi filter
            if (!sSearch.isEmpty()) {
                queryStr = "SELECT COUNT(*)\n" +
                        "  FROM Schedule s\n" +
                        "   INNER JOIN Course c ON s.CourseId=c.Id\n" +
                        "    INNER JOIN  Day_Slot d ON s.DateId=d.Id " +
                        "WHERE (s.isActive IS NULL OR s.isActive = 'true') AND " +
                        "((s.CourseId = c.Id AND c.SubjectCode LIKE '%" + sSearch + "%') OR \n" +
                        " (s.DateId = d.Id AND d.Date LIKE '%" + sSearch + "%') OR" +
                        " (s.groupName LIKE '%" + sSearch + "%'))";
                Query queryCounting2 = em.createNativeQuery(queryStr);
                iTotalDisplayRecords = ((Number) queryCounting2.getSingleResult()).intValue();
            } else {
                iTotalDisplayRecords = iTotalRecords;
            }

            // Query danh sách lịch học
            queryStr = "SELECT s FROM ScheduleEntity s" +
                    (sSearch.isEmpty() ? " WHERE s.isActive IS NULL OR s.isActive = 'true'" :
                            "   INNER JOIN CourseEntity c ON s.courseId.id=c.id" +
                                    "    INNER JOIN  DaySlotEntity d ON s.dateId.id=d.id " +
                                    "WHERE (s.isActive IS NULL OR s.isActive = 'true') AND " +
                                    "((s.courseId.id = c.id AND c.subjectCode LIKE '%" + sSearch + "%') OR \n" +
                                    " (s.dateId.id = d.id AND d.date LIKE '%" + sSearch + "%') OR" +
                                    " (s.groupName LIKE '%" + sSearch + "%'))");
            TypedQuery<ScheduleEntity> query = em.createQuery(queryStr, ScheduleEntity.class);
            query.setFirstResult(iDisplayStart);
            query.setMaxResults(iDisplayLength);

            List<ScheduleEntity> scheduleList = query.getResultList();

            List<List<String>> result = new ArrayList<>();
            for (ScheduleEntity schedule : scheduleList) {
                List<String> dataList = new ArrayList<String>();

                dataList.add(schedule.getId() + "");
                dataList.add(schedule.getCourseId().getSubjectCode());

                if (schedule.getGroupName() != null) {
                    dataList.add(schedule.getGroupName());
                } else {
                    dataList.add("");
                }

                if (schedule.getDateId() != null) {
                    dataList.add(schedule.getDateId().getDate());
                    dataList.add(schedule.getDateId().getSlotId().getSlotName());
                } else {
                    dataList.add("");
                    dataList.add("");
                }

                if (schedule.getRoomId() != null) {
                    dataList.add(schedule.getRoomId().getName());
                } else {
                    dataList.add("");
                }

                if (schedule.getEmpId() != null) {
                    dataList.add(schedule.getEmpId().getFullName());
                } else {
                    dataList.add("");
                }

                if (schedule.getRoomId() != null) {
                    dataList.add(schedule.getRoomId().getCapacity() + "");
                } else {
                    dataList.add("");
                }

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


    @RequestMapping(value = "/loadScheduleList/{employeeId}")
    @ResponseBody
    public JsonObject LoadScheduleListOfEmployee(@PathVariable("employeeId") int employeeId, @RequestParam Map<String, String> params) {
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
            queryStr = "SELECT COUNT(s) FROM ScheduleEntity s WHERE (s.isActive IS NULL OR s.isActive = 'true') AND s.empId.id = " + employeeId;
            TypedQuery<Integer> queryCounting = em.createQuery(queryStr, Integer.class);
            iTotalRecords = ((Number) queryCounting.getSingleResult()).intValue();

            // Đếm số lượng lịch học sau khi filter
            if (!sSearch.isEmpty()) {
                queryStr = "SELECT COUNT(*)\n" +
                        "  FROM Schedule s\n" +
                        "   INNER JOIN Course c ON s.CourseId=c.Id\n" +
                        "    INNER JOIN  Day_Slot d ON s.DateId=d.Id " +
                        "WHERE (s.isActive IS NULL OR s.isActive = 'true') AND " +
                        "((s.CourseId = c.Id AND c.SubjectCode LIKE '%" + sSearch + "%') OR \n" +
                        " (s.DateId = d.Id AND d.Date LIKE '%" + sSearch + "%')" +
                        " (s.groupName LIKE '%\" + sSearch + \"%')) AND" +
                        " (s.EmpId = " + employeeId + ")";
                Query queryCounting2 = em.createNativeQuery(queryStr);
                iTotalDisplayRecords = ((Number) queryCounting2.getSingleResult()).intValue();
            } else {
                iTotalDisplayRecords = iTotalRecords;
            }

            // Query danh sách lịch học
            queryStr = "SELECT s FROM ScheduleEntity s" +
                    (sSearch.isEmpty() ? " WHERE (s.isActive IS NULL OR s.isActive = 'true') AND s.empId.id=" + employeeId + "" :
                            "   INNER JOIN CourseEntity c ON s.courseId.id=c.id" +
                                    "    INNER JOIN  DaySlotEntity d ON s.dateId.id=d.id " +
                                    "    INNER JOIN  EmployeeEntity e ON s.empId.id=e.id " +
                                    "WHERE (s.isActive IS NULL OR s.isActive = 'true') AND " +
                                    "((s.courseId.id = c.id AND c.subjectCode LIKE '%" + sSearch + "%') OR \n" +
                                    " (s.dateId.id = d.id AND d.date LIKE '%" + sSearch + "%')" +
                                    " (s.groupName LIKE '%\" + sSearch + \"%')) AND" +
                                    " (s.empId.id = " + employeeId + ")");
            TypedQuery<ScheduleEntity> query = em.createQuery(queryStr, ScheduleEntity.class);
            query.setFirstResult(iDisplayStart);
            query.setMaxResults(iDisplayLength);

            List<ScheduleEntity> scheduleList = query.getResultList();

            List<List<String>> result = new ArrayList<>();
            for (ScheduleEntity schedule : scheduleList) {
                List<String> dataList = new ArrayList<String>();

                dataList.add(schedule.getId() + "");
                dataList.add(schedule.getCourseId().getSubjectCode());

                if (schedule.getGroupName() != null) {
                    dataList.add(schedule.getGroupName());
                } else {
                    dataList.add("");
                }

                if (schedule.getDateId() != null) {
                    dataList.add(schedule.getDateId().getDate());
                    dataList.add(schedule.getDateId().getSlotId().getSlotName());
                } else {
                    dataList.add("");
                    dataList.add("");
                }

                if (schedule.getRoomId() != null) {
                    dataList.add(schedule.getRoomId().getName());
                } else {
                    dataList.add("");
                }

                if (schedule.getEmpId() != null) {
                    dataList.add(schedule.getEmpId().getFullName());
                } else {
                    dataList.add("");
                }

                if (schedule.getRoomId() != null) {
                    dataList.add(schedule.getRoomId().getCapacity() + "");
                } else {
                    dataList.add("");
                }

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

    public static Date getDate(String s) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = format.parse(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String formatDate(Date s) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String date = "";
        try {
            date = format.format(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static List<String> getDayOfWeekInRange(String d1, String d2, String dayOfWeek) {
        int dayOfWeekType = 0;
        List<String> dateList = new ArrayList<>();
        switch (dayOfWeek) {
            case "Sun": {
                dayOfWeekType = Calendar.SUNDAY;
            }
            break;
            case "Mon": {
                dayOfWeekType = Calendar.MONDAY;
            }
            break;
            case "Tue": {
                dayOfWeekType = Calendar.TUESDAY;
            }
            break;
            case "Wed": {
                dayOfWeekType = Calendar.WEDNESDAY;
            }
            break;
            case "Thu": {
                dayOfWeekType = Calendar.THURSDAY;
            }
            break;
            case "Fri": {
                dayOfWeekType = Calendar.FRIDAY;
            }
            break;
            case "Sat": {
                dayOfWeekType = Calendar.SATURDAY;
            }
            break;
            default: {
                dayOfWeekType = 0;
            }
            break;
        }

        Date date1 = getDate(d1);
        Date date2 = getDate(d2);

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);
        while (!c2.before(c1)) {
            if (c1.get(Calendar.DAY_OF_WEEK) == dayOfWeekType) {
                dateList.add(formatDate(c1.getTime()));
            }
            c1.add(Calendar.DATE, 1);
        }

        return dateList;
    }

    // edit course return json success or not
    @RequestMapping(value = "/schedule/create")
    @ResponseBody
    public JsonObject CreateSchedule(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        Map<StudentEntity, List<ScheduleEntity>> studentsMap = new HashMap<>();
        EmployeeEntity aLecture = null;
        List<ScheduleEntity> scheduleEntities = new ArrayList<>();
        String mess = "";

        try {
            String startDate = params.get("startDate");
            String endDate = params.get("endDate");
            Map<String, List<String>> daySlotMap = new HashMap<>();

            Gson gson = new Gson();

            //Size of slots array and day of week array always equal
            Type type2 = new TypeToken<List<List<String>>>() {
            }.getType();
            List<List<String>> slots = gson.fromJson(params.get("slots"), type2);

            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> dayOfWeekList = gson.fromJson(params.get("dayOfWeekList"), type);

            for (int i = 0; i < dayOfWeekList.size(); i++) {
                List<String> dateList = getDayOfWeekInRange(startDate, endDate, dayOfWeekList.get(i));

                for (String aDate : dateList) {
                    daySlotMap.put(aDate, slots.get(i));
                }
            }

            for (String aDate : daySlotMap.keySet()) {
                for (String aSlotString : daySlotMap.get(aDate)) {
                    SlotEntity aSlot = slotService.findSlotsByName(aSlotString).get(0);
                    DaySlotEntity aDaySlot = daySlotService.findDaySlotByDateAndSlot(aDate, aSlot);
                    List<EmployeeEntity> lectures = employeeService.findEmployeesByFullName(params.get("lecture"));
                    List<RoomEntity> rooms = roomService.findRoomsByCapacity(Integer.parseInt(params.get("capacity")));

                    if (aDaySlot == null) {
                        aDaySlot = new DaySlotEntity();
                        aDaySlot.setSlotId(aSlot);
                        aDaySlot.setDate(aDate);
                        daySlotService.createDateSlot(aDaySlot);
                    } else {
                        ScheduleEntity existingSchedule = scheduleService.findScheduleByDateSlotAndGroupName(aDaySlot, params.get("clazz"));
                        if (existingSchedule != null) {
                            jsonObj.addProperty("fail", true);
                            jsonObj.addProperty("message", "Lớp này đã có lịch học vào " + existingSchedule.getDateId().getSlotId().getSlotName() +
                                    ", ngày " + existingSchedule.getDateId().getDate() + ", giảng viên " + existingSchedule.getEmpId().getFullName() +
                                    ", môn " + existingSchedule.getCourseId().getSubjectCode() +
                                    ", phòng " + existingSchedule.getRoomId().getName());
                            return jsonObj;
                        } else if (lectures != null && lectures.size() > 0) {
                            existingSchedule = scheduleService.findScheduleByDateSlotAndLecture(aDaySlot, lectures.get(0));
                            if (existingSchedule != null) {
                                jsonObj.addProperty("fail", true);
                                jsonObj.addProperty("message", "GV đã có lịch dạy vào " + existingSchedule.getDateId().getSlotId().getSlotName() +
                                        ", ngày " + existingSchedule.getDateId().getDate() + ", lớp " + existingSchedule.getGroupName() +
                                        ", môn " + existingSchedule.getCourseId().getSubjectCode() +
                                        ", phòng " + existingSchedule.getRoomId().getName());
                                return jsonObj;
                            }
                        }
                    }

                    ScheduleEntity model = new ScheduleEntity();
                    model.setGroupName(params.get("clazz"));
                    model.setDateId(aDaySlot);

                    RoomEntity selectedRoom = null;
                    CourseEntity aCourse = courseService.findCourseBySemesterAndSubjectCode(params.get("semester"), params.get("subject"));

                    if (aCourse != null) {
                        model.setCourseId(aCourse);
                    } else {
                        CourseEntity newCourse = new CourseEntity();
                        newCourse.setSemester(params.get("semester"));
                        newCourse.setSubjectCode(params.get("subject"));
                        courseService.createCourse(newCourse);
                        aCourse = courseService.findCourseBySubjectCode(params.get("subject"));
                        model.setCourseId(aCourse);

//                        jsonObj.addProperty("fail", true);
//                        jsonObj.addProperty("message", "Không có môn này trong kì đang chọn");
//                        return jsonObj;
                    }

                    if (rooms != null && rooms.size() > 0) {
                        for (RoomEntity aRoom : rooms) {
                            ScheduleEntity existingSchedule = scheduleService.findScheduleByDateSlotAndRoom(aDaySlot, aRoom);
                            if (existingSchedule == null) {
                                if (aCourse.getSubjectCode().contains("VOV")) {
                                    if (aRoom.getName().contains("VOV")) {
                                        selectedRoom = aRoom;
                                        break;
                                    }
                                }

                                if (aCourse.getSubjectCode().contains("LAB")) {
                                    if (aRoom.getNote().toLowerCase().contains("thực hành")) {
                                        selectedRoom = aRoom;
                                        break;
                                    }
                                }

                                if (!aCourse.getSubjectCode().contains("LAB") && !aCourse.getSubjectCode().contains("VOV")) {
                                    if (!aRoom.getName().contains("VOV") && !aRoom.getNote().toLowerCase().contains("thực hành")) {
                                        selectedRoom = aRoom;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (selectedRoom != null) {
                        model.setRoomId(selectedRoom);
                    } else {
                        mess += "<div> Không có phòng trống vào " + aDaySlot.getSlotId().getSlotName() + ", ngày " + aDaySlot.getDate() + "</div><br/>";
                        continue;
                    }

                    if (lectures != null && lectures.size() > 0) {
                        model.setEmpId(lectures.get(0));
                    } else {
                        jsonObj.addProperty("fail", true);
                        jsonObj.addProperty("message", "Không có giảng viên này");
                        return jsonObj;
                    }

                    model.setId(0);
                    model.setActive(true);
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
                    aLecture = model.getEmpId();
                }
            }

            scheduleService.createScheduleList(scheduleEntities);

            for (StudentEntity key : studentsMap.keySet()) {
                sendNotification("Your schedule has been changed", key.getEmail().substring(0, key.getEmail().indexOf("@")), studentsMap.get(key), androidPushNotificationsService, "create");
            }

            if (aLecture != null) {
                sendNotification("Your schedule has been changed", aLecture.getEmailEDU().substring(0, aLecture.getEmailEDU().indexOf("@")), scheduleEntities, androidPushNotificationsService, "create");
            }

            if (!mess.equals("")) {
                jsonObj.addProperty("warning", true);
                jsonObj.addProperty("message", mess);
            } else {
                jsonObj.addProperty("success", true);

            }

        } catch (Exception e) {
            Logger.writeLog(e);
            jsonObj.addProperty("fail", true);
            jsonObj.addProperty("message", e.getMessage());
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

            Type type2 = new TypeToken<List<String>>() {
            }.getType();
            List<String> slots = gson.fromJson(params.get("slots"), type2);

            for (String aSlotString : slots) {
                SlotEntity aSlot = slotService.findSlotsByName(aSlotString).get(0);
                DaySlotEntity aDaySlot = daySlotService.findDaySlotByDateAndSlot(params.get("startDate"), aSlot);
                List<EmployeeEntity> lectures = employeeService.findEmployeesByFullName(params.get("lecture"));
                List<RoomEntity> rooms = roomService.findRoomsByCapacity(Integer.parseInt(params.get("capacity")));

                if (aDaySlot == null) {
                    aDaySlot = new DaySlotEntity();
                    aDaySlot.setSlotId(aSlot);
                    aDaySlot.setDate(params.get("startDate"));
                    daySlotService.createDateSlot(aDaySlot);
                } else {
                    ScheduleEntity existingSchedule = scheduleService.findScheduleByDateSlotAndGroupName(aDaySlot, params.get("clazz"));
                    if (existingSchedule != null) {
                        if (!existingSchedule.getId().toString().equals(params.get("scheduleId"))) {
                            jsonObj.addProperty("fail", true);
                            jsonObj.addProperty("message", "Lớp này đã có lịch học vào " + existingSchedule.getDateId().getSlotId().getSlotName() +
                                    ", ngày " + existingSchedule.getDateId().getDate() + ", giảng viên " + existingSchedule.getEmpId().getFullName() +
                                    ", môn " + existingSchedule.getCourseId().getSubjectCode() +
                                    ", phòng " + existingSchedule.getRoomId().getName());
                            return jsonObj;
                        }
                    }
                    if (lectures != null && lectures.size() > 0) {
                        existingSchedule = scheduleService.findScheduleByDateSlotAndLecture(aDaySlot, lectures.get(0));
                        if (existingSchedule != null) {
                            if (!existingSchedule.getId().toString().equals(params.get("scheduleId"))) {
                                jsonObj.addProperty("fail", true);
                                jsonObj.addProperty("message", "GV đã có lịch dạy vào " + existingSchedule.getDateId().getSlotId().getSlotName() +
                                        ", ngày " + existingSchedule.getDateId().getDate() + ", lớp " + existingSchedule.getGroupName() +
                                        ", môn " + existingSchedule.getCourseId().getSubjectCode() +
                                        ", phòng " + existingSchedule.getRoomId().getName());
                                return jsonObj;
                            }
                        }
                    }
                }

                ScheduleEntity model = scheduleService.findScheduleById(Integer.parseInt(params.get("scheduleId")));

                RoomEntity selectedRoom = null;

                //find new room
                if (params.get("changeRoom").equals("true") ||
                        !model.getDateId().getDate().equals(aDaySlot.getDate())
                                || !model.getDateId().getSlotId().getSlotName().equals(aDaySlot.getSlotId().getSlotName())) {
                    if (rooms != null && rooms.size() > 0) {
                        for (RoomEntity aRoom : rooms) {
                            ScheduleEntity existingSchedule = scheduleService.findScheduleByDateSlotAndRoom(aDaySlot, aRoom);
                            if (existingSchedule == null) {
                                if (model.getCourseId().getSubjectCode().contains("VOV")) {
                                    if (aRoom.getName().contains("VOV")) {
                                        selectedRoom = aRoom;
                                        break;
                                    }
                                }

                                if (model.getCourseId().getSubjectCode().contains("LAB")) {
                                    if (aRoom.getNote().toLowerCase().contains("thực hành")) {
                                        selectedRoom = aRoom;
                                        break;
                                    }
                                }

                                if (!model.getCourseId().getSubjectCode().contains("LAB") && !model.getCourseId().getSubjectCode().contains("VOV")) {
                                    if (!aRoom.getName().contains("VOV") && !aRoom.getNote().toLowerCase().contains("thực hành")) {
                                        selectedRoom = aRoom;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (selectedRoom != null) {
                        model.setRoomId(selectedRoom);
                    } else {
                        jsonObj.addProperty("fail", true);
                        jsonObj.addProperty("message", "Không có phòng trống vào " + aDaySlot.getSlotId().getSlotName() + ",ngày " + aDaySlot.getDate());
                        return jsonObj;
                    }
                }


                if (model.getDateId().getDate().equals(aDaySlot.getDate())
                        && model.getDateId().getSlotId().getSlotName().equals(aDaySlot.getSlotId().getSlotName())
                        && model.getEmpId().getFullName().equals(lectures.get(0).getFullName())) {

                    model.setDateId(aDaySlot);

                    if (lectures != null && lectures.size() > 0) {
                        model.setEmpId(lectures.get(0));
                    } else {
                        jsonObj.addProperty("fail", true);
                        jsonObj.addProperty("message", "Không có giảng viên này");
                        return jsonObj;
                    }

                    scheduleService.updateSchedule(model);
                } else {

                    //change status of current record
                    model.setActive(false);
                    scheduleService.updateSchedule(model);

                    //add new record
                    model.setDateId(aDaySlot);

                    if (lectures != null && lectures.size() > 0) {
                        model.setEmpId(lectures.get(0));
                    } else {
                        jsonObj.addProperty("fail", true);
                        jsonObj.addProperty("message", "Không có giảng viên này");
                        return jsonObj;
                    }
                    model.setParentScheduleId(model.getId());
                    model.setId(0);
                    model.setActive(true);
                    scheduleService.createSchedule(model);
                }
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
                    sendNotification("Your schedule has been changed", key.getEmail().substring(0, key.getEmail().indexOf("@")), studentsMap.get(key), androidPushNotificationsService, "edit");
                }

                Ultilities.sendNotification("Your schedule has been changed", model.getEmpId().getEmailEDU().substring(0, model.getEmpId().getEmailEDU().indexOf("@")), scheduleEntities, androidPushNotificationsService, "edit");
            }


        } catch (Exception e) {
            Logger.writeLog(e);
            jsonObj.addProperty("fail", true);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

}


