package com.capstone.controllers;

import com.capstone.entities.DynamicMenuEntity;
import com.capstone.services.DynamicMenuServiceImpl;
import com.capstone.services.IDynamicMenuService;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String Index() {
        ModelAndView view = new ModelAndView("Dashboard");
        view.addObject("title", "Dashboard");

        return "Dashboard";
    }
}
