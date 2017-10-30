package com.capstone.controllers;

import com.capstone.entities.DynamicMenuEntity;
import com.capstone.services.DynamicMenuServiceImpl;
import com.capstone.services.IDynamicMenuService;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DynamicMenuController {
    IDynamicMenuService dynamicMenuService = new DynamicMenuServiceImpl();

//    @RequestMapping("/dashboard")
//    public ModelAndView AllMenuList() {
//        ModelAndView view = new ModelAndView("dashboard");
//        view.addObject("title", "Menu List");
//
//        List<DynamicMenuEntity> menuName = dynamicMenuService.getAllMenu().stream().filter(s -> !s.getFunctionName().contains("N/A")).collect(Collectors.toList());
//        List<DynamicMenuEntity> menuGroup = dynamicMenuService.getAllMenu().stream().filter(s -> !s.getFunctionGroup().contains("N/A")).collect(Collectors.toList());
//        List<DynamicMenuEntity> groupName = dynamicMenuService.getAllMenu().stream().filter(s -> !s.getGroupName().contains("N/A")).collect(Collectors.toList());
//        List<DynamicMenuEntity> link = dynamicMenuService.getAllMenu().stream().filter(s -> !s.getLink().contains("N/A")).collect(Collectors.toList());
//        for (DynamicMenuEntity list : menuName){
//            System.out.println(list.getFunctionName());
//        }
//
//        view.addObject("menuName", menuName);
//
//        view.addObject("menuGroup", menuGroup);
//        view.addObject("groupName", groupName);
//        view.addObject("link", link);
//
//        return view;
//    }
//
//    @RequestMapping(value = "/loadMenuList")
//    @ResponseBody
//    public JsonObject LoadStudentListAll() {
//        JsonObject jsonObj = new JsonObject();
//
//
//
//        return jsonObj;
//    }
}
