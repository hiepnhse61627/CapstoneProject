package com.capstone.controllers;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class HomeController {

    private int progress = 0;

    @RequestMapping(value = {"/", "/dashboard"})
    public String Index(ModelMap map) {
        map.addAttribute("title", "Dashboard");
        return "Dashboard";
    }
}
