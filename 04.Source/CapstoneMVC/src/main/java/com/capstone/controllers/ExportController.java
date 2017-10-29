package com.capstone.controllers;

import com.capstone.exporters.ExportStatusReport;
import com.capstone.exporters.IExportObject;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.Callable;

@Controller
public class ExportController {
    private final String mimeType = "application/octet-stream";
    private final String headerKey = "Content-Disposition";
    private IExportObject exportObject;

    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public Callable exportFile(@RequestParam Map<String, String> params, HttpServletResponse response) {
        ExportStatusReport.StatusExportStudentDetailRunning = true;
        ExportStatusReport.StatusStudentDetailExport = "";

        Callable callable = () -> {
            exportObject = createExportImplementation(Integer.parseInt(params.get("objectType")));

            // get output stream of the response
            OutputStream os;
            try {
                // set content attributes for the response
                response.setContentType(mimeType);

                // set headers for the response
                String headerValue = String.format("attachment; filename=\"%s\"", exportObject.getFileName());
                response.setHeader(headerKey, headerValue);

                // write data
                os = response.getOutputStream();
                exportObject.writeData(os, params);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ExportStatusReport.StatusExportStudentDetailRunning = false;
            ExportStatusReport.StatusStudentDetailExport = "";

            return null;
        };

        return callable;
    }

    @RequestMapping(value = "/getStatusExport")
    @ResponseBody
    public JsonObject getStatus() {
        JsonObject data = new JsonObject();
        data.addProperty("running",  ExportStatusReport.StatusExportStudentDetailRunning);
        data.addProperty("status",  ExportStatusReport.StatusStudentDetailExport);
        return data;
    }

    /**
     * Implement Export file
     */
    private IExportObject createExportImplementation(int objectType) {
        final String[] CLASSNAME_EXPORTER = {
                "",
                "com.capstone.exporters.ExportStudentsFailImpl", // 1 = Export students fail
                "com.capstone.exporters.ExportStudentFailedAndNextSubjectImpl", // 2 = export failed subject of each student and next subject
                "com.capstone.exporters.ExportStudentFailedPrerequisiteImpl", // 3 = Export student fail prerequisite
                "com.capstone.exporters.ExportGraduatedStudentsImpl", // 4 = Export graduated student
                "com.capstone.exporters.ExportPDFGraduatedStudentsImpl", // 5 = Export PDF graduated student
                "com.capstone.exporters.ExportCurriculumImpl", // 6 = Export curriculum
                "com.capstone.exporters.ExportStudentListImpl", // 7 = Export students
                "com.capstone.exporters.ExportPercentFailImpl" // 8 = Export percent fail
        };

        try {
            Class exportClass = Class.forName(CLASSNAME_EXPORTER[objectType]);
            return (IExportObject) exportClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }
}
