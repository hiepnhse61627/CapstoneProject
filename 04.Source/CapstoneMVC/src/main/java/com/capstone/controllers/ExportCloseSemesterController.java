package com.capstone.controllers;

import com.capstone.exporters.ExportStatusReport;
import com.capstone.exporters.IExportObject;
import com.capstone.models.Jobs;
import com.capstone.models.Logger;
import com.capstone.services.IRealSemesterService;
import com.capstone.services.RealSemesterServiceImpl;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/managerrole")
public class ExportCloseSemesterController {
    //    private final String mimeType = "application/octet-stream";
//    private final String headerKey = "Content-Disposition";

//    @RequestMapping(value = "/pauseexportStudentDetail")
//    @ResponseBody
//    public JsonObject Stop() {
//        ExportStatusReport.StopExporting = true;
//        JsonObject obj = new JsonObject();
//        obj.addProperty("success", true);
//        return obj;
//	}

    private OutputStream Writefile(Map<String, String> params, IExportObject exportObject) throws Exception {
        String loggerLocation = Logger.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String path = loggerLocation.substring(0, loggerLocation.indexOf("WEB-INF")) + "CloseSemester/";

        IRealSemesterService service = new RealSemesterServiceImpl();
        String semester;
        try {
            semester = service.findSemesterById(Integer.parseInt(params.get("semesterId"))).getSemester();
        } catch (NumberFormatException e) {
            semester = params.get("semesterId");
        }

        String realPath = path + semester + "/";

        File dir = new File(realPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = exportObject.getFileName();
        File file = new File(realPath + fileName);
        if (file.exists()) {
            file.delete();
        }

        OutputStream os = new FileOutputStream(file);
        exportObject.writeData(os, params);
        return os;
    }

    @RequestMapping("/get/{semester}")
    public void GetFolder(HttpServletRequest request, HttpServletResponse response, @PathVariable("semester") String semester) throws Exception {
//        String semester = service.findSemesterById(Integer.parseInt(params.get("semesterId"))).getSemester();
        String realPath = request.getServletContext().getRealPath("/") + "CloseSemester/" + semester + "/";
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"" + semester +".zip\"");
        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());

        //simple file list, just for tests
        File file = new File(realPath);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();

            //packing files
            for (File f : files) {
                //new zip entry and copying inputstream with f to zipOutputStream, after all closing streams
                zipOutputStream.putNextEntry(new ZipEntry(f.getName()));
                FileInputStream fileInputStream = new FileInputStream(f);
                IOUtils.copy(fileInputStream, zipOutputStream);
                fileInputStream.close();
                zipOutputStream.closeEntry();
            }
        }

        zipOutputStream.close();
    }

    @RequestMapping(value = "/export")
    @ResponseBody
    public JsonObject exportFile(@RequestParam String semesterId, HttpServletRequest servlet) {
//        ExportStatusReport.StatusExportStudentDetailRunning = true;
//        ExportStatusReport.StatusStudentDetailExport = "";
//        ExportStatusReport.StopExporting = false;

        JsonObject obj = new JsonObject();
        Thread t = new Thread(() -> {

            IRealSemesterService service = new RealSemesterServiceImpl();
            String semName = service.findSemesterById(Integer.parseInt(semesterId)).getSemester();

            Jobs.addJob(semName, "0");

            OutputStream os = null;

            for (int i = 1; i <= 10; i++) {
                try {
                    IExportObject exportObject = createExportImplementation(i);

                    if (i == 1) {
                        Map<String, String> params = new HashMap<>();
                        params.put("semesterId", semesterId);
                        os = Writefile(params, exportObject);
                    } else if (i == 2) {
                        Map<String, String> params = new HashMap<>();
                        params.put("semesterId", semesterId);
                        params.put("studentId", "64350");
                        os = Writefile(params, exportObject);
                    } else if (i == 4) {
                        Map<String, String> params = new HashMap<>();
                        params.put("type", "Graduate");
                        params.put("semesterId", semesterId);
                        params.put("programId", "-1");
                        os = Writefile(params, exportObject);
                    } else if (i == 7) {
                        Map<String, String> params = new HashMap<>();
                        params.put("semesterId", semesterId);
                        os = Writefile(params, exportObject);
                    } else if (i == 9) {
                        Map<String, String> params = new HashMap<>();
                        params.put("semesterId", semesterId);
                        os = Writefile(params, exportObject);
                    } else if (i == 10) {
                        Map<String, String> params = new HashMap<>();
                        params.put("semesterId", semName);
                        os = Writefile(params, exportObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error at" + i);
                } finally {
                    try {
                        if (os != null) {
                            os.flush();
                            os.close();
                            System.out.println("Exported " + i + " file!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            Jobs.addJob(semName, "1");
            System.out.println("Exported All");
        });
        t.start();

        obj.addProperty("msg", "Thread " + t.getId() + " has started");

        return obj;
    }

//    @RequestMapping(value = "/getStatusExport")
//    @ResponseBody
//    public JsonObject getStatus() {
//        JsonObject data = new JsonObject();
//        data.addProperty("running",  ExportStatusReport.StatusExportStudentDetailRunning);
//        data.addProperty("status",  ExportStatusReport.StatusStudentDetailExport);
//        return data;
//    }

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
                "com.capstone.exporters.ExportFailStatisticsImpl" // 10 = Export fail statistics
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
