package com.capstone.services;

import com.capstone.models.CustomUser;
import com.capstone.models.Logger;
import com.capstone.models.Ultilities;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//ghi lại những j người dùng đã làm
@WebListener
public class LoggingService implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        List<String> userActions = (List<String>) httpSessionEvent.getSession().getAttribute("uActionList");
        if(userActions != null) {
            CustomUser user = Ultilities.getPrincipal();

            String loggerLocation = LoggingService.class.getProtectionDomain().getCodeSource().getLocation().getPath();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date now = new Date();


            String realPath = loggerLocation.substring(0, loggerLocation.indexOf("WEB-INF")) + "LoggingAction/";
            String fileName = user.getUsername() + "." + now.getTime() + ".log";
            String dateSeperateFile = sdf.format(now) + "-Log/";

            File dir = new File(realPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            //thư mục chứa file log theo ngày
            File dir2 = new File(realPath + dateSeperateFile );
            if(!dir2.exists()){
                dir2.mkdirs();
            }

            File file = new File(realPath + dateSeperateFile + fileName);

            try {
                FileOutputStream fos = new FileOutputStream(file, true);
                PrintWriter pw = new PrintWriter(fos);
                for (String action : userActions) {
                    pw.println(action);
                }
                pw.close();
                fos.close();
            } catch (Exception e) {
                Logger.writeLog(e);
                e.printStackTrace();
            }
        }
    }
}
