package com.capstone.controllers;

import com.capstone.models.Ultilities;
import com.capstone.entities.*;
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
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.capstone.services.DateUtil.getDate;

@Controller
public class RoomList {

    IRoomService roomService = new RoomServiceImpl();

    IScheduleService scheduleService = new ScheduleServiceImpl();

    ISlotService slotService = new SlotServiceImpl();

    IDaySlotService daySlotService = new DaySlotServiceImpl();

    IEmployeeService employeeService = new EmployeeServiceImpl();

    IRealSemesterService realSemesterService = new RealSemesterServiceImpl();

    ISubjectService subjectService = new SubjectServiceImpl();

    @RequestMapping("/roomList")
    public ModelAndView RoomListAll(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());
        ModelAndView view = new ModelAndView("RoomList");
        view.addObject("title", "Danh sách phòng");

        return view;
    }

    @RequestMapping(value = "/loadRoomList")
    @ResponseBody
    public JsonObject LoadRoomListAll(@RequestParam Map<String, String> params) {
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
            queryStr = "SELECT COUNT(s) FROM RoomEntity s";
            TypedQuery<Integer> queryCounting = em.createQuery(queryStr, Integer.class);
            iTotalRecords = ((Number) queryCounting.getSingleResult()).intValue();

            // Đếm số lượng gv sau khi filter
            if (!sSearch.isEmpty()) {
                queryStr = "SELECT COUNT(s) FROM RoomEntity s" +
                        " WHERE s.name LIKE :name";
                queryCounting = em.createQuery(queryStr, Integer.class);
                queryCounting.setParameter("name", "%" + sSearch + "%");
                iTotalDisplayRecords = ((Number) queryCounting.getSingleResult()).intValue();
            } else {
                iTotalDisplayRecords = iTotalRecords;
            }

            // Query danh sách gv
            queryStr = "SELECT s FROM RoomEntity s" +
                    (!sSearch.isEmpty() ? " WHERE s.name LIKE :name" : "");
            TypedQuery<RoomEntity> query = em.createQuery(queryStr, RoomEntity.class);
            query.setFirstResult(iDisplayStart);
            query.setMaxResults(iDisplayLength);
            if (!sSearch.isEmpty()) {
                query.setParameter("name", "%" + sSearch + "%");
            }
            List<RoomEntity> roomList = query.getResultList();

            List<List<String>> result = new ArrayList<>();
            for (RoomEntity room : roomList) {
                List<String> dataList = new ArrayList<String>();

                dataList.add(room.getId() + "");
                dataList.add(room.getName());
                dataList.add(room.getCapacity() + "");
                dataList.add(room.getNote());
                dataList.add(room.getIsAvailable() + "");

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


    @RequestMapping("/roomHistory")
    public ModelAndView ScheduleChangeStatistic(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());
        ModelAndView view = new ModelAndView("RoomHistory");
        view.addObject("title", "Lịch sử phòng");

        List<RoomEntity> rooms = roomService.findAllRooms();
        view.addObject("rooms", rooms);

        return view;
    }

    @RequestMapping(value = "/roomHistory/get")
    @ResponseBody
    public JsonObject LoadRoomHistory(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        try {
            List<List<String>> result = LoadRoomHistoryImpl(params);

            Gson gson = new Gson();
            JsonArray array = (JsonArray) gson.toJsonTree(result);

            jsonObj.add("aaData", array);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    public List<List<String>> LoadRoomHistoryImpl(Map<String, String> params) {
        List<List<String>> result = new ArrayList<>();
        try {
            Integer roomId = null;
            String startDate = params.get("startDate");

            List<DaySlotEntity> daySlotEntityList = null;
            RoomEntity aRoom = null;
            List<ScheduleEntity> scheduleList = new ArrayList<>();
            if (!params.get("room").equals("") && !params.get("room").equals("-1")) {
                roomId = Integer.parseInt(params.get("room"));
            }

            if (roomId != null) {
                aRoom = roomService.findRoomById(roomId);
            }

            daySlotEntityList = daySlotService.findDaySlotByDate(startDate);

            for (DaySlotEntity aDaySlot : daySlotEntityList) {
                if (aRoom != null) {
                    ScheduleEntity aSchedule = scheduleService.findScheduleByDateSlotAndRoom(aDaySlot, aRoom);
                    if (aSchedule != null) {
                        scheduleList.add(aSchedule);
                    }
                } else {
                    List<ScheduleEntity> schedules = scheduleService.findScheduleByDateSlot(aDaySlot);
                    if (schedules != null) {
                        for (ScheduleEntity aSchedule : schedules) {
                            scheduleList.add(aSchedule);
                        }
                    }
                }
            }

            for (ScheduleEntity schedule : scheduleList) {

                List<String> dataList = new ArrayList<String>();

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


                result.add(dataList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    @RequestMapping("/freeRoom")
    public ModelAndView ScheduleListAll(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());
        ModelAndView view = new ModelAndView("FreeRoom");
        view.addObject("title", "Đổi phòng trống");

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

    @RequestMapping(value = "/freeRoom/get")
    @ResponseBody
    public JsonObject RequestFreeRoom(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        List<List<String>> result = new ArrayList<>();
        List<ScheduleEntity> scheduleList = new ArrayList<>();
        Set<String> freeRoomList = new HashSet<>();
        Set<String> removeRoomList = new HashSet<>();
        try {
            String employeeId = "";
            String startDate = params.get("startDate");
            String slotName = "";
            if (!params.get("employeeId").equals("") && !params.get("employeeId").equals("-1")) {
                employeeId = params.get("employeeId");
            }

            if (!params.get("slot").equals("")) {
                slotName = params.get("slot");
            }

            if(!slotName.equals("")){
                List<SlotEntity> slotEntities = slotService.findSlotsByName(slotName);

                if (slotEntities != null && slotEntities.size() > 0) {
                    DaySlotEntity aDaySlot = daySlotService.findDaySlotByDateAndSlot(startDate, slotEntities.get(0));
                    if (aDaySlot != null) {
                        //find all schedule in the selected time
//                    for (DaySlotEntity aDaySlot : daySlotEntityList) {
                        List<ScheduleEntity> schedules = scheduleService.findScheduleByDateSlot(aDaySlot);
                        scheduleList.addAll(schedules);
//                    }
                    }

                    //find slot belong to an employee
                    if (!employeeId.equals("")) {
                        List<ScheduleEntity> newScheduleList = new ArrayList<>();
                        for (ScheduleEntity schedule : scheduleList) {
                            if (schedule.getEmpId().getId() == Integer.parseInt(employeeId)) {
                                newScheduleList.add(schedule);
                            }
                        }
                        scheduleList = new ArrayList<>(newScheduleList);
                    }

                    for (ScheduleEntity schedule : scheduleList) {

                        //add room is currently in use
                        removeRoomList.add(schedule.getRoomId().getName());

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


                    List<RoomEntity> allRooms = roomService.findAllRooms();
                    for (RoomEntity aRoom : allRooms) {
                        freeRoomList.add(aRoom.getName());
                    }
                    //get rooms not in use by removing rooms in use in all room list
                    freeRoomList.removeAll(removeRoomList);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        JsonArray array = (JsonArray) gson.toJsonTree(result);
        JsonArray roomList = (JsonArray) gson.toJsonTree(freeRoomList);
        jsonObj.add("roomList", roomList);
        jsonObj.add("aaData", array);
        return jsonObj;
    }


    @RequestMapping(value = "/roomList/create")
    @ResponseBody
    public JsonObject CreateRoomList(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();

        try {

            String name = "";
            if (!params.get("name").equals("")) {
                name = params.get("name");
            }

            String capacity = "0";
            if (!params.get("capacity").equals("")) {
                capacity = params.get("capacity");
            }

            String note = "";
            if (!params.get("note").equals("")) {
                note = params.get("note");
            }

            boolean isAvailable = true;
            if (!params.get("isAvailable").equals("")) {
                if (params.get("isAvailable").equals("true")) {
                    isAvailable = true;
                } else {
                    isAvailable = false;
                }
            }

            List<RoomEntity> roomEntity = roomService.findRoomsByName(name);

            if (roomEntity != null && roomEntity.size() == 0) {
                RoomEntity aRoom = new RoomEntity();
                aRoom.setCapacity(Integer.parseInt(capacity));
                aRoom.setNote(note);
                aRoom.setName(name);
                aRoom.setIsAvailable(isAvailable);

                roomService.createRoom(aRoom);
                jsonObj.addProperty("success", true);
            } else {
                jsonObj.addProperty("fail", true);
                jsonObj.addProperty("message", "Tên phòng đã tồn tại");

            }
        } catch (Exception e) {
            jsonObj.addProperty("fail", true);
            jsonObj.addProperty("message", e.getMessage());
            e.printStackTrace();
        }

        return jsonObj;
    }

    @RequestMapping(value = "/roomList/edit")
    @ResponseBody
    public JsonObject EditRoomList(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();

        try {
            String roomId = "";
            if (!params.get("roomId").equals("")) {
                roomId = params.get("roomId");
            }

            String name = "";
            if (!params.get("name").equals("")) {
                name = params.get("name");
            }

            String capacity = "0";
            if (!params.get("capacity").equals("")) {
                capacity = params.get("capacity");
            }

            String note = "";
            if (!params.get("note").equals("")) {
                note = params.get("note");
            }

            boolean isAvailable = true;
            if (!params.get("isAvailable").equals("")) {
                if (params.get("isAvailable").equals("true")) {
                    isAvailable = true;
                } else {
                    isAvailable = false;
                }
            }

            RoomEntity roomEntity = roomService.findRoomById(Integer.parseInt(roomId));

            if (roomEntity != null) {
                roomEntity.setCapacity(Integer.parseInt(capacity));
                roomEntity.setNote(note);
                roomEntity.setName(name);
                roomEntity.setIsAvailable(isAvailable);

                EntityManagerFactory emf2 = Persistence.createEntityManagerFactory("CapstonePersistence");
                EntityManager em = emf2.createEntityManager();
                try {
                    em.getTransaction().begin();
                    em.merge(roomEntity);
                    em.getTransaction().commit();
                    jsonObj.addProperty("success", true);
                } catch (Exception e) {
                    jsonObj.addProperty("fail", true);
                    jsonObj.addProperty("message", e.getMessage());
                    e.printStackTrace();
                }
            } else {
                jsonObj.addProperty("fail", true);
            }
        } catch (Exception e) {
            jsonObj.addProperty("fail", true);
            jsonObj.addProperty("message", e.getMessage());
            e.printStackTrace();
        }

        return jsonObj;
    }


}


