package com.capstone.controllers;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

@Controller
public class HomeController {

    private int progress = 0;

    @RequestMapping("/")
    public String Index() {
        return "Dashboard";
    }

    @RequestMapping("/next")
    public String Next() throws IOException {
        return "Search";
    }

    @RequestMapping("/status")
    @ResponseBody
    public int getProgress() {
        int tmp = progress;
        if (tmp > 100) tmp = 0;
        return tmp;
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
                        System.out.println(progress);
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
