package com.capstone.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping("/next")
    public String Next() {
        return "SecondPage";
    }
}
