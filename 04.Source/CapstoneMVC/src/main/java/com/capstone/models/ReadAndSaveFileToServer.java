package com.capstone.models;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

public class ReadAndSaveFileToServer {
    public void saveFile(ServletContext context, MultipartFile file, String folder) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();

                File dir = new File(context.getRealPath("/") + "UploadedFiles/" + folder + "/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File[] files = dir.listFiles();
                Arrays.sort(files, Comparator.comparingLong(File::lastModified));
                if (files.length > 4) {
                    files[0].delete();
                    System.out.println(files[0].getName() + " deleted!");
                }

                File serverFile = new File(dir.getAbsolutePath()
                        + File.separator + file.getOriginalFilename());
                if (serverFile.exists()) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                    String suffix = df.format(Calendar.getInstance().getTime());

                    String name = file.getOriginalFilename();
                    if (name.contains(".")) {
                        String filename = name.substring(0, name.lastIndexOf("."));
                        String extension = name.substring(name.lastIndexOf("."), name.length());
                        name = filename + "_" + suffix + extension;
                        serverFile = new File(dir.getAbsolutePath() + File.separator + name);
                    } else {
                        serverFile = new File(dir.getAbsolutePath() + File.separator + name + suffix);
                    }
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
            Arrays.sort(files, Comparator.comparingLong(c -> c.lastModified()));
        }
        return files;
    }
}
