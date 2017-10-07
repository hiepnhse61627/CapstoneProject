package com.capstone.models;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static void writeLog(ServletContext context, Exception ex) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date now = new Date();

        String realPath = context.getRealPath("/") + "LoggingFile/";
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
