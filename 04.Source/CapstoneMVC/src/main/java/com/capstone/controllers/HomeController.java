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

    @RequestMapping("/next")
    public String Next() throws IOException {
        return "Search";
    }

    @RequestMapping("/status")
    @ResponseBody
    public int getProgress() {
        return progress;
    }

    @RequestMapping("/runstatus")
    @ResponseBody
    public JsonObject Run() {
        try {
            Thread t = new Thread(() -> {
                progress = 0;
                while(progress < 100) {
                    try {
                        progress++;
                        System.out.print(progress + ",");
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("success", "true");
        return obj;
    }
}
