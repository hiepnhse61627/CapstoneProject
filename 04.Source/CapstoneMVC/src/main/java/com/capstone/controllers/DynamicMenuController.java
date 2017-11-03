package com.capstone.controllers;

import com.capstone.entities.DynamicMenuEntity;
import com.capstone.services.DynamicMenuServiceImpl;
import com.capstone.services.IDynamicMenuService;
import com.google.gson.JsonObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("menu")
public class DynamicMenuController {
    IDynamicMenuService dynamicMenuService = new DynamicMenuServiceImpl();

//    @RequestMapping("/")
//    public ModelAndView AllMenuList() {
//        ModelAndView view = new ModelAndView();
//        view.addObject("title", "Menu List");
//
//        List<DynamicMenuEntity> menu = dynamicMenuService.getAllMenu().stream().collect(Collectors.toList());
//
//        view.addObject("menu", menu);
//        return view;
//    }


    @RequestMapping(value = "/loadMenuList")
    @Scope("session")
    @ResponseBody
    public JsonObject LoadStudentListAll(HttpSession session, @RequestParam("menu-filter") String filter) {
        JsonObject jsonObj = new JsonObject();
        List<DynamicMenuEntity> menu = dynamicMenuService.getAllMenu();
        for(DynamicMenuEntity items : menu){
            System.out.println(items.getFunctionName());
            System.out.println(items.getGroupName());
        }

        return jsonObj;
    }
}
