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

    @RequestMapping("/dashboard")
    public ModelAndView AllMenuList() {
        ModelAndView view = new ModelAndView("dashboard");
        view.addObject("title", "Menu List");
        HttpSession session

        List<DynamicMenuEntity> menuName = dynamicMenuService.getAllMenu();
        List<DynamicMenuEntity> menuGroup = dynamicMenuService.getAllMenu().stream().filter(s -> !s.getFunctionGroup().contains("N/A")).collect(Collectors.toList());
        List<DynamicMenuEntity> groupName = dynamicMenuService.getAllMenu().stream().filter(s -> !s.getGroupName().contains("N/A")).collect(Collectors.toList());
        List<DynamicMenuEntity> link = dynamicMenuService.getAllMenu().stream().filter(s -> !s.getLink().contains("N/A")).collect(Collectors.toList());


        view.addObject("menuName", menuName);

        view.addObject("menuGroup", menuGroup);
        view.addObject("groupName", groupName);
        view.addObject("link", link);

        return view;
    }


    @RequestMapping(value = "/loadMenuList")
    @Scope("session")
    @ResponseBody
    public JsonObject LoadStudentListAll(HttpSession session, @RequestParam("menu-filter") String filter) {
        JsonObject jsonObj = new JsonObject();
        List<DynamicMenuEntity> menu = dynamicMenuService.getAllMenu();
        session.setAttribute("menuList", menu);


        return jsonObj;
    }
}
