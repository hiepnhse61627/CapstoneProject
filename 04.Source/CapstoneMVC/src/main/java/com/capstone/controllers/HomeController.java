package com.capstone.controllers;

import com.capstone.entities.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    @RequestMapping("/")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("Index");
        view.addObject("title", "Main");
        view.addObject("message", "Welcome!");

        return view;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView Search() {
        ModelAndView view = new ModelAndView("Search");
        view.addObject("title", "Search");

        return view;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ModelAndView SearchByRollNumber(@RequestParam String txtSearch) {
        ModelAndView view = new ModelAndView("Search");
        view.addObject("title", "Search");
        Student stu = new Student();
        stu.setFirstName(txtSearch);
        view.addObject("student", stu);

        return view;
    }
}
