package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.entities.fapEntities.*;
import com.capstone.models.Global;
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
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.Utilities;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.capstone.models.Ultilities.sendNotification;
import static com.capstone.services.DateUtil.formatDate;
import static com.capstone.services.DateUtil.getDate;

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

    IDepartmentService departmentService = new DepartmentServiceImpl();

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;


    @RequestMapping(value = "/deacttiveAllSchedule")
    @ResponseBody
    public JsonObject DeacttiveAllSchedule(@RequestParam Map<String, String> params) {
        Ultilities.logUserAction("DeacttiveAllSchedule ");

        JsonObject jsonObj = new JsonObject();
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();
            EntityTransaction etx = em.getTransaction();
            etx.begin();
            String queryStr = "UPDATE Schedule SET isActive = 'false'";
            Query query = em.createNativeQuery(queryStr);
            int countUpdated = query.executeUpdate();
            etx.commit();
            jsonObj.addProperty("success", true);
            jsonObj.addProperty("countUpdated", countUpdated);

        } catch (Exception e) {
            e.printStackTrace();
            jsonObj.addProperty("fail", true);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

    @RequestMapping("/scheduleList")
    public ModelAndView ScheduleListAll(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());
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
    public ModelAndView ScheduleChangeStatistic(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());
        ModelAndView view = new ModelAndView("ScheduleChangeStatistic");
        view.addObject("title", "Danh sách đổi lịch");

        List<EmployeeEntity> emps = employeeService.findAllEmployees();
        view.addObject("employees", emps);

        List<DepartmentEntity> departmentLists = departmentService.findAllDepartments();
        view.addObject("departments", departmentLists);

        return view;
    }

    @RequestMapping(value = "/loadScheduleList")
    @ResponseBody
    public JsonObject LoadScheduleListAll(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        List<List<String>> result = new ArrayList<>();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            String sSearch = params.get("sSearch");
            int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
            int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
            int iTotalRecords = 0;
            int iTotalDisplayRecords = 0;

            String lecture = "";
            String subjectCode = "";
            String slot = "";
            String groupName = "";
            String startDate = params.get("startDate");
            String endDate = params.get("endDate");

            if (!startDate.equals("")) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date tmpStart = DateUtil.getDate(startDate);
                startDate = format.format(tmpStart);

                Date tmpEnd = DateUtil.getDate(endDate);
                endDate = format.format(tmpEnd);
            }

            if (!params.get("lecture").equals("") && !params.get("lecture").equals("-1")) {
                lecture = params.get("lecture");
            }

            if (!params.get("slot").equals("") && !params.get("slot").equals("-1")) {
                slot = params.get("slot");
            }

            if (!params.get("subject").equals("") && !params.get("subject").equals("-1")) {
                subjectCode = params.get("subject");
            }

            if (!params.get("groupName").equals("") && !params.get("groupName").equals("-1")) {
                groupName = params.get("groupName");
            }

            if (!(lecture.equals("") && subjectCode.equals("") && slot.equals("") && groupName.equals("") && startDate.equals(""))) {
                String queryStr;
                // Đếm số lượng lịch học
                queryStr = "SELECT COUNT(s) FROM ScheduleEntity s WHERE s.isActive IS NULL OR s.isActive = 'true'";
                TypedQuery<Integer> queryCounting = em.createQuery(queryStr, Integer.class);
                iTotalRecords = ((Number) queryCounting.getSingleResult()).intValue();

                // Đếm số lượng lịch học sau khi filter
                if (!sSearch.isEmpty() || !lecture.equals("") || !subjectCode.equals("") || !slot.equals("") || !groupName.equals("") || !startDate.equals("")) {
                    queryStr = "SELECT COUNT(*)\n" +
                            "  FROM Schedule s\n" +
                            "   INNER JOIN Course c ON s.CourseId=c.Id\n" +
                            "    INNER JOIN  Day_Slot d ON s.DateId=d.Id " +
                            "    INNER JOIN Slot sl ON d.SlotId=sl.Id " +
                            "    INNER JOIN  Employee e ON s.EmpId=e.Id " +
                            "WHERE (s.isActive IS NULL OR s.isActive = 'true') AND " +
                            "(" +
                            "(s.CourseId = c.Id AND c.SubjectCode LIKE '%" + subjectCode + "%') AND \n" +
                            " (s.DateId = d.Id AND d.Date LIKE '%" + sSearch + "%') AND " +
                            "(d.SlotId=sl.Id AND sl.SlotName LIKE '%" + slot + "%') AND " +
                            " (s.EmpId = e.Id AND e.EmailEDU LIKE '%" + lecture + "%') AND" +
                            " (s.groupName LIKE '%" + groupName + "%') " +
                            " AND CONVERT(nvarchar(50), CONVERT(SMALLDATETIME, d.Date, 105), 23) >= '" + startDate + "' AND " +
                            " CONVERT(nvarchar(50), CONVERT(SMALLDATETIME, d.Date, 105), 23) <= '" + endDate + "')";
                    Query queryCounting2 = em.createNativeQuery(queryStr);
                    iTotalDisplayRecords = ((Number) queryCounting2.getSingleResult()).intValue();
                } else {
                    iTotalDisplayRecords = iTotalRecords;
                }

                // Query danh sách lịch học
                queryStr = "SELECT *" +
                        "  FROM Schedule s\n" +
                        (sSearch.isEmpty() && lecture.equals("") && subjectCode.equals("") && slot.equals("") && groupName.equals("") && startDate.equals("") ?
                                " INNER JOIN  Day_Slot d ON s.DateId=d.Id " +
                                        "WHERE s.isActive IS NULL OR s.isActive = 'true' " +
                                        "ORDER BY \n" +
                                        "  CONVERT(nvarchar(50), CONVERT(SMALLDATETIME, d.Date, 105), 23)" :
                                "   INNER JOIN Course c ON s.CourseId=c.Id\n" +
                                        "    INNER JOIN  Day_Slot d ON s.DateId=d.Id " +
                                        "    INNER JOIN Slot sl ON d.SlotId=sl.Id " +
                                        "    INNER JOIN  Employee e ON s.EmpId=e.Id " +
                                        "WHERE (s.isActive IS NULL OR s.isActive = 'true') AND " +
                                        "(" +
                                        " (s.CourseId = c.Id AND c.SubjectCode LIKE '%" + subjectCode + "%') AND \n" +
                                        " (s.DateId = d.Id AND d.Date LIKE '%" + sSearch + "%') AND " +
                                        " (d.SlotId=sl.Id AND sl.SlotName LIKE '%" + slot + "%') AND " +
                                        " (s.EmpId = e.Id AND e.EmailEDU LIKE '%" + lecture + "%') AND" +
                                        " (s.groupName LIKE '%" + groupName + "%')" +
                                        " AND CONVERT(nvarchar(50), CONVERT(SMALLDATETIME, d.Date, 105), 23) >= '" + startDate + "' AND " +
                                        " CONVERT(nvarchar(50), CONVERT(SMALLDATETIME, d.Date, 105), 23) <= '" + endDate + "'" +
                                        ")\n" +
                                        " ORDER BY \n" +
                                        "  CONVERT(nvarchar(50), CONVERT(SMALLDATETIME, d.Date, 105), 23)");
                Query query = em.createNativeQuery(queryStr, ScheduleEntity.class);
                query.setFirstResult(iDisplayStart);
                query.setMaxResults(iDisplayLength);

                List<ScheduleEntity> scheduleList = query.getResultList();


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

                    Date date1 = getDate(schedule.getDateId().getDate());
                    Date now = new Date();

                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(date1);
                    Calendar now1 = Calendar.getInstance();
                    now1.setTime(now);
                    now1.set(Calendar.HOUR_OF_DAY, 0);
                    now1.set(Calendar.MINUTE, 0);
                    now1.set(Calendar.SECOND, 0);
                    now1.set(Calendar.MILLISECOND, 0);

                    if (c1.after(now1) || c1.equals(now1)) {
                        dataList.add("false");
                    } else {
                        dataList.add("true");
                    }

                    result.add(dataList);
                }


            }
            jsonObj.addProperty("iTotalRecords", iTotalRecords);
            jsonObj.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
            JsonArray aaData = (JsonArray) new Gson()
                    .toJsonTree(result, new TypeToken<List<List<String>>>() {
                    }.getType());


            jsonObj.add("aaData", aaData);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    @RequestMapping(value = "/scheduleChangeStatistic/get")
    @ResponseBody
    public JsonObject LoadScheduleChangeAll(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        try {
            List<List<String>> result = loadScheduleChangeAllImpl(params);

            Gson gson = new Gson();
            JsonArray array = (JsonArray) gson.toJsonTree(result);

            jsonObj.add("aaData", array);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    public List<List<String>> loadScheduleChangeAllImpl(Map<String, String> params) {
        List<List<String>> result = new ArrayList<>();
        try {
            Integer lectureId = null;
            Integer departmentId = null;
            String startDate = params.get("startDate");
            String endDate = params.get("endDate");

            if (!startDate.equals("")) {
                if (!params.get("lecture").equals("") && !params.get("lecture").equals("-1")) {
                    lectureId = Integer.parseInt(params.get("lecture"));
                }

                if (!params.get("department").equals("") && !params.get("department").equals("-1")) {
                    departmentId = Integer.parseInt(params.get("department"));
                }

                List<ScheduleEntity> scheduleList = scheduleService.findScheduleByLectureHaveParentSchedule(lectureId);

                if (departmentId != null) {
                    DepartmentEntity aDepartment = departmentService.findDepartmentById(departmentId);
//                List<SubjectDepartmentEntity> aSubjectDepartmentList = subjectDepartmentService.findSubjectDepartmentsByDepartment(aDepartment);
                    List<SubjectEntity> subjectList = subjectService.findSubjectByDepartment(aDepartment);
                    List<ScheduleEntity> tmpList = new ArrayList<>();
                    for (ScheduleEntity aSchedule : scheduleList) {
                        for (SubjectEntity aSubject : subjectList) {
                            if (aSubject.getId().equals(aSchedule.getCourseId().getSubjectCode())) {
                                tmpList.add(aSchedule);
                                continue;
                            }
                        }
                    }
                    scheduleList = new ArrayList<>(tmpList);
                }

                if (!params.get("dateTextbox").equals("")) {
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

                Collections.sort(scheduleList, new Comparator<ScheduleEntity>() {
                    @Override
                    public int compare(ScheduleEntity o1, ScheduleEntity o2) {
                        try {
                            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                            Date aDate = df.parse(o1.getDateId().getDate());
                            Date aDate2 = df.parse(o2.getDateId().getDate());
                            if (aDate.compareTo(aDate2) > 0) {
                                return 1;
                            }

                            if (aDate.compareTo(aDate2) == 0) {
                                String slot1 = o1.getDateId().getSlotId().getSlotName();
                                String slot2 = o2.getDateId().getSlotId().getSlotName();

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

                for (ScheduleEntity schedule : scheduleList) {

                    List<String> dataList = new ArrayList<String>();

                    ScheduleEntity parentSchedule = scheduleService.findScheduleById(schedule.getParentScheduleId());
                    dataList.add(schedule.getId() + "");
                    if (schedule.getCourseId() != null) {
                        dataList.add(schedule.getCourseId().getSubjectCode());

                    } else {
                        dataList.add("");
                    }

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
                        dataList.add(schedule.getEmpId().getEmailEDU().substring(0, schedule.getEmpId().getEmailEDU().indexOf("@")));
                    } else {
                        dataList.add("");
                    }

                    if (parentSchedule != null) {
                        String slotName = "";
                        String dateName = "";
                        String empName = "";

                        if (parentSchedule.getDateId() != null) {
                            slotName = parentSchedule.getDateId().getSlotId().getSlotName();
                            dateName = parentSchedule.getDateId().getDate();
                        }

                        if (parentSchedule.getEmpId() != null) {
                            empName = parentSchedule.getEmpId().getEmailEDU().substring(0, parentSchedule.getEmpId().getEmailEDU().indexOf("@"));
                        }
                        dataList.add(slotName + ", ngày " + dateName + ", " + empName);

                    } else {
                        dataList.add("");
                    }

                    result.add(dataList);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    @RequestMapping(value = "/loadScheduleList/{employeeId}")
    @ResponseBody
    public JsonObject LoadScheduleListOfEmployee(@PathVariable("employeeId") int employeeId, @RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        List<List<String>> result = new ArrayList<>();
        try {

            String startDate = params.get("startDate");
            String endDate = params.get("endDate");

//            if (!startDate.equals(endDate)) {
            List<ScheduleEntity> scheduleList = scheduleService.findScheduleByLecture(employeeId);


            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            List<ScheduleEntity> removeList = new ArrayList<>();
            for (ScheduleEntity aSchedule : scheduleList) {
                Date aDate = format.parse(aSchedule.getDateId().getDate());
                if (aDate.before(format.parse(startDate)) || aDate.after(format.parse(endDate))
                        || aSchedule.getActive() == null || aSchedule.getActive() == false) {
                    removeList.add(aSchedule);
                }
            }
            scheduleList.removeAll(removeList);


            Collections.sort(scheduleList, new Comparator<ScheduleEntity>() {
                @Override
                public int compare(ScheduleEntity o1, ScheduleEntity o2) {
                    try {
                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        Date aDate = df.parse(o1.getDateId().getDate());
                        Date aDate2 = df.parse(o2.getDateId().getDate());
                        if (aDate.compareTo(aDate2) > 0) {
                            return 1;
                        }

                        if (aDate.compareTo(aDate2) == 0) {
                            String slot1 = o1.getDateId().getSlotId().getSlotName();
                            String slot2 = o2.getDateId().getSlotId().getSlotName();

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


                Date date1 = getDate(schedule.getDateId().getDate());
                Date now = new Date();

                Calendar c1 = Calendar.getInstance();
                c1.setTime(date1);
                Calendar now1 = Calendar.getInstance();
                now1.setTime(now);
                now1.set(Calendar.HOUR_OF_DAY, 0);
                now1.set(Calendar.MINUTE, 0);
                now1.set(Calendar.SECOND, 0);
                now1.set(Calendar.MILLISECOND, 0);

                if (c1.after(now1) || c1.equals(now1)) {
                    dataList.add("false");
                } else {
                    dataList.add("true");
                }

                result.add(dataList);
            }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonArray aaData = (JsonArray) new Gson()
                .toJsonTree(result, new TypeToken<List<List<String>>>() {
                }.getType());

        jsonObj.add("aaData", aaData);
        return jsonObj;
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

    public boolean isWeekDayEqual(String d1, String d2) {
        Date date1 = getDate(d1);
        Date date2 = getDate(d2);

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        return c1.get(Calendar.DAY_OF_WEEK) == c2.get(Calendar.DAY_OF_WEEK);
    }

    // edit course return json success or not
    @RequestMapping(value = "/schedule/create")
    @ResponseBody
    public JsonObject CreateSchedule(@RequestParam Map<String, String> params) {
        //logging user action
        Ultilities.logUserAction("CreateSchedule");
        JsonObject jsonObj = new JsonObject();
        Map<StudentEntity, List<ScheduleEntity>> studentsMap = new HashMap<>();
        EmployeeEntity aLecture = null;
        List<ScheduleEntity> scheduleEntities = new ArrayList<>();
        String mess = "";

        try {
            String startDate = params.get("startDate");
            String endDate = params.get("endDate");
            Map<String, List<String>> daySlotMap = new HashMap<>();
            String className = params.get("clazz");
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
                    ScheduleEntity model = new ScheduleEntity();

                    SlotEntity aSlot = slotService.findSlotsByName(aSlotString).get(0);
                    DaySlotEntity aDaySlot = daySlotService.findDaySlotByDateAndSlot(aDate, aSlot);
                    List<EmployeeEntity> lectures = employeeService.findEmployeesByFullName(params.get("lecture"));
                    List<RoomEntity> rooms = roomService.findRoomsByCapacity(Integer.parseInt(params.get("capacity")));

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
                    }

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
                                    ", ngày " + existingSchedule.getDateId().getDate() + ", giảng viên " + (existingSchedule.getEmpId() == null ? "" : existingSchedule.getEmpId().getFullName()) +
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


                            List<CourseStudentEntity> courseStudentEntities = courseStudentService.findCourseStudentByGroupNameAndCourse(className, aCourse);
                            List<StudentEntity> listOfStudent = new ArrayList<>();
                            for (CourseStudentEntity courseStudentEntity : courseStudentEntities) {
                                listOfStudent.add(courseStudentEntity.getStudentId());
                            }

                            for (StudentEntity aStudent : listOfStudent) {
                                HashSet<String> groupNameListOfStudent = new HashSet<>();

                                List<CourseStudentEntity> courseStudentEntitiesOfOneStudent = courseStudentService.findCourseStudentByStudent(aStudent);

                                for (CourseStudentEntity courseStudentEntity : courseStudentEntitiesOfOneStudent) {
                                    groupNameListOfStudent.add(courseStudentEntity.getGroupName());
                                }

                                for (String groupName : groupNameListOfStudent) {
                                    ScheduleEntity existingSchedule1 = scheduleService.findScheduleByDateSlotAndGroupName(aDaySlot, groupName);
                                    if (existingSchedule1 != null) {
                                        if (!existingSchedule1.getId().toString().equals(params.get("scheduleId"))) {
                                            jsonObj.addProperty("fail", true);
                                            jsonObj.addProperty("message", "Sinh viên " + aStudent.getRollNumber() + " của slot hiện tại đã có lịch học vào " +
                                                    "" + existingSchedule1.getDateId().getSlotId().getSlotName() +
                                                    ", ngày " + existingSchedule1.getDateId().getDate() + ", giảng viên " +
                                                    (existingSchedule1.getEmpId() == null ? "" : existingSchedule1.getEmpId().getFullName()) +
                                                    ", môn " + existingSchedule1.getCourseId().getSubjectCode() +
                                                    ", lớp " + groupName + ", phòng " + existingSchedule1.getRoomId().getName());
                                            return jsonObj;
                                        }
                                    }
                                }
                            }
                        }
                    }


                    model.setGroupName(className);
                    model.setDateId(aDaySlot);

                    RoomEntity selectedRoom = null;

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
        //logging user action
        Ultilities.logUserAction("EditSchedule");
        JsonObject jsonObj = new JsonObject();
        String mess = "";
        try {
            Gson gson = new Gson();

            Type type2 = new TypeToken<List<String>>() {
            }.getType();
            List<String> slots = gson.fromJson(params.get("slots"), type2);
            for (String aSlotString : slots) {
                SlotEntity aSlot = slotService.findSlotsByName(aSlotString).get(0);
                DaySlotEntity aDaySlot = daySlotService.findDaySlotByDateAndSlot(params.get("startDate"), aSlot);
                if (aDaySlot == null) {
                    aDaySlot = new DaySlotEntity();

                    aDaySlot.setDate(params.get("startDate"));
                    aDaySlot.setSlotId(aSlot);
                    aDaySlot = daySlotService.createDateSlot(aDaySlot);
                }

                List<EmployeeEntity> lectures = employeeService.findEmployeesByFullName(params.get("lecture"));
                List<RoomEntity> rooms = roomService.findRoomsByCapacity(Integer.parseInt(params.get("capacity")));
                ScheduleEntity originalSchedule = scheduleService.findScheduleById(Integer.parseInt(params.get("scheduleId")));
                String roomName = params.get("room");

                if (originalSchedule.getDateId().getDate().equals(aDaySlot.getDate())
                        && originalSchedule.getDateId().getSlotId().getSlotName().equals(aDaySlot.getSlotId().getSlotName())
                        && originalSchedule.getEmpId().equals(lectures.get(0))) {

                } else {
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
                                        ", ngày " + existingSchedule.getDateId().getDate() + ", giảng viên " + (existingSchedule.getEmpId() == null ? "" : existingSchedule.getEmpId().getFullName()) +
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

                        List<CourseStudentEntity> courseStudentEntities = courseStudentService.findCourseStudentByGroupNameAndCourse(originalSchedule.getGroupName(), originalSchedule.getCourseId());
                        List<StudentEntity> listOfStudent = new ArrayList<>();
                        for (CourseStudentEntity courseStudentEntity : courseStudentEntities) {
                            listOfStudent.add(courseStudentEntity.getStudentId());
                        }

                        for (StudentEntity aStudent : listOfStudent) {
                            HashSet<String> groupNameListOfStudent = new HashSet<>();

                            List<CourseStudentEntity> courseStudentEntitiesOfOneStudent = courseStudentService.findCourseStudentByStudent(aStudent);

                            for (CourseStudentEntity courseStudentEntity : courseStudentEntitiesOfOneStudent) {
                                groupNameListOfStudent.add(courseStudentEntity.getGroupName());
                            }

                            for (String groupName : groupNameListOfStudent) {
                                ScheduleEntity existingSchedule1 = scheduleService.findScheduleByDateSlotAndGroupName(aDaySlot, groupName);
                                if (existingSchedule1 != null) {
                                    if (!existingSchedule1.getId().toString().equals(params.get("scheduleId"))) {
                                        jsonObj.addProperty("fail", true);
                                        jsonObj.addProperty("message", "SV " + aStudent.getRollNumber() + " của slot hiện tại đã có lịch học vào " +
                                                "" + existingSchedule1.getDateId().getSlotId().getSlotName() +
                                                ", ngày " + existingSchedule1.getDateId().getDate() + ", giảng viên " +
                                                (existingSchedule1.getEmpId() == null ? "" : existingSchedule1.getEmpId().getFullName()) +
                                                ", môn " + existingSchedule1.getCourseId().getSubjectCode() +
                                                ", lớp " + groupName + ", phòng " + existingSchedule1.getRoomId().getName());
                                        return jsonObj;
                                    }
                                }
                            }

                        }

                    }
                }


                List<ScheduleEntity> sameScheduleList = new ArrayList<>();

                if (params.get("all").equals("true")) {
                    List<ScheduleEntity> tmpList = scheduleService.findScheduleByGroupnameAndCourseAndLecture(originalSchedule.getCourseId(), originalSchedule.getGroupName(), lectures.get(0));
                    for (ScheduleEntity aSchedule : tmpList) {
                        if (aSchedule.getDateId().getSlotId().getSlotName().equals(originalSchedule.getDateId().getSlotId().getSlotName())
                                && isWeekDayEqual(aSchedule.getDateId().getDate(), originalSchedule.getDateId().getDate())) {
                            sameScheduleList.add(aSchedule);
                        }
                    }
                    sameScheduleList.remove(originalSchedule);
                }


                RoomEntity selectedRoom = null;

                //find new room
                if (params.get("changeRoom").equals("true")) {
                    if (params.get("changeRoom").equals("true") || !originalSchedule.getDateId().getDate().equals(aDaySlot.getDate())
                            || !originalSchedule.getDateId().getSlotId().getSlotName().equals(aDaySlot.getSlotId().getSlotName())) {
                        if (rooms != null && rooms.size() > 0) {
                            for (RoomEntity aRoom : rooms) {
                                ScheduleEntity existingSchedule = scheduleService.findScheduleByDateSlotAndRoom(aDaySlot, aRoom);
                                if (existingSchedule == null) {
                                    if (originalSchedule.getCourseId().getSubjectCode().contains("VOV")) {
                                        if (aRoom.getName().contains("VOV")) {
                                            selectedRoom = aRoom;
                                            break;
                                        }
                                    }

                                    if (originalSchedule.getCourseId().getSubjectCode().contains("LAB")) {
                                        if (aRoom.getNote().toLowerCase().contains("thực hành")) {
                                            selectedRoom = aRoom;
                                            break;
                                        }
                                    }

                                    if (!originalSchedule.getCourseId().getSubjectCode().contains("LAB") && !originalSchedule.getCourseId().getSubjectCode().contains("VOV")) {
                                        if (!aRoom.getName().contains("VOV") && !aRoom.getNote().toLowerCase().contains("thực hành")) {
                                            selectedRoom = aRoom;
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        if (selectedRoom != null) {
                            originalSchedule.setRoomId(selectedRoom);
                        } else {
                            jsonObj.addProperty("fail", true);
                            jsonObj.addProperty("message", "Không có phòng trống vào " + aDaySlot.getSlotId().getSlotName() + ",ngày " + aDaySlot.getDate());
                            return jsonObj;
                        }
                    }
                } else {
                    if (!roomName.equals(originalSchedule.getRoomId().getName()) ||
                            !originalSchedule.getDateId().getDate().equals(aDaySlot.getDate())
                            || !originalSchedule.getDateId().getSlotId().getSlotName().equals(aDaySlot.getSlotId().getSlotName())) {
                        RoomEntity foundRoom = roomService.findRoomsByExactName(roomName);
                        //room exist
                        if (foundRoom != null) {
                            ScheduleEntity existingSchedule = scheduleService.findScheduleByDateSlotAndRoom(aDaySlot, foundRoom);
                            //have another schedule
                            if (existingSchedule != null) {
                                jsonObj.addProperty("fail", true);
                                jsonObj.addProperty("message", "Phòng này đã có lịch học vào " + existingSchedule.getDateId().getSlotId().getSlotName() +
                                        ", ngày " + existingSchedule.getDateId().getDate() + ", giảng viên " + (existingSchedule.getEmpId() == null ? "" : existingSchedule.getEmpId().getFullName()) +
                                        ", môn " + existingSchedule.getCourseId().getSubjectCode() +
                                        ", lớp " + existingSchedule.getGroupName());
                                return jsonObj;
                            } else {
                                originalSchedule.setRoomId(foundRoom);
                            }
                        } else {
                            jsonObj.addProperty("fail", true);
                            jsonObj.addProperty("message", "Không có phòng này tồn tại");
                            return jsonObj;
                        }
                    }
                }

                //only change room
                if (originalSchedule.getDateId().getDate().equals(aDaySlot.getDate())
                        && originalSchedule.getDateId().getSlotId().getSlotName().equals(aDaySlot.getSlotId().getSlotName())
                        && originalSchedule.getEmpId().equals(lectures.get(0))) {

                    scheduleService.updateSchedule(originalSchedule);

                    if (params.get("all").equals("true")) {
                        for (ScheduleEntity aSchedule : sameScheduleList) {
                            DaySlotEntity tmpDaySlot = daySlotService.findDaySlotByDateAndSlot(aSchedule.getDateId().getDate(), aSlot);
                            if (tmpDaySlot == null) {
                                tmpDaySlot = new DaySlotEntity();
                                tmpDaySlot.setSlotId(aSlot);
                                tmpDaySlot.setDate(aSchedule.getDateId().getDate());

                                daySlotService.createDateSlot(tmpDaySlot);
                            }
                            aSchedule.setDateId(tmpDaySlot);

                            if (lectures != null && lectures.size() > 0) {
                                aSchedule.setEmpId(lectures.get(0));
                            } else {
                                jsonObj.addProperty("fail", true);
                                jsonObj.addProperty("message", "Không có giảng viên này");
                                return jsonObj;
                            }


                            RoomEntity selectedRoom2 = null;
                            //find new room
                            if (params.get("changeRoom").equals("true")) {
                                if (rooms != null && rooms.size() > 0) {
                                    for (RoomEntity aRoom : rooms) {
                                        ScheduleEntity existingSchedule = scheduleService.findScheduleByDateSlotAndRoom(aSchedule.getDateId(), aRoom);
                                        if (existingSchedule == null) {
                                            if (originalSchedule.getCourseId().getSubjectCode().contains("VOV")) {
                                                if (aRoom.getName().contains("VOV")) {
                                                    selectedRoom2 = aRoom;
                                                    break;
                                                }
                                            }

                                            if (originalSchedule.getCourseId().getSubjectCode().contains("LAB")) {
                                                if (aRoom.getNote().toLowerCase().contains("thực hành")) {
                                                    selectedRoom2 = aRoom;
                                                    break;
                                                }
                                            }

                                            if (!originalSchedule.getCourseId().getSubjectCode().contains("LAB") && !originalSchedule.getCourseId().getSubjectCode().contains("VOV")) {
                                                if (!aRoom.getName().contains("VOV") && !aRoom.getNote().toLowerCase().contains("thực hành")) {
                                                    selectedRoom2 = aRoom;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }

                                if (selectedRoom2 != null) {
                                    aSchedule.setRoomId(selectedRoom2);
                                } else {
                                    mess += "<div> Không có phòng trống vào " + aSchedule.getDateId().getSlotId().getSlotName() + ", ngày " + aSchedule.getDateId().getDate() + "</div><br/>";
                                    continue;
                                }
                            } else {
                                if (!roomName.equals(aSchedule.getRoomId().getName())) {
                                    RoomEntity foundRoom = roomService.findRoomsByExactName(roomName);
                                    //room exist
                                    if (foundRoom != null) {
                                        ScheduleEntity existingSchedule = scheduleService.findScheduleByDateSlotAndRoom(aSchedule.getDateId(), foundRoom);
                                        //have another schedule
                                        if (existingSchedule != null) {
                                            jsonObj.addProperty("fail", true);
                                            jsonObj.addProperty("message", "Phòng này đã có lịch học vào " + existingSchedule.getDateId().getSlotId().getSlotName() +
                                                    ", ngày " + existingSchedule.getDateId().getDate() + ", giảng viên " + (existingSchedule.getEmpId() == null ? "" : existingSchedule.getEmpId().getFullName()) +
                                                    ", môn " + existingSchedule.getCourseId().getSubjectCode() +
                                                    ", lớp " + existingSchedule.getGroupName());
                                            return jsonObj;
                                        } else {
                                            aSchedule.setRoomId(foundRoom);
                                        }
                                    } else {
                                        jsonObj.addProperty("fail", true);
                                        jsonObj.addProperty("message", "Không có phòng này tồn tại");
                                        return jsonObj;
                                    }
                                }
                            }


                            scheduleService.updateSchedule(aSchedule);
                        }
                    }
                } else {
                    String oldDateStr = originalSchedule.getDateId().getDate();
                    String newDateStr = aDaySlot.getDate();
                    Date oldDate = DateUtil.getDate(oldDateStr);
                    Date newDate = DateUtil.getDate(newDateStr);
                    Calendar cOldDate = Calendar.getInstance();
                    cOldDate.setTime(oldDate);
                    Calendar cNewDate = Calendar.getInstance();
                    cNewDate.setTime(newDate);

                    long diff = oldDate.getTime() - newDate.getTime();
                    float days = (diff / (1000 * 60 * 60 * 24));
                    days = days * -1;

                    //change status of current record
                    originalSchedule.setActive(false);
                    scheduleService.updateSchedule(originalSchedule);

                    //add new record
                    originalSchedule.setDateId(aDaySlot);

                    if (lectures != null && lectures.size() > 0) {
                        originalSchedule.setEmpId(lectures.get(0));
                    } else {
                        jsonObj.addProperty("fail", true);
                        jsonObj.addProperty("message", "Không có giảng viên này");
                        return jsonObj;
                    }
                    originalSchedule.setParentScheduleId(originalSchedule.getId());
                    originalSchedule.setId(0);
                    originalSchedule.setActive(true);
                    scheduleService.createSchedule(originalSchedule);

                    if (params.get("all").equals("true")) {
                        for (ScheduleEntity aSchedule : sameScheduleList) {
                            //change status of current record
                            aSchedule.setActive(false);
                            scheduleService.updateSchedule(aSchedule);

                            Date date1 = getDate(aSchedule.getDateId().getDate());
                            Calendar c1 = Calendar.getInstance();
                            c1.setTime(date1);
                            c1.add(Calendar.DATE, (int) days);
                            String tmpDate = DateUtil.formatDate(c1.getTime());

                            DaySlotEntity tmpDaySlot = daySlotService.findDaySlotByDateAndSlot(tmpDate, aSlot);
                            if (tmpDaySlot == null) {
                                tmpDaySlot = new DaySlotEntity();
                                tmpDaySlot.setSlotId(aSlot);
                                tmpDaySlot.setDate(tmpDate);
                                daySlotService.createDateSlot(tmpDaySlot);
                            }

                            aSchedule.setDateId(tmpDaySlot);

                            if (lectures != null && lectures.size() > 0) {
                                aSchedule.setEmpId(lectures.get(0));
                            } else {
                                jsonObj.addProperty("fail", true);
                                jsonObj.addProperty("message", "Không có giảng viên này");
                                return jsonObj;
                            }

                            aSchedule.setRoomId(originalSchedule.getRoomId());

                            aSchedule.setParentScheduleId(aSchedule.getId());
                            aSchedule.setId(0);
                            aSchedule.setActive(true);

                            ScheduleEntity newSchedule = new ScheduleEntity();
                            newSchedule.setRoomId(aSchedule.getRoomId());
                            newSchedule.setEmpId(aSchedule.getEmpId());
                            newSchedule.setDateId(aSchedule.getDateId());
                            newSchedule.setCourseId(aSchedule.getCourseId());
                            newSchedule.setGroupName(aSchedule.getGroupName());
                            newSchedule.setActive(true);
                            newSchedule.setParentScheduleId(aSchedule.getId());

                            scheduleService.createSchedule(newSchedule);
                        }
                    }

                }
                if (!mess.equals("")) {
                    jsonObj.addProperty("warning", true);
                    jsonObj.addProperty("message", mess);
                } else {
                    jsonObj.addProperty("success", true);

                }
                List<ScheduleEntity> scheduleEntities = new ArrayList<>();
                scheduleEntities.add(originalSchedule);

                List<StudentEntity> studentList = new ArrayList<>();
                List<CourseStudentEntity> courseStudentEntityList = courseStudentService.findCourseStudentByGroupNameAndCourse(originalSchedule.getGroupName(), originalSchedule.getCourseId());
                if (courseStudentEntityList != null) {
                    for (CourseStudentEntity courseStudentEntity : courseStudentEntityList) {
                        StudentEntity aStudent = courseStudentEntity.getStudentId();
                        studentList.add(aStudent);

                    }
                }

                for (StudentEntity aStudent : studentList) {
                    sendNotification("Your schedule has been changed", aStudent.getEmail().substring(0, aStudent.getEmail().indexOf("@")), scheduleEntities, androidPushNotificationsService, "edit");
                }

                Ultilities.sendNotification("Your schedule has been changed", originalSchedule.getEmpId().getEmailEDU().substring(0, originalSchedule.getEmpId().getEmailEDU().indexOf("@")), scheduleEntities, androidPushNotificationsService, "edit");

                if (params.get("all").equals("true")) {
                    for (StudentEntity aStudent : studentList) {
                        sendNotification("Your schedule has been changed", aStudent.getEmail().substring(0, aStudent.getEmail().indexOf("@")), sameScheduleList, androidPushNotificationsService, "edit");
                    }

                    Ultilities.sendNotification("Your schedule has been changed", originalSchedule.getEmpId().getEmailEDU().substring(0, originalSchedule.getEmpId().getEmailEDU().indexOf("@")), sameScheduleList, androidPushNotificationsService, "edit");

                }
            }


        } catch (Exception e) {
            Logger.writeLog(e);
            jsonObj.addProperty("fail", true);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

    @RequestMapping(value = "/syncFAPChangedSchedule")
    @ResponseBody
    public JsonObject SyncFAPChangedSchedule(@RequestParam Map<String, String> params) {
        Ultilities.logUserAction("SyncFAPChangedSchedule");
        JsonObject jsonObj = new JsonObject();
        try {
            syncChangedScheduleFapImpl();
            jsonObj.addProperty("success", true);

        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
            jsonObj.addProperty("fail", true);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

    public void syncChangedScheduleFapImpl() {
        try {
            EntityManagerFactory emf2 = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em2 = emf2.createEntityManager();

            //get latest date when sync from FAP
            String queryStr2 = "SELECT s FROM ScheduleEntity s WHERE (s.parentScheduleId IS NOT NULL) ORDER BY s.changedDate DESC";
            TypedQuery<ScheduleEntity> query2 = em2.createQuery(queryStr2, ScheduleEntity.class);
            query2.setMaxResults(1);
            List<ScheduleEntity> latestRecordByChangedDate = query2.getResultList();

            Date latestDate = null;

            if (latestRecordByChangedDate != null && latestRecordByChangedDate.size() > 0) {
                latestDate = latestRecordByChangedDate.get(0).getChangedDate();
            }

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("FapDB");
            EntityManager em = emf.createEntityManager();
            // Query danh sách lịch học thay đổi từ FAP
            String queryStr = "SELECT s FROM ChangedScheduleEntity s " + (latestDate == null ? "" : "WHERE (s.changedScheduleEntityPK.changedDate >= :date)") + " ORDER BY s.changedScheduleEntityPK.changedDate";
//            String queryStr = "SELECT s FROM ChangedScheduleEntity s " + (latestDate == null ? "" : "") + " ORDER BY s.changedScheduleEntityPK.changedDate";

            TypedQuery<ChangedScheduleEntity> query = em.createQuery(queryStr, ChangedScheduleEntity.class);
            List<ChangedScheduleEntity> scheduleList = latestDate == null ? query.getResultList() : query.setParameter("date", latestDate).getResultList();
//            List<ChangedScheduleEntity> scheduleList = query.getResultList();

            List<RealSemesterEntity> realSemesterEntityList = realSemesterService.getAllSemester();

            List<EmployeeEntity> employeeEntityList = employeeService.findAllEmployees();

            for (RealSemesterEntity aSemester : realSemesterEntityList) {
                if (aSemester.getStartDate() != null && aSemester.getEndDate() != null) {
                    Date startDate = getDate(aSemester.getStartDate());
                    Date endDate = getDate(aSemester.getEndDate());

                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTime(startDate);
                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTime(endDate);

                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    for (ChangedScheduleEntity changedSchedule : scheduleList) {
                        Date scheduleFromDate = format.parse(changedSchedule.getFromDate());
                        String formatedFromDate = formatDate(scheduleFromDate);
                        if (!(scheduleFromDate.before(startDate) || scheduleFromDate.after(endDate))) {
                            //find original course
                            CourseEntity course = courseService.findCourseBySemesterAndSubjectCode(aSemester.getSemester(), changedSchedule.getSubjectCode());

                            //find original lecture
                            EmployeeEntity emp = null;
                            for (EmployeeEntity aEmployee : employeeEntityList) {
                                if (aEmployee.getEmailFE().toLowerCase().substring(0, aEmployee.getEmailFE().indexOf("@")).equals(changedSchedule.getFromLecturer().toLowerCase())
                                        || aEmployee.getEmailEDU().toLowerCase().substring(0, aEmployee.getEmailEDU().indexOf("@")).equals(changedSchedule.getFromLecturer().toLowerCase())) {
                                    emp = aEmployee;
                                }
                            }

                            //find original slot
                            SlotEntity slot = slotService.findSlotsByName("Slot " + changedSchedule.getFromSlot()).get(0);

                            //find original DaySlot
                            DaySlotEntity daySlot = daySlotService.findDaySlotByDateAndSlot(formatedFromDate, slot);

                            if (daySlot == null) {
                                DaySlotEntity tmpDaySlot = new DaySlotEntity();
                                tmpDaySlot.setDate(formatedFromDate);
                                tmpDaySlot.setSlotId(slot);
                                daySlot = daySlotService.createDateSlot(tmpDaySlot);
                            }

                            //find original room
                            RoomEntity room = roomService.findRoomsByExactName(changedSchedule.getFromRoomNo());

                            //find original schedule with isActive
                            ScheduleEntity scheduleEntity = scheduleService.findScheduleByDateSlotAndLectureAndRoomAndCourse(daySlot, emp, room, course);

                            if (scheduleEntity != null) {
                                scheduleEntity.setActive(false);
                                scheduleEntity.setChangedDate(changedSchedule.getChangedScheduleEntityPK().getChangedDate());
                                scheduleService.updateSchedule(scheduleEntity);
                            } else {
                                scheduleEntity = scheduleService.findScheduleByDateSlotAndLectureAndRoomAndCourseDontCareIsActive(daySlot, emp, room, course);

                                if (scheduleEntity == null) {
                                    scheduleEntity = scheduleService.findScheduleByDateSlotAndLectureAndCourseDontCareIsActive(daySlot, emp, course);
                                    if (scheduleEntity == null) {
                                        scheduleEntity = new ScheduleEntity();
                                        scheduleEntity.setCourseId(course);
                                        scheduleEntity.setRoomId(room);
                                        scheduleEntity.setDateId(daySlot);
                                        scheduleEntity.setEmpId(emp);
                                        scheduleEntity.setGroupName(changedSchedule.getClassName());
                                        scheduleEntity.setParentScheduleId(null);
                                        scheduleEntity.setActive(false);
                                        scheduleEntity.setId(0);
                                        scheduleEntity.setChangedDate(changedSchedule.getChangedScheduleEntityPK().getChangedDate());
                                        scheduleEntity = scheduleService.createSchedule(scheduleEntity);
                                    } else {
                                        scheduleEntity.setActive(false);
                                        scheduleEntity.setChangedDate(changedSchedule.getChangedScheduleEntityPK().getChangedDate());
                                        scheduleEntity.setRoomId(room);
                                        scheduleService.updateSchedule(scheduleEntity);
                                    }

                                }


                            }

                            EmployeeEntity emp2 = null;
                            //find changed lecture
                            if (changedSchedule.getToLecturer() != null) {
                                for (EmployeeEntity aEmployee : employeeEntityList) {
                                    if (aEmployee.getEmailFE().toLowerCase().substring(0, aEmployee.getEmailFE().indexOf("@")).equals(changedSchedule.getToLecturer().toLowerCase())
                                            || aEmployee.getEmailEDU().toLowerCase().substring(0, aEmployee.getEmailEDU().indexOf("@")).equals(changedSchedule.getToLecturer().toLowerCase())) {
                                        emp2 = aEmployee;
                                    }
                                }
                            } else {
                                //dont change lecture
                                emp2 = emp;
                            }

                            SlotEntity slot2 = null;
                            //find changed slot
                            if (changedSchedule.getToSlot() != null) {
                                slot2 = slotService.findSlotsByName("Slot " + changedSchedule.getToSlot()).get(0);
                            } else {
                                //dont change slot
                                slot2 = slot;
                            }

                            DaySlotEntity daySlot2 = null;
                            //find changed DaySlot
                            if (changedSchedule.getToDate() != null) {
                                Date scheduleToDate = format.parse(changedSchedule.getToDate());
                                String formatedToDate = formatDate(scheduleToDate);
                                daySlot2 = daySlotService.findDaySlotByDateAndSlot(formatedToDate, slot2);
                                if (daySlot2 == null) {
                                    DaySlotEntity tmpDaySlot = new DaySlotEntity();
                                    tmpDaySlot.setDate(formatedToDate);
                                    tmpDaySlot.setSlotId(slot2);
                                    daySlot2 = daySlotService.createDateSlot(tmpDaySlot);
                                }
                            } else {
                                //dont change date
                                daySlot2 = daySlotService.findDaySlotByDateAndSlot(formatedFromDate, slot2);
                                if (daySlot2 == null) {
                                    DaySlotEntity tmpDaySlot = new DaySlotEntity();
                                    tmpDaySlot.setDate(formatedFromDate);
                                    tmpDaySlot.setSlotId(slot2);
                                    daySlot2 = daySlotService.createDateSlot(tmpDaySlot);
                                }
                            }

                            RoomEntity room2 = null;
                            //find changed room
                            if (changedSchedule.getToRoomNo() != null) {
                                room2 = roomService.findRoomsByExactName(changedSchedule.getToRoomNo());
                            } else {
                                //dont change room
                                room2 = room;
                            }

                            if (changedSchedule.getReason().equals("_ChangeRoom")) {
                                scheduleEntity.setActive(true);
                                scheduleEntity.setRoomId(room2);
                                scheduleEntity.setChangedDate(changedSchedule.getChangedScheduleEntityPK().getChangedDate());
                                scheduleService.updateSchedule(scheduleEntity);
                            } else {
                                //find changed schedule exist or not
                                ScheduleEntity scheduleEntity2 = scheduleService.findScheduleByDateSlotAndLectureAndRoomAndCourse(daySlot2, emp2, room2, course);
                                if (scheduleEntity2 != null) {
                                    //changed schedule already exist in DB, only run this code once when import DB first time
                                    if (scheduleEntity2.getParentScheduleId() == null) {
                                        scheduleEntity2.setParentScheduleId(scheduleEntity.getId());
                                        scheduleEntity2.setChangedDate(changedSchedule.getChangedScheduleEntityPK().getChangedDate());
                                        scheduleService.updateSchedule(scheduleEntity2);
                                    }
                                } else {
                                    //change others and not exist
                                    scheduleEntity2 = new ScheduleEntity();
                                    scheduleEntity2.setCourseId(course);
                                    scheduleEntity2.setRoomId(room2);
                                    scheduleEntity2.setDateId(daySlot2);
                                    scheduleEntity2.setEmpId(emp2);
                                    scheduleEntity2.setGroupName(scheduleEntity.getGroupName());
                                    scheduleEntity2.setParentScheduleId(scheduleEntity.getId());
                                    scheduleEntity2.setActive(true);
                                    scheduleEntity2.setId(0);
                                    scheduleEntity2.setChangedDate(changedSchedule.getChangedScheduleEntityPK().getChangedDate());
                                    scheduleService.createSchedule(scheduleEntity2);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/countAttendance")
    public ModelAndView countAttendancePage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());
        ModelAndView view = new ModelAndView("CountAttendance");
        view.addObject("title", "Theo dõi điểm danh theo lớp");

        List<SubjectEntity> subjects = subjectService.getAllSubjects();
        view.addObject("subjects", subjects);


        return view;
    }

    @RequestMapping(value = "/countAttendanceOfClass")
    @ResponseBody
    public JsonObject CountAttendanceOfClass(@RequestParam Map<String, String> params) {
        Ultilities.logUserAction("CountAttendanceOfClass");
        JsonObject jsonObj = new JsonObject();
        List<List<String>> result = new ArrayList<>();

        try {
            EntityManagerFactory emf2 = Persistence.createEntityManagerFactory("FapDB");
            EntityManager em2 = emf2.createEntityManager();

            String subjectCode = "";
            if (!params.get("subject").equals("") && !params.get("subject").equals("-1")) {
                subjectCode = params.get("subject");
            }

            String groupName = "";
            if (!params.get("groupName").equals("") && !params.get("groupName").equals("-1")) {
                groupName = params.get("groupName");
            }

            RealSemesterEntity currentSemester = Global.getCurrentSemester();
            Date startDate = DateUtil.getDate(currentSemester.getStartDate());
            Date endDate = DateUtil.getDate(currentSemester.getEndDate());
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String startDateStr = format.format(startDate);
            String endDateStr = format.format(endDate);

            String startDateStr2 = params.get("startDate");
            String endDateStr2 = params.get("endDate");

            DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");

            if (!startDateStr2.equals("")) {
                Date tmpStart = DateUtil.getDate(startDateStr2);
                startDateStr2 = format2.format(tmpStart);

                Date tmpEnd = DateUtil.getDate(endDateStr2);
                endDateStr2 = format2.format(tmpEnd);
            }

            List<ScheduleEntity> scheduleLists = scheduleService.findScheduleBySubjectCodeAndGroupNameBeforeNowTime(subjectCode, groupName);

            DateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
            List<ScheduleEntity> removeList = new ArrayList<>();
            for (ScheduleEntity aSchedule : scheduleLists) {
                Date aDate = formatDate.parse(aSchedule.getDateId().getDate());
                if (aDate.before(format2.parse(startDateStr2)) || aDate.after(format2.parse(endDateStr2))) {
                    removeList.add(aSchedule);
                }
            }
            scheduleLists.removeAll(removeList);


            Map<String, Integer> resultMap = new HashMap<>();
            Map<String, String> totalSlotMap = new HashMap<>();

            //initialize the result map
            for (ScheduleEntity aSchedule : scheduleLists) {
                resultMap.put(aSchedule.getGroupName(), 0);
                totalSlotMap.put(aSchedule.getGroupName(), "");
            }


            Date now = new Date();
            String nowStr = format2.format(now);

            Set<String> groupNameSet = new HashSet();

            for (ScheduleEntity aSchedule : scheduleLists) {
                groupNameSet.add(aSchedule.getGroupName());
            }
            for (String aGroupName : groupNameSet) {
                //get subject id from FAP DB
                String queryStringForSubject = "SELECT * FROM Subjects WHERE SubjectCode LIKE '" + subjectCode + "'";
                Query queryForSubject = em2.createNativeQuery(queryStringForSubject, Subjects.class);
                List<Subjects> subjectsList = queryForSubject.getResultList();
                int subjectId = subjectsList != null && subjectsList.size() > 0 ? subjectsList.get(0).getSubjectID() : -1;

                if (subjectId != -1) {
                    //get course id from FAP DB
                    String queryStringForCourse = "SELECT * FROM Courses WHERE GroupName LIKE '" + aGroupName + "' AND SubjectId = " + subjectId + "" +
                            " AND StartDate >= '" + startDateStr + "' AND  StartDate < '" + endDateStr + "'";
                    Query queryForCourse = em2.createNativeQuery(queryStringForCourse, CoursesEntity.class);
                    List<CoursesEntity> courseList = queryForCourse.getResultList();
                    int courseId = courseList != null && courseList.size() > 0 ? courseList.get(0).getCourseID() : -1;
                    if (courseList != null && courseList.size() > 0) {
                        totalSlotMap.put(aGroupName, courseList.get(0).getNumberOfSlots() + "");
                    }

                    if (courseId != -1) {
                        //get schedule of course from FAP DB
                        String queryStringForSchedule = "SELECT * FROM Schedules s WHERE s.CourseID = " + courseId + " " +
                                " AND s.Date <= '" + nowStr + "'";
                        Query queryForSchedule = em2.createNativeQuery(queryStringForSchedule, SchedulesEntity.class);
                        List<SchedulesEntity> fapScheduleList = queryForSchedule.getResultList();

                        for (SchedulesEntity theSchedule : fapScheduleList) {

                            int scheduleId = theSchedule.getScheduleID();
//                            if (scheduleId != -1) {
                            //get schedule of course from FAP DB
                            String queryStringForAttendance = "SELECT 1 FROM Attendances s JOIN Schedules a ON a.ScheduleID=s.ScheduleID WHERE s.ScheduleID = " + scheduleId + " " +
                                    " AND a.Date >= '" + (startDateStr2.equals("") ? startDateStr : startDateStr2) + "' AND  a.Date < '" + (endDateStr2.equals("") ? endDateStr : endDateStr2) + "' AND s.Status = 1";
                            Query queryForAttendances = em2.createNativeQuery(queryStringForAttendance);
                            List fapAttendanceList = queryForAttendances.getResultList();

                            if (fapAttendanceList != null && fapAttendanceList.size() > 0) {
                                int countAttendance = resultMap.get(aGroupName);
                                ++countAttendance;
                                resultMap.put(aGroupName, countAttendance);
                            }
//                            }
                        }


                    }
                }

            }


            for (String key : resultMap.keySet()) {
                List<String> dataList = new ArrayList<String>();

                dataList.add(key);
                dataList.add(resultMap.get(key) + "/" + totalSlotMap.get(key));

                result.add(dataList);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        Gson gson = new Gson();
        JsonArray array = (JsonArray) gson.toJsonTree(result);

        jsonObj.add("aaData", array);

        return jsonObj;
    }


    @RequestMapping(value = "/getGroupNameByLecture")
    @ResponseBody
    public JsonObject getGroupNameByLecture(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        Set<String> groupNameSet = new HashSet<>();
        try {
            String empEmailEDU = params.get("lectureEmailEDU");

            if (empEmailEDU != null && !empEmailEDU.equals("")) {
                EmployeeEntity emp = employeeService.findEmployeeByEmail(empEmailEDU);
                if (emp != null) {
                    List<ScheduleEntity> scheduleEntityList = scheduleService.findScheduleByLecture(emp.getId());

                    for (ScheduleEntity aSchedule : scheduleEntityList) {
                        groupNameSet.add(aSchedule.getGroupName());
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        JsonArray array = (JsonArray) gson.toJsonTree(groupNameSet);

        jsonObj.add("groupNameList", array);
        return jsonObj;
    }


    @RequestMapping(value = "/getGroupNameBySubject")
    @ResponseBody
    public JsonObject getGroupNameBySubject(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        Set<String> groupNameSet = new HashSet<>();
        try {
            String subjectCode = params.get("subjectCode");

            if (subjectCode != null && !subjectCode.equals("")) {
                List<ScheduleEntity> scheduleEntityList = scheduleService.findScheduleBySubjectCodeAndGroupNameBeforeNowTime(subjectCode, "");

                for (ScheduleEntity aSchedule : scheduleEntityList) {
                    groupNameSet.add(aSchedule.getGroupName());
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        JsonArray array = (JsonArray) gson.toJsonTree(groupNameSet);

        jsonObj.add("groupNameList", array);
        return jsonObj;
    }
}


