package com.capstone.controllers;

import com.capstone.entities.CredentialsEntity;
import com.capstone.models.CustomCredentialsEntity;
import com.capstone.models.CustomUser;
import com.capstone.services.CredentialsServiceImpl;
import com.capstone.services.ICredentialsService;
import com.capstone.services.IStudentService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private CustomUser getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser user = (CustomUser) authentication.getPrincipal();
        return user;
    }

    @RequestMapping("/")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("Profile");
        view.addObject("title", "Profile");

        CustomUser principal = getPrincipal();
        ICredentialsService credentialsService = new CredentialsServiceImpl();
        CredentialsEntity entity = credentialsService.findCredential(principal.getUsername());

        view.addObject("user", entity);

        return view;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject Edit(@RequestBody CustomCredentialsEntity cred) {
        JsonObject data = new JsonObject();

        try {
            ICredentialsService service = new CredentialsServiceImpl();
            if (cred.getId() != null) {
                CredentialsEntity c = service.findCredentialById(cred.getId());
                c.setFullname(cred.getFullname());
                c.setRole(cred.getRole());
                c.setStudentRollNumber(cred.getStudentRollNumber());

                // check password
                if (cred.getPassword() != null && !cred.getPassword().isEmpty()) {
                    if (BCrypt.checkpw(cred.getPassword(), c.getPassword())) {
                        PasswordEncoder encoder = new BCryptPasswordEncoder();
                        String encodedPass = encoder.encode(cred.getNewPassword());
                        c.setPassword(encodedPass);
                    } else {
                        data.addProperty("success", false);
                        data.addProperty("msg", "Password cũ không đúng");
                        return data;
                    }
                }

                service.SaveCredential(c, false);

                Authentication auth = new UsernamePasswordAuthenticationToken(new CustomUser(c.getUsername(), c.getPassword(), getGrantedAuthorities(c), c), c.getPassword(), getGrantedAuthorities(c));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            data.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    private List<GrantedAuthority> getGrantedAuthorities(CredentialsEntity user){
        List<GrantedAuthority> authorities = new ArrayList<>();
        String[] roles = user.getRole().split(",");
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.trim()));
        }
        return authorities;
    }
}
