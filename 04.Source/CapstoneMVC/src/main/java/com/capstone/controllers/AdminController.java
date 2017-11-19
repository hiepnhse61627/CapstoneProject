package com.capstone.controllers;

import com.capstone.entities.CredentialsEntity;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.RealSemesterEntity;
import com.capstone.models.CustomCredentialsEntity;
import com.capstone.models.CustomUser;
import com.capstone.models.Global;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.security.access.method.P;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @RequestMapping("/index")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("AdminHomePage");
        view.addObject("title", "Tài khoản");
        return view;
    }

    @RequestMapping("/change")
    public ModelAndView ChangeSemester() {
        ModelAndView view = new ModelAndView("AdminChangeSemesterTemporary");
        view.addObject("title", "Set semester");
        view.addObject("semesters", Global.getSortedList());
        view.addObject("temporarySemester", Global.getTemporarySemester().getId());
        return view;
    }

    @RequestMapping(value = "/getUsers")
    @ResponseBody
    public JsonObject GetUsers(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();

        try {
            ICredentialsService credentialsService = new CredentialsServiceImpl();
            List<CredentialsEntity> list = credentialsService.getAllCredentials();
            List<List<String>> parent = new ArrayList<>();
            if (!list.isEmpty()) {
                list.forEach(m -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(m.getUsername());
                    tmp.add(m.getPicture() == null ? "N/A" : m.getPicture());
                    tmp.add(m.getEmail() == null ? "N/A" : m.getEmail());
                    tmp.add(m.getFullname() == null ? "N/A" : m.getFullname());
                    tmp.add(m.getRole() == null ? "N/A" : m.getRole());
                    tmp.add(m.getId().toString());
                    parent.add(tmp);
                });
            }

            List<List<String>> set2 = parent.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            JsonArray result = (JsonArray) new Gson().toJsonTree(set2);

            data.addProperty("iTotalRecords", parent.size());
            data.addProperty("iTotalDisplayRecords", parent.size());
            data.add("aaData", result);
            data.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject ShowEdit(@RequestParam int userId) {
        JsonObject data = new JsonObject();

        try {
            ICredentialsService credentialsService = new CredentialsServiceImpl();
            CredentialsEntity user = credentialsService.findCredentialById(userId);

            JsonObject result = (JsonObject) new Gson().toJsonTree(user, CredentialsEntity.class);

            data.addProperty("success", true);
            data.add("data", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @RequestMapping(value = "/changesemster", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject SetTemporarySemester(@RequestParam int semesterId) {
        JsonObject data = new JsonObject();

        try {
            IRealSemesterService service = new RealSemesterServiceImpl();
            RealSemesterEntity tem = service.findSemesterById(semesterId);
            Global.setTemporarySemester(tem);
            data.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject Save(@RequestBody CredentialsEntity cred) {
        JsonObject data = new JsonObject();

        try {
            ICredentialsService service = new CredentialsServiceImpl();
            if (cred.getId() != null) {
                CredentialsEntity c = service.findCredentialById(cred.getId());
                c.setUsername(cred.getUsername());
                c.setFullname(cred.getFullname());
                c.setRole(cred.getRole());
                c.setStudentRollNumber(cred.getStudentRollNumber());
                c.setPicture(cred.getPicture());

                if (cred.getPassword() != null && !cred.getPassword().isEmpty()) {
                    PasswordEncoder encoder = new BCryptPasswordEncoder();
                    String encodedPass = encoder.encode(cred.getPassword());
                    c.setPassword(encodedPass);
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
