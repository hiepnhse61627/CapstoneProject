package com.capstone.models;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReadAndSaveFileToServer {
    public void saveFile(ServletContext context, MultipartFile file, String folder) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();

//                File dir = new File(context.getRealPath("/") + "UploadedFiles/UploadedSubjectTemplate/");
                File dir = new File(context.getRealPath("/") + "UploadedFiles/" + folder + "/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File[] files = dir.listFiles();
                if (files.length > 5) {
                    files[0].delete();
                }

                File serverFile = new File(dir.getAbsolutePath()
                        + File.separator + file.getOriginalFilename());
                if (serverFile.exists()) {
                    SimpleDateFormat df = new SimpleDateFormat("_yyyy-MM-dd-HH-mm-ss");
                    String suffix = df.format(Calendar.getInstance().getTime());
                    serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename() + suffix);
                }

                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();

                System.out.println(("Server File Location = " + serverFile.getAbsolutePath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public File[] readFiles(ServletContext context, String folder) {
        File[] files = null;
        File dir = new File(context.getRealPath("/") + "UploadedFiles/" + folder + "/");
        if (dir.isDirectory()) {
            files = dir.listFiles();
        }
        return files;
    }
}
