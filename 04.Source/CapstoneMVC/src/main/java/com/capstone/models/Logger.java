package com.capstone.models;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static void writeLog(Exception ex) {
        String loggerLocation = Logger.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date now = new Date();

        String realPath = loggerLocation.substring(0, loggerLocation.indexOf("WEB-INF")) + "LoggingFile/";
        System.out.println(realPath);
        String fileName = "exception." + sdf.format(now) + ".log";

        sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        File dir = new File(realPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(realPath + fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            PrintWriter pw = new PrintWriter(fos);
            pw.println("[" + sdf.format(now) + "]");
            ex.printStackTrace(pw);
            pw.println();

            pw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
