package com.capstone.controllers;

import com.capstone.entities.Marks;
import com.capstone.entities.NewMark;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

@Controller
public class HomeController {
    @RequestMapping("/")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("Index");
        view.addObject("title", "Main");
        view.addObject("message", "Welcome!");

        return view;
    }

    @RequestMapping("/next")
    public String Next() {
        ExcelToOject();
        return "SecondPage";
    }

    public void ExcelToOject() {
        try {
            FileInputStream file = new FileInputStream(new File("C:\\Users\\huuth\\Desktop\\CapstoneProject.git\\trunk\\04.Source\\Export Mark_13.9.xlsx"));

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            ArrayList<NewMark> markList = new ArrayList<>();
            //I've Header and I'm ignoring header for that I've +1 in loop
            for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                NewMark e = new NewMark();
                Row ro = sheet.getRow(i);
                for (int j = ro.getFirstCellNum(); j <= ro.getLastCellNum(); j++) {
                    Cell ce = ro.getCell(j);
                    if (j == 0) {
                        //If you have Header in text It'll throw exception because it won't get NumericValue
                        e.setSemesterName(ce.getStringCellValue());
                    }
                    if (j == 1) {
                        e.setRollNumber(ce.getStringCellValue());
                    }
                    if (j == 2) {
                        e.setSubjectCode(ce.getStringCellValue());
                    }
                    if (j == 3) {
                        e.setClassName(ce.getStringCellValue());
                    }
                    if (j == 4) {
                        e.setAverageMark(ce.getNumericCellValue());
                    }
                    if (j == 5) {
                        e.setStatus(ce.getStringCellValue());
                    }
                }
                markList.add(e);
            }
            for (NewMark marks : markList) {
                System.out.println("Succeed on" + marks.getRollNumber());
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
