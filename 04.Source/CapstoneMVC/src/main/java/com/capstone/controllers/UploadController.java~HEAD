package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;


import org.apache.poi.xssf.usermodel.XSSFCell;
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
                XSSFCell rollNumberCell = row.getCell(rollNumberIndex);
                XSSFCell studentNameCell = row.getCell(studentNameIndex);
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

        XSSFSheet sheet = workbook.getSheetAt(0);
        List<MarksEntity> markList = new ArrayList<MarksEntity>();

        for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
            MarksEntity e = new MarksEntity();
            XSSFRow ro = sheet.getRow(i);
            for (int j = ro.getFirstCellNum(); j <= ro.getLastCellNum(); j++) {
                XSSFCell ce = ro.getCell(j);
                if (j == 0) {
                    //If you have Header in text It'll throw exception because it won't get NumericValue
                    if (e.getRealSemesterBySemesterId() != null){
                        e.setRealSemesterBySemesterId(e.getRealSemesterBySemesterId());
                    }
                }
                if(j == 1){
                    e.setRollNumber(ce.getStringCellValue());
                }
                if(j == 2){
                    e.setSubjectId(ce.getStringCellValue());
                }
                if(j == 4){
                    e.setAverageMark(ce.getNumericCellValue());
                }
                if(j == 5){
                    e.setStatus(ce.getStringCellValue());
                }
            }
            markList.add(e);
        }
        System.out.println("Bang Diem: " + markList.size());
    }

}
