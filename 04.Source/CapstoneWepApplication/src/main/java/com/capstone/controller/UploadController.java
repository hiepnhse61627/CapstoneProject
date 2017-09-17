package com.capstone.controller;

import com.capstone.entities.StudentEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class UploadController {

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();

        XSSFWorkbook workbook = new XSSFWorkbook(is);
        XSSFSheet spreadsheet = workbook.getSheetAt(0);

        XSSFRow row;
        int rollNumberIndex = 1;
        int studentNameIndex = 2;
        int excelDataIndex = 3;
        List<StudentEntity> students = new ArrayList<StudentEntity>();

        for (int rowIndex = excelDataIndex; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
            row = spreadsheet.getRow(rowIndex);
            if (row != null) {
                StudentEntity student = new StudentEntity();
                Cell rollNumberCell = row.getCell(rollNumberIndex);
                Cell studentNameCell = row.getCell(studentNameIndex);
                if (rollNumberCell != null) {
                    System.out.println(rollNumberCell.getStringCellValue() + " \t\t ");
                    student.setRollNumber(rollNumberCell.getStringCellValue());
                }
                if (studentNameCell != null) {
                    System.out.println(studentNameCell.getStringCellValue());
                    student.setFullName(studentNameCell.getStringCellValue());
                }

                if (student.getRollNumber() != null) {
                    students.add(student);
                }
            }
        }
        System.out.println("Danh sach sinh vien: " + students.size());
    }

    @RequestMapping(value = "/uploadMark", method = RequestMethod.POST)
    public void uploadMark(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();

        XSSFWorkbook workbook = new XSSFWorkbook(is);
        XSSFSheet spreadsheet = workbook.getSheetAt(0);

        XSSFRow row;
        int rollNumberIndex = 1;
        int studentNameIndex = 2;
        int excelDataIndex = 3;
        List<StudentEntity> students = new ArrayList<StudentEntity>();

        for (int rowIndex = excelDataIndex; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
            row = spreadsheet.getRow(rowIndex);
            if (row != null) {
                StudentEntity student = new StudentEntity();
                Cell rollNumberCell = row.getCell(rollNumberIndex);
                Cell studentNameCell = row.getCell(studentNameIndex);
                if (rollNumberCell != null) {
                    System.out.println(rollNumberCell.getStringCellValue() + " \t\t ");
                    student.setRollNumber(rollNumberCell.getStringCellValue());
                }
                if (studentNameCell != null) {
                    System.out.println(studentNameCell.getStringCellValue());
                    student.setFullName(studentNameCell.getStringCellValue());
                }

                if (student.getRollNumber() != null) {
                    students.add(student);
                }
            }
        }
        System.out.println("Danh sach sinh vien: " + students.size());
    }
}
