package com.capstone.controllers;

import com.capstone.services.OAuth2Authenticator;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/email")
public class SendEmailController {

    private final String xlsExcelExtension = "xls";
    private final String xlsxExcelExtension = "xlsx";

    private String status = "";
    private boolean run = true;

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
                    tmp.add(String.valueOf(rowIndex));
                    for (int i = 0; i < row.getLastCellNum(); i++) {
                        Cell cell = row.getCell(i);
                        String str;
                        if (cell.getCellType() == Cell.CELL_TYPE_BLANK) str = "N/A";
                        else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                            str = String.valueOf(cell.getNumericCellValue());
                        else str = cell.getStringCellValue();
                        tmp.add(str);
                    }
                    data.add(tmp);
                } else {
                    break;
                }
            }

            data = data.stream().filter(c -> !c.get(0).equals("N/A")).collect(Collectors.toList());

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


    @RequestMapping("/status")
    @ResponseBody
    public JsonObject Status() {
        JsonObject obj = new JsonObject();
        obj.addProperty("status", status);
        obj.addProperty("run", run);
        return obj;
    }

    @RequestMapping("/stop")
    @ResponseBody
    public JsonObject Stop() {
        JsonObject obj = new JsonObject();
        run = false;
        obj.addProperty("success", true);
        obj.addProperty("msg", "Stop succesfull");
        return obj;
    }

    @RequestMapping("/send")
    @ResponseBody
    public Callable<JsonObject> SendEmail(@RequestParam Map<String, String> params, @RequestParam String token, @RequestParam String username, @RequestParam String name) {
        run = true;
        status = "";

        Callable<JsonObject> callable = () -> {
            JsonObject obj = new JsonObject();

            try {
                Gson gson = new Gson();
                List<List<String>> list = gson.fromJson(params.get("params"), new TypeToken<List<List<String>>>() {
                }.getType());

//                Properties props = new Properties();

                String server = "smtp.gmail.com";
//                String userAccount = "fptsendtestemail@gmail.com"; // Sender Account.
//               String password = "namlai120"; // Password -> Application Specific Password.

//                props.put("mail.smtp.host", "smtp.gmail.com");
//                props.put("mail.smtp.user", userAccount);
//                props.put("mail.smtp.password", token);
//                props.put("mail.smtp.port", "587");
//                props.put("mail.smtp.auth", true);
//                props.put("mail.smtp.starttls.enable", "true");
//                props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
//                props.put("mail.smtp.debug", "false");
//                props.put("mail.smtp.socketFactory.port", "465");
//                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//                props.put("mail.smtp.socketFactory.fallback", "false");


//                Properties props = new Properties();
//                props.put("mail.imaps.sasl.enable", "true");
//                props.put("mail.imaps.sasl.mechanisms", "XOAUTH2");
//                props.put(OAuth2SaslClientFactory., token);
//                Session session = Session.getInstance(props);

//                URLName unusedUrlName = null;
//                IMAPSSLStore store = new IMAPSSLStore(session, unusedUrlName);
//                String emptyPassword = "";
//                store.connect(host, port, userEmail, emptyPassword);

                OAuth2Authenticator.initialize();
//                Store imapStore = OAuth2Authenticator.connectToImap("imap.gmail.com", 993, username, token, true);
                SMTPTransport smtpTransport = OAuth2Authenticator.connectToSmtp("smtp.gmail.com", 587, username, token, true);

//                Session session = Session.getInstance(props,
//                        new Authenticator() {
//                            protected PasswordAuthentication getPasswordAuthentication() {
//                                return new PasswordAuthentication(userAccount, password);
//                            }
//                        });

                int i = 1;
                for (List<String> student : list) {
                    if (!run) {
                        System.out.println("Send email has been canceled");
                        break;
                    }

                    Session session = OAuth2Authenticator.getSession();
                    MimeMessage mimeMessage = new MimeMessage(session);
                    Address toAddress = new InternetAddress(student.get(2));
                    Address fromAddress = new InternetAddress(username, name, "utf-8");
                    String msg = "<div>" +
                            "<h3>Thông tin sinh viên</h3>" +
                            "<p>Họ tên: " + student.get(0) + "</p>" +
                            "<p>MSSV: " + student.get(1) + "</p>" +
                            "<p>Tín chỉ: " + student.get(3) + "</p>" +
                            "<p>Môn nợ: " + student.get(4) + "</p>" +
                            "<p>Môn tiếp theo theo tiến trình: " + student.get(5) + "</p>" +
                            "<p>Môn đang học: " + student.get(6) + "</p>" +
                            "<p>Môn châm tiến độ: " + student.get(7) + "</p>" +
                            "<p>Môn dề xuất dự kiến: " + student.get(8) + "</p>" +
                            "</div>";
                    mimeMessage.setContent(msg, "text/html; charset=UTF-8");
                    mimeMessage.setFrom(fromAddress);
                    mimeMessage.setRecipient(Message.RecipientType.TO, toAddress);
                    mimeMessage.setSubject("Thông báo!", "utf-8");
//                    Transport transport = session.getTransport("imap");
//                    transport.connect(server, username , token);
//                    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
//                    String plainTextToken = "user=" + username + "^Aauth=Bearer " + token;
//                    System.out.println("plaintext: " + plainTextToken);
//                    byte[] authToken = plainTextToken.getBytes();
//                    String encodedToken = DatatypeConverter.printBase64Binary(authToken);
//                    smtpTransport.issueCommand("AUTH XOAUTH2 " + plainTextToken, 235);
                    smtpTransport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

                    status = "Đã gửi cho " + i++ + " trên " + list.size() + " sinh viên";
                    System.out.println(status);
                }

                obj.addProperty("success", true);
            } catch (Exception e) {
                e.printStackTrace();
                obj.addProperty("success", false);
                obj.addProperty("msg", e.getMessage());
                e.printStackTrace();
            }

            run = false;
            status = "";

            return obj;
        };

        return callable;
    }
}
