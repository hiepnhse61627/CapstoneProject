package com.capstone.controllers;

import com.capstone.entities.StudentEntity;
import com.capstone.exporters.ExportStatusReport;
import com.capstone.exporters.IExportObject;
import com.capstone.services.*;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping(value = "/pauseexportStudentDetail")
    @ResponseBody
    public JsonObject Stop() {
        ExportStatusReport.StopExporting = true;
        JsonObject obj = new JsonObject();
        obj.addProperty("success", true);
        return obj;
	}

    @RequestMapping(value = "/exportExcel")
    @ResponseBody
    public Callable exportFile(@RequestParam Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {
        ExportStatusReport.StatusExportStudentDetailRunning = true;
        ExportStatusReport.StatusStudentDetailExport = "";
        ExportStatusReport.StopExporting = false;

        Callable callable = () -> {
            exportObject = createExportImplementation(Integer.parseInt(params.get("objectType")));

            // get output stream of the response
            OutputStream os;
            try {
                // set content attributes for the response
                response.setContentType(mimeType);

                // set headers for the response
                if (params.get("objectType").equals("4")) {
                    int programId = Integer.parseInt(params.get("programId"));
                    int semesterId = Integer.parseInt(params.get("semesterId"));
                    String type = params.get("type");

                    IRealSemesterService service = new RealSemesterServiceImpl();
                    IProgramService programService = new ProgramServiceImpl();
                    exportObject.setFileName(programService.getProgramById(programId).getName() + "_" + type + "_" + service.findSemesterById(semesterId).getSemester() + ".xlsx");
                    String headerValue = String.format("attachment; filename=\"%s\"", exportObject.getFileName());
                    response.setHeader(headerKey, headerValue);
                } else if(params.get("objectType").equals("18")) {
                    Integer studentId = Integer.valueOf(params.get("studentId"));
                    IStudentService studentService = new StudentServiceImpl();

                    StudentEntity studentEntity = studentService.findStudentById(studentId);
                    exportObject.setFileName("IAT-" + studentEntity.getRollNumber() + ".xlsx");
                    String headerValue = String.format("attachment; filename=\"%s\"", exportObject.getFileName());
                    response.setHeader(headerKey, headerValue);
                } else {
                    String headerValue = String.format("attachment; filename=\"%s\"", exportObject.getFileName());
                    response.setHeader(headerKey, headerValue);
                }

                // write data
                os = response.getOutputStream();
                exportObject.writeData(os, params, request);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ExportStatusReport.StatusExportStudentDetailRunning = false;
            ExportStatusReport.StatusStudentDetailExport = "";
            ExportStatusReport.StopExporting = false;

            return null;
        };


        return callable;
    }

    @RequestMapping(value = "/exportExcelWithoutCallable")
    @ResponseBody
    public void exportFileWithoutCallable(@RequestParam Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {

        exportObject = createExportImplementation(Integer.parseInt(params.get("objectType")));
        ExportStatusReport.StatusExportStudentDetailRunning = true;
        ExportStatusReport.StatusStudentDetailExport = "";
        ExportStatusReport.StopExporting = false;
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
            exportObject.writeData(os, params, request);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                "com.capstone.exporters.ExportPercentFailImpl", // 8 = Export percent fail
                "com.capstone.exporters.ExportGoodStudentsImpl", // 9 = Export good student
                "com.capstone.exporters.ExportFailStatisticsImpl", // 10 = Export fail statistics
                "com.capstone.exporters.ExportStudentArrangementImpl", // 11 = Export student arrangement
                "com.capstone.exporters.ExportStudentArrangementValidationImpl", // 12 = Export student arrangement validate
                "com.capstone.exporters.ExportStudentOnlyNextImpl", // 13 = Export student only next subjects
                "com.capstone.exporters.ExportAllCurriculumImpl", // 14 = Export student only next subjects
                "com.capstone.exporters.ExportStudentArrangementBySlotImpl", // 15 = Export student arrangement by slot
                "com.capstone.exporters.ExportPDFGraduatedStudentsImpl", // 16 = Export PDF Graduated
                "com.capstone.exporters.ExportExcelGraduatedStudentsImpl", // 17 = export excel graduated
                "com.capstone.exporters.ExportInterimAcademicTranscriptImpl", // 18 = export interim academic transcript
                "com.capstone.exporters.ExportStudentsFailedMoreThanRequiredCreditsImpl", // 19 = export students failed more than required credits
                "com.capstone.exporters.ExportExcelStudentsStudyResultBySemester", //20 = export students study Results
                "com.capstone.exporters.ExportExcelStudentsStudyInfo", //21 = export students study info
                "com.capstone.exporters.ExportConvert2StudentQuantityByClassAndSubject", //22
				"com.capstone.exporters.ExportBestStudentImpl", // 23 = Best student in subjects in semester
                "com.capstone.exporters.ExportChangedScheduleImpl", // 24 = Changed schedule
                "com.capstone.exporters.ExportFreeScheduleImpl", // 25 = Free schedule
                "com.capstone.exporters.ExportGraduatedConfirmation" // 26

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
