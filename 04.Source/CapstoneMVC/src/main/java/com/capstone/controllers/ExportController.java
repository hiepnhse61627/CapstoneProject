package com.capstone.controllers;

import com.capstone.exporters.IExportObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class ExportController {
    private final String mimeType = "application/octet-stream";
    private final String headerKey = "Content-Disposition";
    private IExportObject exportObject;

    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public void exportFile(@RequestParam Map<String, String> params, HttpServletResponse response) {
        exportObject = createExportImplementation(Integer.parseInt(params.get("objectType")));
        // set content attributes for the response
        response.setContentType(mimeType);
        // set headers for the response
        String headerValue = String.format("attachment; filename=\"%s\"", exportObject.getFileName());
        response.setHeader(headerKey, headerValue);
        // get output stream of the response
        OutputStream os;
        try {
            os = response.getOutputStream();
            exportObject.writeData(os, params);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Implement Export file
     */
    private IExportObject createExportImplementation(int objectType) {
        final String[] CLASSNAME_EXPORTER = {
                "",
                "com.capstone.exporters.ExportStudentsFailImpl", // 1 = Export students fail
                "com.capstone.exporters.ExportStudentFailedAndNextSubjectImpl" // 2 = export failed subject of each student and next subject
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
