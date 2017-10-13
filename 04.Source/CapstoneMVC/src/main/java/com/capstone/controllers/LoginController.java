package com.capstone.controllers;

import com.capstone.entities.CredentialsEntity;
import com.capstone.models.Logger;
import com.capstone.services.CredentialsServiceImpl;
import com.capstone.services.ICredentialsService;
import com.google.gson.JsonObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @RequestMapping("/favicon.ico")
    public String Redirect() {
        return "redirect:/";
    }

    @RequestMapping("/register")
    public String Register() {
        return "Register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject Register(@RequestBody CredentialsEntity user) {
        ICredentialsService service = new CredentialsServiceImpl();
        JsonObject obj = new JsonObject();
        try {
            CredentialsEntity entity = service.findCredential(user.getUsername());
            if (entity != null) {
                obj.addProperty("success", false);
                obj.addProperty("msg", "User exist!");
                return obj;
            }

            PasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPass = encoder.encode(user.getPassword());
            System.out.println("New password: " + encodedPass);
            user.setPassword(encodedPass);
            service.CreateCredentiall(user);

            obj.addProperty("success", true);
        } catch (Exception e) {
            Logger.writeLog(e);

            obj.addProperty("success", false);
            obj.addProperty("msg", e.getMessage());
        }

        return obj;
    }

    @RequestMapping("/deny")
    public String Deny() {
        return "Deny";
    }

    @RequestMapping(value = "/login")
    public String Login() {
        return "Login";
    }

    @RequestMapping(value="/logout")
    public String Logout (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }
}
