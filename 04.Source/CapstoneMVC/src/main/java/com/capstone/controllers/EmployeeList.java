package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.entities.fapEntities.SchedulesEntity;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.capstone.services.DateUtil.formatDate;
import static com.capstone.services.DateUtil.getDate;

@Controller
public class EmployeeList {
    IRoomService roomService = new RoomServiceImpl();

    IScheduleService scheduleService = new ScheduleServiceImpl();

    ISlotService slotService = new SlotServiceImpl();

    IDaySlotService daySlotService = new DaySlotServiceImpl();

    IEmployeeService employeeService = new EmployeeServiceImpl();

    IRealSemesterService realSemesterService = new RealSemesterServiceImpl();

    ISubjectService subjectService = new SubjectServiceImpl();

    IEmployeeCompetenceService employeeCompetenceService = new EmployeeCompetenceServiceImpl();


    @RequestMapping("/employeeList")
    public ModelAndView EmployeeListAll(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());
        ModelAndView view = new ModelAndView("EmployeeList");
        view.addObject("title", "Danh sách giảng viên");

        return view;
    }

    @RequestMapping("/employeeList/{employeeId}")
    public ModelAndView EmployeeInfo(@PathVariable("employeeId") int employeeId, HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize2(request, "/employeeList")) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());
        ModelAndView view = new ModelAndView("EmployeeInfo");
        view.addObject("title", "Thông tin giảng viên");
        List<SubjectEntity> subjects = subjectService.getAllSubjects();
        view.addObject("subjects", subjects);

        List<RoomEntity> rooms = roomService.findAllRooms();
        view.addObject("rooms", rooms);

        List<EmployeeEntity> emps = employeeService.findAllEmployees();
        view.addObject("employees", emps);

        List<SlotEntity> slots = slotService.findAllSlots();
        view.addObject("slots", slots);

        List<RealSemesterEntity> semesters = realSemesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);

        view.addObject("semesters", semesters);

        EmployeeEntity emp = employeeService.findEmployeeById(employeeId);

        view.addObject("employee", emp);

        Set listCapacity = new HashSet();
        for (RoomEntity room : rooms) {
            listCapacity.add(room.getCapacity());
        }
        view.addObject("capacity", listCapacity);

        return view;
    }

    @RequestMapping("/requestLecture")
    public ModelAndView RequestLecturePage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());
        ModelAndView view = new ModelAndView("RequestLecture");
        view.addObject("title", "Tìm GV thay thế");

        List<SubjectEntity> subjects = subjectService.getAllSubjects();
        view.addObject("subjects", subjects);

        List<SlotEntity> slots = slotService.findAllSlots();
        view.addObject("slots", slots);

        List<RoomEntity> rooms = roomService.findAllRooms();
        view.addObject("rooms", rooms);

        List<EmployeeEntity> emps = employeeService.findAllEmployees();
        view.addObject("employees", emps);

        return view;
    }

    @RequestMapping(value = "/employee/edit/{employeeId}")
    @ResponseBody
    public JsonObject EditEmployee(@PathVariable("employeeId") int employeeId, @RequestParam Map<String, String> params) {

        //logging user action
        Ultilities.logUserAction("Edit employee " + employeeId);
        JsonObject jsonObj = new JsonObject();

        try {
            EmployeeEntity emp = employeeService.findEmployeeById(employeeId);
            String position = params.get("position");
            String emailPersonal = params.get("emailPersonal");
            String emailFE = params.get("emailFE");
            String emailEDU = params.get("emailEDU");
            String phone = params.get("phone");
            String address = params.get("address");
            String contract = params.get("contract");
            String code = params.get("code");

            if (position != null && !position.equals("")) {
                emp.setPosition(position);
            }

            if (emailPersonal != null && !emailPersonal.equals("")) {
                emp.setPersonalEmail(emailPersonal);
            }

            if (emailFE != null && !emailFE.equals("")) {
                emp.setEmailFE(emailFE);
            }

            if (emailEDU != null && !emailEDU.equals("")) {
                emp.setEmailEDU(emailEDU);
            }

            if (phone != null && !phone.equals("")) {
                emp.setPhone(phone);
            }

            if (address != null && !address.equals("")) {
                emp.setAddress(position);
            }

            if (contract != null && !contract.equals("")) {
                emp.setContract(position);
            }

            if (code != null && !code.equals("")) {
                emp.setCode(code);
            }

            employeeService.updateEmployee(emp);
            jsonObj.addProperty("success", true);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }


    @RequestMapping("/employeeFreeSchedule")
    public ModelAndView EmployeeFreeSchedule(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());
        ModelAndView view = new ModelAndView("EmployeeFreeSchedule");
        view.addObject("title", "Thống kê lịch trống của GV");

        List<EmployeeEntity> emps = employeeService.findAllEmployees();
        view.addObject("employees", emps);

        List<SubjectEntity> subjects = subjectService.getAllSubjects();
        view.addObject("subjects", subjects);

        List<RoomEntity> rooms = roomService.findAllRooms();
        view.addObject("rooms", rooms);

        Set listCapacity = new HashSet();
        for (RoomEntity room : rooms) {
            listCapacity.add(room.getCapacity());
        }
        view.addObject("capacity", listCapacity);

        List<SlotEntity> slots = slotService.findAllSlots();
        view.addObject("slots", slots);

        List<RealSemesterEntity> semesters = realSemesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);

        view.addObject("semesters", semesters);

        return view;
    }

    @RequestMapping(value = "/employeeFreeSchedule/get")
    @ResponseBody
    public JsonObject LoadEmployeeFreeScheduleAll(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        List<List<String>> result = LoadEmployeeFreeScheduleAllImpl(params);

        Integer lectureId = null;
        if (!params.get("lecture").equals("") && !params.get("lecture").equals("-1")) {
            lectureId = Integer.parseInt(params.get("lecture"));
        }

        jsonObj.addProperty("employeeCompetence", findEmployeCompetence(lectureId));

        Gson gson = new Gson();
        JsonArray array = (JsonArray) gson.toJsonTree(result);
        jsonObj.add("aaData", array);


        return jsonObj;
    }

    public String findEmployeCompetence(Integer lectureId) {
        String empCompetenceStr = "";
        List<EmpCompetenceEntity> empCompetenceEntities = employeeCompetenceService.findEmployeeCompetencesByEmployee(lectureId);
        if (empCompetenceEntities != null && empCompetenceEntities.size() > 0) {
            for (EmpCompetenceEntity empComp : empCompetenceEntities) {
                empCompetenceStr += empComp.getSubjectId().getId() + ", ";
            }

            empCompetenceStr = empCompetenceStr.substring(0, empCompetenceStr.lastIndexOf(", ") - 1);
        } else {
            empCompetenceStr = "Chưa có dữ liệu";
        }
        return empCompetenceStr;
    }

    public List<List<String>> LoadEmployeeFreeScheduleAllImpl(@RequestParam Map<String, String> params) {
        List<List<String>> result = new ArrayList<>();

        try {
            Integer lectureId = null;
            String startDate = params.get("startDate");
            String endDate = params.get("endDate");

            Map<Date, List<String>> freeDaySlot = new TreeMap<>();

            if (!params.get("lecture").equals("") && !params.get("lecture").equals("-1")) {
                lectureId = Integer.parseInt(params.get("lecture"));
            }


            List<ScheduleEntity> scheduleList = scheduleService.findScheduleByLecture(lectureId);
            if (scheduleList != null) {
                List<String> slotNameList = new ArrayList<>();

                List<SlotEntity> slotList = slotService.findAllSlots();

                for (SlotEntity aSlot : slotList) {
                    slotNameList.add(aSlot.getSlotName());
                }

                //get all schedule in date range
                if (!startDate.equals(endDate)) {
                    List<ScheduleEntity> removeList = new ArrayList<>();
                    for (ScheduleEntity aSchedule : scheduleList) {
                        Date aDate = getDate(aSchedule.getDateId().getDate());
                        if (aDate.before(getDate(startDate)) || aDate.after(getDate(endDate))) {
                            removeList.add(aSchedule);
                        }
                    }
                    scheduleList.removeAll(removeList);

                    List<String> allDates = new ArrayList();
                    Date date1 = getDate(startDate);
                    Date date2 = getDate(endDate);

                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(date1);
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(date2);
                    while (!c2.before(c1)) {
                        allDates.add(formatDate(c1.getTime()));
                        c1.add(Calendar.DATE, 1);
                    }

                    for (ScheduleEntity aSchedule : scheduleList) {
                        allDates.remove(aSchedule.getDateId().getDate());
                    }

                    for (String aDate : allDates) {
                        List<String> tmpSlotList = new ArrayList<>();
                        tmpSlotList.add("Trống slot cả ngày");
                        freeDaySlot.put(getDate(aDate), tmpSlotList);

                    }

                    //get all remaining free slot of a employee schedule in a date
                    for (ScheduleEntity aSchedule : scheduleList) {
                        List<String> slotOfDayList = freeDaySlot.get(getDate(aSchedule.getDateId().getDate()));
                        if (slotOfDayList == null) {
                            slotOfDayList = new ArrayList<>(slotNameList);
                        }
                        slotOfDayList.remove(aSchedule.getDateId().getSlotId().getSlotName());
                        freeDaySlot.put(getDate(aSchedule.getDateId().getDate()), slotOfDayList);
                    }


                    for (Date key : freeDaySlot.keySet()) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(key);
                        if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                            String totalSlot = "";
                            for (String aSlot : freeDaySlot.get(key)) {
                                totalSlot += aSlot + ", ";
                            }

                            List<String> dataList = new ArrayList<String>();

                            switch (cal.get(Calendar.DAY_OF_WEEK)) {
                                case Calendar.SUNDAY:
                                    dataList.add("Chủ nhật");
                                    break;
                                case Calendar.MONDAY:
                                    dataList.add("Thứ 2");
                                    break;
                                case Calendar.TUESDAY:
                                    dataList.add("Thứ 3");
                                    break;
                                case Calendar.WEDNESDAY:
                                    dataList.add("Thứ 4");
                                    break;
                                case Calendar.THURSDAY:
                                    dataList.add("Thứ 5");
                                    break;
                                case Calendar.FRIDAY:
                                    dataList.add("Thứ 6");
                                    break;
                                case Calendar.SATURDAY:
                                    dataList.add("Thứ 7");
                                    break;
                                default:
                                    dataList.add("");
                                    break;
                            }
                            dataList.add(formatDate(key));
                            dataList.add(totalSlot.substring(0, totalSlot.lastIndexOf(", ")));
                            result.add(dataList);
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

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

    @RequestMapping(value = "/getScheduleEmployeeInfo", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject getScheduleEmployeeInfoByEmail(@RequestBody String body) {
        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(body);
        String email = obj.get("email").getAsString();
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            String queryStr = "SELECT s FROM EmployeeEntity s" +
                    " WHERE s.emailEDU LIKE :email OR s.emailFE LIKE :email";
            Query query = em.createQuery(queryStr);
            query.setParameter("email", "%" + email + "%");

            EmployeeEntity emp = (EmployeeEntity) query.getSingleResult();

            Gson gson = new Gson();
            obj = new JsonObject();

            String queryStr2 = "SELECT s FROM ScheduleEntity s" +
                    " WHERE s.empId.id = :id";
            Query query2 = em.createQuery(queryStr2);
            query2.setParameter("id", emp.getId());
            List<ScheduleEntity> scheduleList = query2.getResultList();

            List<ScheduleModel> scheduleModelList = new ArrayList<>();
            for (ScheduleEntity schedule : scheduleList) {
                ScheduleModel model = new ScheduleModel();
                model.setCourseName(schedule.getCourseId().getSubjectCode());
                model.setDate(schedule.getDateId().getDate());
                model.setRoom(schedule.getRoomId().getName());
                model.setSlot(schedule.getDateId().getSlotId().getSlotName());
                model.setStartTime(schedule.getDateId().getSlotId().getStartTime());
                model.setEndTime(schedule.getDateId().getSlotId().getEndTime());
                model.setLecture(emp.getFullName());
                scheduleModelList.add(model);
            }

            MobileUserModel user = new MobileUserModel();
            user.setCode(emp.getCode());
            user.setId(emp.getId());
            user.setName(emp.getFullName());
            user.setEmailEDU(emp.getEmailEDU());
            user.setPosition(emp.getPosition());

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


    @RequestMapping(value = "/requestLecture/get")
    @ResponseBody
    public JsonObject RequestLecture(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        List<List<String>> result = new ArrayList<>();
//        List<List<String>> empResult = new ArrayList<>();
        List<EmployeeEntity> removeEmployees = new ArrayList<>();
//        List<EmployeeEntity> fromLecture = new ArrayList<>();

        try {
            String subjectCode = "";
            String slotName = "";
            String startDate = params.get("startDate");

            List<ScheduleEntity> scheduleList = new ArrayList<>();

            if (!params.get("slot").equals("") && !params.get("slot").equals("-1")) {
                slotName = params.get("slot");
            }

            if (!params.get("subject").equals("") && !params.get("subject").equals("-1")) {
                subjectCode = params.get("subject");
            }

            if (!slotName.equals("") && !subjectCode.equals("")) {
                List<SlotEntity> slotEntities = slotService.findSlotsByName(slotName);
                if (slotEntities != null && slotEntities.size() > 0) {
                    //find all schedule in the selected time
                    DaySlotEntity aDaySlot = daySlotService.findDaySlotByDateAndSlot(startDate, slotEntities.get(0));
                    List<ScheduleEntity> schedules = scheduleService.findScheduleByDateSlot(aDaySlot);
                    if (schedules != null) {
                        scheduleList.addAll(schedules);
                    }
                }

                //get all lecture in db
                List<EmployeeEntity> employeeEntities = employeeService.findAllEmployees();

                //if lecture have schedule in selected time then all that lecture to remove list
                for (EmployeeEntity emp : employeeEntities) {
                    for (ScheduleEntity aSchedule : scheduleList) {
                        if (aSchedule.getEmpId() != null) {
                            if (aSchedule.getEmpId().getFullName().equals(emp.getFullName())) {
                                removeEmployees.add(emp);
                            }
                        }
                    }
                }

                //remove lecture in remove list
                employeeEntities.removeAll(removeEmployees);

                List<EmployeeEntity> selectedEmployees = new ArrayList<>();

                for (EmployeeEntity emp : employeeEntities) {
                    List<EmpCompetenceEntity> empCompList = employeeCompetenceService.findEmployeeCompetencesByEmployee(emp.getId());
                    for (EmpCompetenceEntity empComp : empCompList) {
                        if (empComp.getSubjectId().getId().equals(subjectCode)) {
                            selectedEmployees.add(empComp.getEmployeeId());
                        }
                    }
                }

                for (EmployeeEntity emp : selectedEmployees) {
                    List<String> dataList = new ArrayList<String>();

                    dataList.add(emp.getId() + "");
                    if (emp.getFullName() != null) {
                        dataList.add(emp.getFullName());
                    } else {
                        dataList.add("");
                    }

                    if (emp.getPhone() != null) {
                        dataList.add(emp.getPhone());
                    } else {
                        dataList.add("");
                    }

                    if (emp.getEmailEDU() != null) {
                        dataList.add(emp.getEmailEDU());
                    } else {
                        dataList.add("");
                    }

                    result.add(dataList);
                }

//                for (EmployeeEntity emp : fromLecture) {
//                    List<String> dataList = new ArrayList<String>();
//
//                    dataList.add(emp.getId() + "");
//
//                    if (emp.getEmailEDU() != null) {
//                        String shortName = emp.getEmailEDU().substring(0, emp.getEmailEDU().indexOf("@"));
//                        dataList.add(shortName + " - " + emp.getFullName());
//                    } else {
//                        dataList.add("");
//                    }
//
//                    empresult.add(dataList);
//
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
//        JsonArray fromLectureJson = (JsonArray) gson.toJsonTree(empresult);
//        jsonObj.add("fromLecture", fromLectureJson);

        JsonArray array = (JsonArray) gson.toJsonTree(result);

        jsonObj.add("aaData", array);
        return jsonObj;
    }


    @RequestMapping(value = "/getLectureByDateSlot")
    @ResponseBody
    public JsonObject getLectureByDateSlot(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        List<List<String>> empResult = new ArrayList<>();
        HashSet<EmployeeEntity> fromLecture = new HashSet<>();
        Set<String> freeRoomList = new HashSet<>();
        Set<String> removeRoomList = new HashSet<>();
        try {
            String subjectCode = "";
            String slotName = "";
            String startDate = params.get("startDate");

            String slotWillTeach = params.get("slotWillTeach");
            String dayWillTeach = params.get("dayWillTeach");
            List<SlotEntity> slotWillTeachEntities = slotService.findSlotsByName(slotWillTeach);
            DaySlotEntity aDaySlotWillTeach = daySlotService.findDaySlotByDateAndSlot(dayWillTeach, slotWillTeachEntities.get(0));
            List<ScheduleEntity> schedulesInWillTeachDaySlot = scheduleService.findScheduleByDateSlot(aDaySlotWillTeach);
            for (ScheduleEntity aSchedule : schedulesInWillTeachDaySlot) {
                removeRoomList.add(aSchedule.getRoomId().getName());
            }

            List<ScheduleEntity> scheduleList = new ArrayList<>();

            if (!params.get("slot").equals("") && !params.get("slot").equals("-1")) {
                slotName = params.get("slot");
            }

            if (!params.get("subject").equals("") && !params.get("subject").equals("-1")) {
                subjectCode = params.get("subject");
            }

            if (!slotName.equals("") && !subjectCode.equals("")) {
                List<SlotEntity> slotEntities = slotService.findSlotsByName(slotName);
                if (slotEntities != null && slotEntities.size() > 0) {
                    //find all schedule in the selected time
                    DaySlotEntity aDaySlot = daySlotService.findDaySlotByDateAndSlot(startDate, slotEntities.get(0));
                    List<ScheduleEntity> schedules = scheduleService.findScheduleByDateSlot(aDaySlot);
                    if (schedules != null) {
                        scheduleList.addAll(schedules);
                    }
                }

                //if lecture have schedule in selected time then all that lecture to remove list
                for (ScheduleEntity aSchedule : scheduleList) {
                    if (aSchedule.getEmpId() != null) {
                        if (aSchedule.getCourseId().getSubjectCode().equals(subjectCode)) {
                            fromLecture.add(aSchedule.getEmpId());
                        }
                    }
                }


                for (EmployeeEntity emp : fromLecture) {
                    List<String> dataList = new ArrayList<String>();

                    dataList.add(emp.getId() + "");

                    if (emp.getEmailEDU() != null) {
                        String shortName = emp.getEmailEDU().substring(0, emp.getEmailEDU().indexOf("@"));
                        dataList.add(shortName + " - " + emp.getFullName());
                    } else {
                        dataList.add("");
                    }
                    empResult.add(dataList);
                }

                List<RoomEntity> allRooms = roomService.findAllRooms();
                for (RoomEntity aRoom : allRooms) {
                    freeRoomList.add(aRoom.getName());
                }
                //get rooms not in use by removing rooms in use in all room list
                freeRoomList.removeAll(removeRoomList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        JsonArray fromLectureJson = (JsonArray) gson.toJsonTree(empResult);
        jsonObj.add("fromLecture", fromLectureJson);

        JsonArray roomList = (JsonArray) gson.toJsonTree(freeRoomList);
        jsonObj.add("roomList", roomList);
        return jsonObj;
    }


    @RequestMapping("/sendRequestLecture")
    @ResponseBody
    public Callable<JsonObject> SendEmail(@RequestParam String email,
                                          @RequestParam String lectureFrom,
                                          @RequestParam String lectureTo,
                                          @RequestParam String dateWillTeach,
                                          @RequestParam String subjectCode,
                                          @RequestParam String slotWillTeach,
                                          @RequestParam String originalDate,
                                          @RequestParam String originalSlot,
                                          @RequestParam String noChangeRoom,
                                          @RequestParam String room,
                                          @RequestParam String token,
                                          @RequestParam String username,
                                          @RequestParam String name,
                                          @RequestParam String editor) {
        Ultilities.logUserAction("Send emails");
        Callable<JsonObject> callable = () -> {
            JsonObject obj = new JsonObject();

            try {
                EmployeeEntity emp = employeeService.findEmployeeById(Integer.parseInt(lectureFrom));
                String roomName = "Thông báo sau";

                try {
                    if (noChangeRoom.equals("true")) {
                        List<SlotEntity> slotEntities = slotService.findSlotsByName(originalSlot);
                        DaySlotEntity aDaySlot = daySlotService.findDaySlotByDateAndSlot(originalDate, slotEntities.get(0));
                        ScheduleEntity aSchedule = scheduleService.findScheduleByDateSlotAndLecture(aDaySlot, emp);
                        roomName = aSchedule.getRoomId().getName();
                    } else {
                        roomName = room;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                OAuth2Authenticator.initialize();
                SMTPTransport smtpTransport = OAuth2Authenticator.connectToSmtp("smtp.gmail.com", 587, username, token, true);

                Session session = OAuth2Authenticator.getSession();
                MimeMessage mimeMessage = new MimeMessage(session);
                Address toAddress = new InternetAddress(email);
                Address fromAddress = new InternetAddress(username, name, "utf-8");
                String msg = "<div>" +
                        "<h3>Yêu cầu dạy thế</h3>" +
                        "<h4>Dear anh/chị " + lectureTo + ",</h4>" +
                        "<p>Anh/chị vừa nhận được yêu cầu dạy thế từ phòng đào tạo. Thông tin chi tiết như sau: </p>" +
                        "<p>GV cần dạy thế: " + emp.getFullName() + "</p>" +
                        "<p>Môn: " + subjectCode + "</p>" +
                        "<p>Ngày ban đầu: " + originalDate + "</p>" +
                        "<p>Slot ban đầu: " + originalSlot + "</p>" +
                        "<hr>" +
                        "<p>Phòng sẽ dạy: " + roomName + "</p>" +
                        "<p>Ngày sẽ dạy: " + dateWillTeach + "</p>" +
                        "<p>Slot sẽ dạy: " + slotWillTeach + "</p>" +
                        "</div>" +
                        "</hr>" +
                        "<h4>Thông tin thêm:</h4>" +
                        "<div style='margin-top: 10px'>" +
                        editor +
                        "</div>" +
                        "<br/>" +
                        "<h4>Xin cám ơn.</h4>";
                mimeMessage.setContent(msg, "text/html; charset=UTF-8");
                mimeMessage.setFrom(fromAddress);
                mimeMessage.setRecipient(Message.RecipientType.TO, toAddress);
                mimeMessage.setSubject("[FUG-HCM] Yêu cầu dạy thế môn", "utf-8");
                smtpTransport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

                obj.addProperty("success", true);
            } catch (Exception e) {
                e.printStackTrace();
                obj.addProperty("success", false);
                obj.addProperty("msg", e.getMessage());
                e.printStackTrace();
            }

            return obj;
        };

        return callable;
    }


}


