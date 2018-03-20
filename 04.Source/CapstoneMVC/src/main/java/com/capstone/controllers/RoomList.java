package com.capstone.controllers;

import com.capstone.entities.EmployeeEntity;
import com.capstone.entities.RoomEntity;
import com.capstone.entities.ScheduleEntity;
import com.capstone.models.Ultilities;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class RoomList {

    @RequestMapping("/roomList")
    public ModelAndView RoomListAll(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " +request.getRequestURI());
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

                dataList.add(room.getName());
                dataList.add(room.getCapacity()+"");

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


