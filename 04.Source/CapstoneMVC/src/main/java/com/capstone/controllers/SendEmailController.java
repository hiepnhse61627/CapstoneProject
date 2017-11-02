package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.ReadAndSaveFileToServer;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/email")
public class SendEmailController {

    private final String xlsExcelExtension = "xls";
    private final String xlsxExcelExtension = "xlsx";

    final String smtpServer = "smtp.gmail.com";
    final String userAccount = "fptsendtestemail@gmail.com"; // Sender Account.
    //    final String password = "lhhsqhsbagjpjhxa"; // Password -> Application Specific Password.
    final String password = "namlai120"; // Password -> Application Specific Password.
    final String SOCKET_FACTORY = "javax.net.ssl.SSLSocketFactory";
    final String smtpPort = "587";
    final String PORT = "465";

    @RequestMapping("/index")
    public String Index() {
        return "SendEmail";
    }

    @RequestMapping(value = "/uploadEmail", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject uploadFile(@RequestParam("file") MultipartFile file) {
        JsonObject obj = ReadFile(file);
        ;
        return obj;
    }

    private JsonObject ReadFile(MultipartFile file) {
        JsonObject obj = new JsonObject();

        try {
            InputStream is = file.getInputStream();

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1, originalFileName.length());

            Workbook workbook = null;
            Sheet spreadsheet = null;
            Row row = null;
            if (extension.equals(xlsExcelExtension)) {
                workbook = new HSSFWorkbook(is);
                spreadsheet = workbook.getSheetAt(0);
            } else if (extension.equals(xlsxExcelExtension)) {
                workbook = new XSSFWorkbook(is);
                spreadsheet = workbook.getSheetAt(0);
            } else {
                obj.addProperty("success", false);
                obj.addProperty("message", "Chỉ chấp nhận file excel");
                return obj;
            }

            int excelDataIndex = 6;

            List<List<String>> data = new ArrayList<>();
            for (int rowIndex = excelDataIndex; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    List<String> tmp = new ArrayList<>();
                    for (int i = 0; i < row.getLastCellNum(); i++) {
                        Cell cell = row.getCell(i);
                        String str;
                        if (cell.getCellType() == Cell.CELL_TYPE_BLANK) str = "N/A";
                        else str = cell.getStringCellValue();
                        tmp.add(str);
                    }
                    data.add(tmp);
                } else {
                    break;
                }
            }

            JsonArray array = (JsonArray) new Gson().toJsonTree(data);

            obj.addProperty("success", true);
            obj.add("data", array);
        } catch (Exception e) {
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
            e.printStackTrace();
        }

        obj.addProperty("success", true);
        return obj;
    }

    @RequestMapping("/send")
    @ResponseBody
    public JsonObject SendEmail(@RequestParam Map<String, String> params) {
        JsonObject obj = new JsonObject();

        try {
            Gson gson = new Gson();
            List<List<String>> list = gson.fromJson(params.get("params"), new TypeToken<List<List<String>>>() {
            }.getType());

            final Properties props = new Properties();
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.user", userAccount);
            props.put("mail.smtp.password", password);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.debug", "false");
            props.put("mail.smtp.socketFactory.port", PORT);
            props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
            props.put("mail.smtp.socketFactory.fallback", "false");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(userAccount, password);
                        }
                    });

            for (List<String> student : list) {
                MimeMessage mimeMessage = new MimeMessage(session);
                final Address toAddress = new InternetAddress(student.get(2)); // toAddress
                final Address fromAddress = new InternetAddress(userAccount);
                String msg = "<div>" +
                        "<h3>Thông tin sinh viên</h3>" +
                        "<p>Họ têm: " + student.get(0) + "</p>" +
                        "<p>MSSV: " + student.get(1) + "</p>" +
                        "<p>Môn nợ: " + student.get(3) + "</p>" +
                        "<p>Môn tiếp theo theo tiến trình: " + student.get(4) + "</p>" +
                        "<p>Môn đang học: " + student.get(5) + "</p>" +
                        "<p>Môn châm tiến độ: " + student.get(6) + "</p>" +
                        "<p>Môn dề xuất dự kiến: " + student.get(7) + "</p>" +
                        "</div>";
                mimeMessage.setContent(msg, "text/html; charset=UTF-8");
                mimeMessage.setFrom(fromAddress);
                mimeMessage.setRecipient(javax.mail.Message.RecipientType.TO, toAddress);
                mimeMessage.setSubject("Thông báo!");
                Transport transport = session.getTransport("smtp");
                transport.connect(smtpServer, userAccount, password);
                transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            }

            obj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
            e.printStackTrace();
        }

        return obj;
    }
}
