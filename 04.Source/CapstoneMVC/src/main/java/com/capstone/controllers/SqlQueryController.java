package com.capstone.controllers;

import com.capstone.models.ColumnModel;
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

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SqlQueryController {

    IBaseService baseService = new BaseServiceImpl();

    @RequestMapping(value = "/goSQLQueryPage")
    public String goSqlQueryPage() {
        return "sqlQueryPage";
    }

    @RequestMapping(value = "/get-table-properties", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject getTableProperties(@RequestParam("queryStr") String sqlString) {
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();

        try {
            // Create aoColumns sTitle
            List<String> headers = baseService.getColumnHeaders(sqlString);
            List<ColumnModel> columnHeaders = new ArrayList<>();
            for (String header : headers) {
                ColumnModel columnModel = new ColumnModel();
                columnModel.setsTitle(header);
                columnHeaders.add(columnModel);
            }
            JsonArray aoColumnsData = (JsonArray) gson.toJsonTree(columnHeaders);
            // Create aoColumnDefs
            List<Object[]> aTargetList = new ArrayList<>();
            Object[] child = new Object[headers.size()];
            for (int i = 0; i < headers.size(); i++) {
                child[i] = i;
            }
            aTargetList.add(child);
            JsonArray aTargets = (JsonArray) gson.toJsonTree(aTargetList);
            Map<String, JsonArray> aoColumnDefsMap = new HashMap<>();
            aoColumnDefsMap.put("aTargets", aTargets);
            JsonObject aoColumnDefs = (JsonObject) gson.toJsonTree(aoColumnDefsMap);
            // add to json object
            jsonObject.addProperty("success", true);
            jsonObject.add("aoColumns", aoColumnsData);
            jsonObject.add("aoColumnDefs", aoColumnDefs);
            return jsonObject;
        } catch (Exception ex) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", ex.getMessage());
            return jsonObject;
        }
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

            jsonObject.addProperty("iTotalRecords", objects.size());
            jsonObject.addProperty("iTotalDisplayRecords",  objects.size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return jsonObject;
    }
}
