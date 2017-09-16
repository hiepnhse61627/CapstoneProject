package com.capstone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping(value = "/")
    public String home() {
        return "Index";
    }

    @RequestMapping("/next")
    public String Next() {
        return "SecondPage";
    }
}
