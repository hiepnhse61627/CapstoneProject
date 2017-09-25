package com.capstone.controllers;

import com.capstone.services.BaseServiceImpl;
import com.capstone.services.IBaseService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SqlQueryController {

    IBaseService baseService = new BaseServiceImpl();

    @RequestMapping(value = "/goSQLQueryPage")
    public String goSqlQueryPage() {
        return "sqlQueryPage";
    }

    @RequestMapping(value = "/query-in-database")
    @ResponseBody
    public JsonObject getDataInDatabaseByQuery(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();

        try {
            List<Object> objects = baseService.getDataInDatabaseByQuery(params.get("queryStr"));
            List<Object> objects2 = objects.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            JsonArray aaData = (JsonArray) gson.toJsonTree(objects2);

            Object[] firstArrObject = (Object[]) objects2.get(0);
            int arrLength = Array.getLength(firstArrObject);
            List<Object[]> aTargetList = new ArrayList<>();

            Object[] child = new Object[arrLength];
            for (int i = 0; i < arrLength; i++) {
                child[i] = i;
            }
            aTargetList.add(child);

            JsonArray aTargets = (JsonArray) gson.toJsonTree(aTargetList);
            Map<String, JsonArray> aoColumnDefsMap = new HashMap<>();
            aoColumnDefsMap.put("aTargets", aTargets);

            JsonObject aoColumnDefs = (JsonObject) gson.toJsonTree(aoColumnDefsMap);

            jsonObject.addProperty("iTotalRecords", objects.size());
            jsonObject.addProperty("iTotalDisplayRecords",  objects.size());
            jsonObject.add("aaData", aaData);
            jsonObject.add("aoColumnDefs", aoColumnDefs);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return jsonObject;
    }
}
