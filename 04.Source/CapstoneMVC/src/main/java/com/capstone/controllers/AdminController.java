package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.*;
import com.capstone.entities.CredentialsEntity;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.RealSemesterEntity;
import com.capstone.entities.RolesEntity;
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

    // home page of admin
    @RequestMapping("/index")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("AdminHomePage");
        view.addObject("title", "Tài khoản");
        return view;
    }

    // simulate change semester page
    @RequestMapping("/change")
    public ModelAndView ChangeSemester() {
        ModelAndView view = new ModelAndView("AdminChangeSemesterTemporary");
        view.addObject("title", "Set semester");
        view.addObject("semesters", Global.getSortedList());
        view.addObject("temporarySemester", Global.getTemporarySemester().getId());
        view.addObject("currentSemester", Global.getCurrentSemester().getId());
        return view;
    }

    // get all the users account data then search
    @RequestMapping(value = "/getUsers")
    @ResponseBody
    public JsonObject GetUsers(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();

        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
        String sSearch = params.get("sSearch").trim();

        try {
            ICredentialsService credentialsService = new CredentialsServiceImpl();
            ICredentialsRolesService credentialsRolesService = new CredentialsRolesServiceImpl();

            List<CredentialsEntity> userList = credentialsService.getAllCredentials();
            List<List<String>> list = new ArrayList<>();
            if (!userList.isEmpty()) {
                for (CredentialsEntity u : userList) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(u.getPicture() == null || u.getPicture().isEmpty() ? "N/A" : u.getPicture());
                    tmp.add(u.getUsername());
                    tmp.add(u.getFullname() == null || u.getFullname().isEmpty() ? "N/A" : u.getFullname());
                    tmp.add(u.getEmail() == null || u.getEmail().isEmpty() ? "N/A" : u.getEmail());

                    String role = "";
                    List<CredentialsRolesEntity> roleList = credentialsRolesService.getCredentialsRolesByCredentialsId(u.getId());
                    if (roleList == null || roleList.size() == 0) {
                        role = "N/A";
                    } else {
                        for (int i = 0; i < roleList.size(); ++i) {
                            role += roleList.get(i).getRolesId().getId();
                            if (i != roleList.size() - 1) {
                                role += "<br>";
                            }
                        }
                    }
                    tmp.add(role);

                    tmp.add(u.getId().toString());
                    list.add(tmp);
                }
            }

            List<List<String>> searchList = list.stream().filter(u ->
                    Ultilities.containsIgnoreCase(u.get(1), sSearch)
                            || Ultilities.containsIgnoreCase(u.get(2), sSearch)
                            || Ultilities.containsIgnoreCase(u.get(3), sSearch))
                    .collect(Collectors.toList());
            List<List<String>> displayList = searchList.stream().skip(iDisplayStart).limit(iDisplayLength)
                    .collect(Collectors.toList());

            JsonArray result = (JsonArray) new Gson().toJsonTree(displayList);

            data.addProperty("iTotalRecords", list.size());
            data.addProperty("iTotalDisplayRecords", searchList.size());
            data.add("aaData", result);
            data.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    // edit user details page
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject ShowEdit(@RequestParam int userId) {
        ICredentialsService credentialsService = new CredentialsServiceImpl();
        ICredentialsRolesService credentialsRolesService = new CredentialsRolesServiceImpl();
        JsonObject data = new JsonObject();

        try {
            CredentialsEntity user = credentialsService.findCredentialById(userId);
            List<CredentialsRolesEntity> userRolesMappingList = credentialsRolesService
                    .getCredentialsRolesByCredentialsId(user.getId());

            List<String> roles = new ArrayList<>();
            for (CredentialsRolesEntity mapping : userRolesMappingList) {
                roles.add(mapping.getRolesId().getId());
            }

            CredentialsModel model = new CredentialsModel();
            model.setId(user.getId());
            model.setUsername(user.getUsername());
            model.setPassword(user.getPassword());
            model.setFullname(user.getFullname());
            model.setPicture(user.getPicture());
            model.setEmail(user.getEmail());
            model.setRoles(roles);

            JsonObject userData = (JsonObject) new Gson().toJsonTree(model, CredentialsModel.class);

            data.addProperty("success", true);
            data.add("data", userData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    // trigger when user click on temporary semester radio button
    @RequestMapping(value = "/changesemster", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject SetTemporarySemester(@RequestParam int semesterId) {
        JsonObject data = new JsonObject();

        try {
            IRealSemesterService service = new RealSemesterServiceImpl();
            RealSemesterEntity tem = service.findSemesterById(semesterId);
            Global.setTemporarySemester(tem);
            System.out.println("Temporary " + Global.getTemporarySemester().getSemester());
            data.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    // trigger when user click on current semester radio button
    @RequestMapping(value = "/changecurrentsemster", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject SetCurrentSemester(@RequestParam int semesterId) {
        JsonObject data = new JsonObject();

        try {
            IRealSemesterService service = new RealSemesterServiceImpl();
            RealSemesterEntity tem = service.findSemesterById(semesterId);
            Global.setCurrentSemester(tem);
            System.out.println("Current " + Global.getCurrentSemester().getSemester());
            data.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    // save new user details
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject Save(@RequestBody CredentialsModel cred) {
        JsonObject data = new JsonObject();

        try {
            ICredentialsService credentialsService = new CredentialsServiceImpl();
            ICredentialsRolesService credentialsRolesService = new CredentialsRolesServiceImpl();

            if (cred.getId() != null) {
                CredentialsEntity c = credentialsService.findCredentialById(cred.getId());
                c.setUsername(cred.getUsername());
                c.setFullname(cred.getFullname());
                c.setPicture(cred.getPicture());

                if (cred.getPassword() != null && !cred.getPassword().isEmpty()) {
                    PasswordEncoder encoder = new BCryptPasswordEncoder();
                    String encodedPass = encoder.encode(cred.getPassword());
                    c.setPassword(encodedPass);
                }
                credentialsService.SaveCredential(c, false);

                List<CredentialsRolesEntity> roleList = credentialsRolesService.getCredentialsRolesByCredentialsId(cred.getId());
                // Create new roles
                for (String currentRole : cred.getRoles()) {
                    boolean exist = false;
                    for (CredentialsRolesEntity role : roleList) {
                        if (role.getRolesId().getId().equals(currentRole)) {
                            exist = true;
                            break;
                        }
                    }

                    if (!exist) {
                        RolesEntity newRole = new RolesEntity();
                        newRole.setId(currentRole);

                        CredentialsRolesEntity cr = new CredentialsRolesEntity();
                        cr.setCredentialsId(c);
                        cr.setRolesId(newRole);

                        credentialsRolesService.createCredentialRoles(cr);
                    }
                }

                for (CredentialsRolesEntity currentRole : roleList) {
                    boolean exist = false;
                    for (String r : cred.getRoles()) {
                        if (currentRole.getRolesId().getId().equals(r)) {
                            exist = true;
                            break;
                        }
                    }

                    if (!exist) {
                        credentialsRolesService.deleteCredentialRoles(currentRole);
                    }
                }
//                Authentication auth = new UsernamePasswordAuthenticationToken(new CustomUser(c.getUsername(), c.getPassword(), getGrantedAuthorities(c), c), c.getPassword(), getGrantedAuthorities(c));
//                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            data.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @RequestMapping("/manageRoles")
    public ModelAndView manageRoles() {
        ModelAndView mav = new ModelAndView("CreateRolesPage");
        mav.addObject("title", "Tạo mới chức vụ");
        return mav;
    }

//    @RequestMapping("/createNewRole")
//    public JsonObject createRoles(@RequestParam String newRole){
//
//
//        return ;
//    }



    // get all user roles to a list
    private List<GrantedAuthority> getGrantedAuthorities(CredentialsEntity user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        String[] roles = user.getRole().split(",");
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.trim()));
        }
        return authorities;
    }

    @RequestMapping("/manageRolesPage")
    public ModelAndView manageCredentialsRoles()
    {
        ModelAndView mav = new ModelAndView("CreateRolesPage");
        mav.addObject("title", "Quản lý chức vụ");
        return mav;
    }

    @RequestMapping(value = "/currentRolesData")
    @ResponseBody
    public JsonObject getCurrentRolesData(Map<String, String> params) {
        JsonObject data = new JsonObject();

        try {
            RolesServiceImpl rolesService = new RolesServiceImpl();

           List<RolesEntity> allRoles = rolesService.getAllRoles();
            List<List<String>> result = new ArrayList<>();
            int count =1;
            for (RolesEntity role :
                 allRoles) {
                List temporary = new ArrayList();
                //index
                temporary.add(count++);
                //roles
                temporary.add(role.getId());
                //add to result list
                result.add(temporary);
            }


            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            data.addProperty("iTotalRecords", result.size());
            data.addProperty("iTotalDisplayRecords", result.size());
            data.add("aaData", aaData);
            data.addProperty("sEcho", params.get("sEcho"));

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @RequestMapping(value = "/createNewRole", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goCreateNewRole(Map<String, String> params, @RequestParam("newRole") String newRole) {
        JsonObject data = new JsonObject();

        try {
            RolesServiceImpl rolesService = new RolesServiceImpl();

           if(newRole!= null && !newRole.isEmpty()){

               List<RolesEntity> existList = rolesService.getRolesById(newRole);
               if(!existList.isEmpty()){
                   data.addProperty("success", false);
                   data.addProperty("message", "Chức vụ đã tồn tại");
                   return data;
               }
               RolesEntity rolesEntity = new RolesEntity();
               rolesEntity.setId(newRole);
              boolean result = rolesService.createNewRole(rolesEntity);
              if(result){
                  data.addProperty("success", true);
                  data.addProperty("message", "Tạo chức vụ thành công");
              }else{
                  data.addProperty("success", false);
                  data.addProperty("message", "Đã xảy ra lỗi!");
              }
           }else{
               data.addProperty("success", false);
               data.addProperty("message", "Role can't be empty");
           }

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("success", false);
            data.addProperty("message", e.getMessage());
        }
        return data;
    }



}
