package com.capstone.controllers;

import com.capstone.entities.StudentEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Controller
public class StudentController {

    @RequestMapping("/create")
    public String Index() {
        return "CreateNewStudent";
    }

    @RequestMapping(value = "/createnew", method = RequestMethod.POST)
    @ResponseBody
    public StudentEntity CreateNewStudent(@RequestBody StudentEntity student) {
        System.out.println(student.getFullName());
        System.out.println(student.getRollNumber());
        return student;
    }
}
