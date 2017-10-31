package com.capstone.controllers;

import com.capstone.services.CredentialsServiceImpl;
import com.capstone.services.ICredentialsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @RequestMapping("/index")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("AdminPages/AdminHomePage");
        view.addObject("title", "Tài khoản");
        ICredentialsService service = new CredentialsServiceImpl();
        view.addObject("list", service.getAllCredentials());
        return view;
    }
}
