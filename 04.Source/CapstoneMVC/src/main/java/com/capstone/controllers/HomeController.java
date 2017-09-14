package com.capstone.controllers;

import com.capstone.entities.Marks;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    public String Next() throws IOException {
        ExcelToObject();
        return "SecondPage";
    }

    public void ExcelToObject() throws IOException {
        FileInputStream file = new FileInputStream(new File("C:\\Users\\THIENPHSE61426\\Desktop\\CapstoneProject.git\\trunk\\04.Source\\Export Mark_13.9.xlsx"));

        //Create Workbook instance holding reference to .xlsx file
        XSSFWorkbook workbook = new XSSFWorkbook(file);

        //Get first/desired sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0);

        ArrayList<Marks> marksList = new ArrayList();
        //I've Header and I'm ignoring header for that I've +1 in loop
        for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
            Marks m = new Marks();
            Row ro = sheet.getRow(i);
            for (int j = ro.getFirstCellNum(); j <= ro.getLastCellNum(); j++) {
                Cell ce = ro.getCell(j);
//                if (j == 0) {
//                    //If you have Header in text It'll throw exception because it won't get NumericValue
//                    m.setSemesterName(ce.getStringCellValue());
//                }
//                if (j == 1) {
//                    m.setRollNumber(ce.getStringCellValue());
//                }
//                if (j == 2) {
//                    m.setSubjectCode(ce.getStringCellValue());
//                }
            }
            marksList.add(m);
        }
//        for (NewEmployee emp : employeeList) {
//            System.out.println("ID:" + emp.getId() + " firstName:" + emp.getFirstName());
//        }
        System.out.println("List : " + marksList.toString());
        file.close();
    }
}
