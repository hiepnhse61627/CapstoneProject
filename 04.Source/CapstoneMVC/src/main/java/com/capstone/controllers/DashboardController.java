package com.capstone.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {
    @RequestMapping("/dashboard")
    public ModelAndView Index() {
        ModelAndView model = new ModelAndView("Dashboard");

        return model;
    }


}
