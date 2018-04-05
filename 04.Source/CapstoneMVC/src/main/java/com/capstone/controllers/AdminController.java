package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.entities.fapEntities.StudentAvgMarks;
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

import javax.servlet.http.HttpServletRequest;
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
    public ModelAndView Index(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //loggin user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView view = new ModelAndView("AdminHomePage");
        RolesServiceImpl rolesService = new RolesServiceImpl();
        List<RolesEntity> allRoles = rolesService.getAllRoles();

        view.addObject("roleList", allRoles);
        view.addObject("title", "Tài khoản");
        return view;
    }

    // simulate change semester page
    @RequestMapping("/change")
    public ModelAndView ChangeSemester(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //loggin user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

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
                    tmp.add(u.getPicture() == null || u.getPicture().isEmpty() ? "-" : u.getPicture());
                    tmp.add(u.getUsername());
                    tmp.add(u.getFullname() == null || u.getFullname().isEmpty() ? "-" : u.getFullname());
                    tmp.add(u.getEmail() == null || u.getEmail().isEmpty() ? "-" : u.getEmail());

                    String role = "";
                    List<CredentialsRolesEntity> roleList = credentialsRolesService.getCredentialsRolesByCredentialsId(u.getId());
                    if (roleList == null || roleList.size() == 0) {
                        role = "-";
                    } else {
                        for (int i = 0; i < roleList.size(); ++i) {
                            role += roleList.get(i).getRolesId().getName();
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
                roles.add(mapping.getRolesId().getName());
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

            Ultilities2ServiceImpl ult2 = new Ultilities2ServiceImpl();
            List<StudentAvgMarks> t =ult2.getFAPMarksBySemester("Fall2017");
            System.out.println(t.size());
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

        Ultilities.logUserAction("Change " + semesterId + " to temporary semester");
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

        Ultilities.logUserAction("Change " + semesterId + " to current semester");
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

        Ultilities.logUserAction("Change " + cred.getEmail() + " account detail");

        RolesServiceImpl rolesService = new RolesServiceImpl();
        List<RolesEntity> allRoles = rolesService.getAllRoles();
        try {
            ICredentialsService credentialsService = new CredentialsServiceImpl();
            ICredentialsRolesService credentialsRolesService = new CredentialsRolesServiceImpl();
            //validate data
            if (cred.getUsername().trim().isEmpty() || cred.getPassword().trim().isEmpty()
                    || cred.getEmail().trim().isEmpty()) {
                data.addProperty("success", false);
                data.addProperty("message", "Data thiếu");
                return data;
            }
            if (cred.getId() != null) {
                CredentialsEntity c = credentialsService.findCredentialById(cred.getId());
                if (!c.getUsername().equalsIgnoreCase(cred.getUsername())) {
                    CredentialsEntity credentialExist = credentialsService.findCredential(cred.getUsername());
                    if (credentialExist != null) {
                        data.addProperty("success", false);
                        data.addProperty("message", "Username đã tồn tại");
                        return data;
                    } else {
                        c.setUsername(cred.getUsername());
                    }
                }
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
                        if (role.getRolesId().getName().equals(currentRole)) {
                            exist = true;
                            break;
                        }
                    }

                    if (!exist) {
                        RolesEntity newRole = allRoles.stream()
                                .filter(q -> q.getName().equalsIgnoreCase(currentRole)).findFirst().orElse(null);

                        CredentialsRolesEntity cr = new CredentialsRolesEntity();
                        cr.setCredentialsId(c);
                        cr.setRolesId(newRole);

                        credentialsRolesService.createCredentialRoles(cr);
                    }
                }

                // Delete old roles
                for (CredentialsRolesEntity currentRole : roleList) {
                    boolean exist = false;
                    for (String r : cred.getRoles()) {
                        if (currentRole.getRolesId().getName().equals(r)) {
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

    // create credential
    @RequestMapping(value = "/createNewCredential", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goCreateCredential(@RequestBody CredentialsModel cred) {
        JsonObject data = new JsonObject();

        Ultilities.logUserAction("Create " + cred.getEmail() + " account ");

        RolesServiceImpl rolesService = new RolesServiceImpl();
        List<RolesEntity> allRoles = rolesService.getAllRoles();
        try {
            ICredentialsService credentialsService = new CredentialsServiceImpl();
            ICredentialsRolesService credentialsRolesService = new CredentialsRolesServiceImpl();
            if (cred.getUsername().trim().isEmpty() || cred.getEmail().trim().isEmpty()) {
                data.addProperty("success", false);
                data.addProperty("message", "Data không được rỗng");
                return data;
            }
            CredentialsEntity credentialExist = credentialsService.findCredential(cred.getUsername());
            if (credentialExist == null) {

                CredentialsEntity c = new CredentialsEntity();
                c.setUsername(cred.getUsername());
                c.setFullname(cred.getFullname());
                if (!cred.getPicture().trim().isEmpty()) {
                    c.setPicture(cred.getPicture());
                } else {
                    c.setPicture(null);
                }
                c.setEmail(cred.getEmail());

                if (cred.getPassword() != null && !cred.getPassword().isEmpty()) {
                    PasswordEncoder encoder = new BCryptPasswordEncoder();
                    String encodedPass = encoder.encode(cred.getPassword());
                    c.setPassword(encodedPass);
                }
                credentialsService.SaveCredential(c, true);

                // Create new roles
                if(cred.getRoles() != null){
                    for (String currentRole : cred.getRoles()) {

                        RolesEntity newRole = allRoles.stream()
                                .filter(q -> q.getName().equalsIgnoreCase(currentRole)).findFirst().orElse(null);

                        CredentialsRolesEntity cr = new CredentialsRolesEntity();
                        cr.setCredentialsId(c);
                        cr.setRolesId(newRole);

                        credentialsRolesService.createCredentialRoles(cr);
                    }
                }


//                Authentication auth = new UsernamePasswordAuthenticationToken(new CustomUser(c.getUsername(), c.getPassword(), getGrantedAuthorities(c), c), c.getPassword(), getGrantedAuthorities(c));
//                SecurityContextHolder.getContext().setAuthentication(auth);
                data.addProperty("success", true);
                data.addProperty("message", "Tạo tài khoản thành công");
            } else {
                data.addProperty("success", false);
                data.addProperty("message", "Username đã tồn tại");
            }

        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("success", false);
            data.addProperty("message", e.getMessage());
        }

        return data;
    }

    //delete credentials
    @RequestMapping(value = "/deleteExistCredential", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goDeleteExistCredential(Map<String, String> params, @RequestParam("credentialId") int credId) {
        JsonObject data = new JsonObject();


        try {
            CredentialsRolesServiceImpl credentialsRolesService = new CredentialsRolesServiceImpl();
            CredentialsServiceImpl credentialsService = new CredentialsServiceImpl();
            //xóa tất cả những roles liên quan đến account này
            List<CredentialsRolesEntity> involeList = credentialsRolesService.getCredentialsRolesByCredentialsId(credId);
            for (CredentialsRolesEntity item : involeList) {
                credentialsRolesService.deleteCredentialRoles(item);
            }
            CredentialsEntity deleteCredential = credentialsService.findCredentialById(credId);

            Ultilities.logUserAction("Delete " + deleteCredential.getEmail() + " account");

            boolean checkDelete = credentialsService.deleteCredential(credId);
            if (checkDelete) {
                data.addProperty("success", true);
                data.addProperty("message", "Xóa tài khoản thành công");
            } else {
                data.addProperty("success", false);
                data.addProperty("message", "Tài khoản không tồn tại");
            }


        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("success", false);
            data.addProperty("message", e.getMessage());
        }
        return data;
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
    public ModelAndView goManageRolesPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //loggin user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

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
            int count = 1;
            for (RolesEntity role :
                    allRoles) {
                List temporary = new ArrayList();
                //index
                temporary.add(count++);
                //roles
                temporary.add(role.getName());
                //id
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

        Ultilities.logUserAction("Create " + newRole + " as new Role");
        try {
            RolesServiceImpl rolesService = new RolesServiceImpl();

            if (newRole != null && !newRole.isEmpty()) {

                List<RolesEntity> existList = rolesService.getRolesByName(newRole);
                if (!existList.isEmpty()) {
                    data.addProperty("success", false);
                    data.addProperty("message", "Chức vụ đã tồn tại");
                    return data;
                }
                RolesEntity rolesEntity = new RolesEntity();
                rolesEntity.setName(newRole);
                boolean result = rolesService.createNewRole(rolesEntity);
                if (result) {
                    data.addProperty("success", true);
                    data.addProperty("message", "Tạo chức vụ thành công");
                } else {
                    data.addProperty("success", false);
                    data.addProperty("message", "Đã xảy ra lỗi!");
                }
            } else {
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

    @RequestMapping(value = "/updateExistRole", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goUpdateExistMenu(Map<String, String> params, @RequestParam(value = "roleId") int roleId,
                                        @RequestParam(value = "roleName") String roleName) {
        JsonObject data = new JsonObject();

        try {
            RolesServiceImpl rolesService = new RolesServiceImpl();
            RolesEntity rolesEntity = rolesService.findRolesEntity(roleId);
            if (rolesEntity != null) {
                Ultilities.logUserAction("Update " + rolesEntity.getName() + " - role to " + roleName + " -role");
                rolesEntity.setName(roleName);
                //update
                rolesService.updateRole(rolesEntity);
                data.addProperty("success", true);
                data.addProperty("message", "Cập nhật thành công!");
            } else {
                data.addProperty("success", false);
                data.addProperty("message", "Không tìm thấy chức vụ!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("success", false);
            data.addProperty("message", e.getMessage());
        }
        return data;
    }

    @RequestMapping("/manageMenuPage")
    public ModelAndView goManageMenuPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("ManageMenu");
        mav.addObject("title", "Quản lý menu");
        return mav;
    }

    @RequestMapping(value = "/currentMenuData")
    @ResponseBody
    public JsonObject getCurrentMenuData(Map<String, String> params) {
        JsonObject data = new JsonObject();

        try {
            DynamicMenuServiceImpl dynamicMenuService = new DynamicMenuServiceImpl();

            List<DynamicMenuEntity> allMenu = dynamicMenuService.getAllMenu();
            List<List<String>> result = new ArrayList<>();
            for (DynamicMenuEntity menu :
                    allMenu) {
                List temporary = new ArrayList();
                //get data
                temporary.add(menu.getFunctionGroup() == null ? "-" : menu.getFunctionGroup());
                temporary.add(menu.getFunctionName());
                temporary.add(menu.getGroupName() == null ? "-" : menu.getGroupName());
                temporary.add(menu.getLink());
                temporary.add(menu.getId());

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

    @RequestMapping(value = "/createNewMenu", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goCreateNewMenu(Map<String, String> params, @RequestParam("newFunctionGroup") String newFunctionGroup,
                                      @RequestParam("newMenuName") String newMenuName, @RequestParam("newGroupName") String newGroupName,
                                      @RequestParam("newLink") String newLink) {
        JsonObject data = new JsonObject();

        Ultilities.logUserAction("Create new menu " + newLink + " - " + newMenuName);
        try {
            DynamicMenuServiceImpl dynamicMenuService = new DynamicMenuServiceImpl();
            if (!newMenuName.isEmpty() && !newLink.isEmpty()) {
                DynamicMenuEntity exist = dynamicMenuService.findDynamicMenuByLink(newLink);
                if (exist != null) {
                    data.addProperty("success", false);
                    data.addProperty("message", "Link đã tồn tại!");
                    return data;
                } else {
                    if (newFunctionGroup.trim().isEmpty()) {
                        newFunctionGroup = null;
                    }
                    if (newGroupName.trim().isEmpty()) {
                        newGroupName = null;
                    }
                    DynamicMenuEntity newMenu = new DynamicMenuEntity();
                    newMenu.setFunctionGroup(newFunctionGroup);
                    newMenu.setFunctionName(newMenuName);
                    newMenu.setGroupName(newGroupName);
                    newMenu.setLink(newLink);

                    boolean result = dynamicMenuService.createNewMenu(newMenu);
                    if (result) {
                        data.addProperty("success", true);
                        data.addProperty("message", "Tạo menu thành công");
                    } else {
                        data.addProperty("success", false);
                        data.addProperty("message", "Đã xảy ra lỗi!");
                    }
                }
            } else {
                data.addProperty("success", false);
                data.addProperty("message", "Dữ liệu gửi bị trống!");
            }

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("success", false);
            data.addProperty("message", e.getMessage());
        }
        return data;
    }

    @RequestMapping(value = "/updateExistMenu", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goUpdateExistMenu(Map<String, String> params, @RequestParam(value = "e-functionGroup") String newFunctionGroup,
                                        @RequestParam("e-menuName") String newMenuName, @RequestParam(value = "e-groupName") String newGroupName,
                                        @RequestParam("e-Link") String newLink, @RequestParam("e-menuId") int menuId) {
        JsonObject data = new JsonObject();

        try {
            DynamicMenuServiceImpl dynamicMenuService = new DynamicMenuServiceImpl();
            if (!newMenuName.isEmpty() && !newLink.isEmpty()) {
                DynamicMenuEntity exist = dynamicMenuService.findDynamicMenuEntity(menuId);
                if (exist != null) {
                    if (newFunctionGroup.trim().isEmpty()) {
                        newFunctionGroup = null;
                    }
                    if (newGroupName.trim().isEmpty()) {
                        newGroupName = null;
                    }
                    Ultilities.logUserAction("Update " + exist.getFunctionName() + " - " + exist.getLink() + " menu");

                    exist.setFunctionGroup(newFunctionGroup);
                    exist.setFunctionName(newMenuName);
                    exist.setGroupName(newGroupName);
                    exist.setLink(newLink);

                    boolean result = dynamicMenuService.updateMenu(exist);
                    if (result) {
                        data.addProperty("success", true);
                        data.addProperty("message", "Tạo menu thành công");
                    } else {
                        data.addProperty("success", false);
                        data.addProperty("message", "Đã xảy ra lỗi!");
                    }
                } else {
                    data.addProperty("success", false);
                    data.addProperty("message", "Menu không tồn tại!");
                    return data;
                }
            } else {
                data.addProperty("success", false);
                data.addProperty("message", "Dữ liệu gửi bị trống!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("success", false);
            data.addProperty("message", e.getMessage());
        }
        return data;
    }


    @RequestMapping(value = "/deleteExistMenu", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goDeleteExistMenu(Map<String, String> params, @RequestParam("menuId") int menuId) {
        JsonObject data = new JsonObject();


        try {
            DynamicMenuServiceImpl dynamicMenuService = new DynamicMenuServiceImpl();
            RolesAuthorityServiceImpl rolesAuthorityService = new RolesAuthorityServiceImpl();
            DynamicMenuEntity menu = dynamicMenuService.findDynamicMenuEntity(menuId);

            //xóa tất cả những rolesAuthority liên quan đến menu
            List<RolesAuthorityEntity> involeList = rolesAuthorityService.findRolesAuthorityByMenuId(menuId);
            boolean checkDelete = rolesAuthorityService.deleteRolesAuthorityByIdList(involeList);

            Ultilities.logUserAction("Delete " + menu.getFunctionName() + " - " + menu.getLink() + " menu");

            if (checkDelete) {
                dynamicMenuService.deleteMenu(menu);

            } else {
                data.addProperty("success", false);
                data.addProperty("message", "Không xóa được những phân quyền liên quan!");
            }
            data.addProperty("success", true);
            data.addProperty("message", "Xóa menu thành công");

        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("success", false);
            data.addProperty("message", e.getMessage());
        }
        return data;
    }

    @RequestMapping("/manageRolesAuthorityPage")
    public ModelAndView goManageRolesAuthorityPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //loggin user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("AssignRolesAuthority");
        RolesServiceImpl rolesService = new RolesServiceImpl();
        List<RolesEntity> allRoles = rolesService.getAllRoles();
        RolesAuthorityServiceImpl rolesAuthorityService = new RolesAuthorityServiceImpl();

        DynamicMenuServiceImpl dynamicMenuService = new DynamicMenuServiceImpl();
        List<DynamicMenuEntity> allMenus = dynamicMenuService.getAllMenu();
        List<DynamicMenuEntity> noGroupName = allMenus.stream()
                .filter(q -> q.getGroupName() == null).collect(Collectors.toList());

        List<DynamicMenuEntity> haveGroupName = allMenus.stream()
                .filter(q -> q.getGroupName() != null).collect(Collectors.toList());
        List<GroupNameDynamicMenu> groupMenus = new ArrayList<>();
        for (DynamicMenuEntity menu : haveGroupName) {
            boolean exist = groupMenus.stream().anyMatch(q -> q.getGroupName().equalsIgnoreCase(menu.getGroupName()));
            if (!exist) {
                groupMenus.add(new GroupNameDynamicMenu(menu.getFunctionGroup(), menu.getGroupName()));
            }
        }


        mav.addObject("title", "Phân quyền chức vụ");
        mav.addObject("roleList", allRoles);
        mav.addObject("noGroupName", noGroupName);
        mav.addObject("haveGroupName", haveGroupName);
        mav.addObject("groupMenus", groupMenus);
        return mav;
    }

    //lấy tất cả menu mà roles này đang được truy cập
    @RequestMapping(value = "/getRoleAuthorityData", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goGetRoleAuthorityData(Map<String, String> params, @RequestParam("selectedRoleId") int selectedRoleId) {
        JsonObject data = new JsonObject();

        try {
            RolesAuthorityServiceImpl rolesAuthorityService = new RolesAuthorityServiceImpl();
            List<DynamicMenuEntity> menuList = rolesAuthorityService.findMenuByRoleId(selectedRoleId);
            List<Integer> menuIds = menuList.stream().map(q -> q.getId()).collect(Collectors.toList());

            JsonArray authorityArray = (JsonArray) new Gson().toJsonTree(menuIds);
            data.add("authorityArray", authorityArray);
            data.addProperty("success", true);
            data.addProperty("message", "Done get authority");

        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("success", false);
            data.addProperty("message", e.getMessage());
        }
        return data;
    }

    //uploadAuthority:
    @RequestMapping(value = "/updateRolesAuthority", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goUpdateRolesAuthority(Map<String, String> params,
                                             @RequestParam("allMenuIds") List<Integer> uploadAuthority,
                                             @RequestParam("selectedRoleId") int selectedRoleId) {
        JsonObject data = new JsonObject();


        try {
            DynamicMenuServiceImpl dynamicMenuService = new DynamicMenuServiceImpl();
            RolesAuthorityServiceImpl rolesAuthorityService = new RolesAuthorityServiceImpl();
            RolesServiceImpl rolesService = new RolesServiceImpl();
            List<DynamicMenuEntity> allMenus = dynamicMenuService.getAllMenu();
            RolesEntity selectedRole = rolesService.findRolesEntity(selectedRoleId);

            if (selectedRole == null) {
                data.addProperty("success", false);
                data.addProperty("message", "Không tìm thấy chức vụ được chọn");
                return data;
            }

            Ultilities.logUserAction("Update " + selectedRole.getName() + " - role authority");

            List<RolesAuthorityEntity> currentAuthority = rolesAuthorityService.findRolesAuthorityByRoleId(selectedRoleId);

            //lấy những trang menu được thêm quyền truy cập
            List<Integer> newAthority = uploadAuthority.stream()
                    .filter(q -> !currentAuthority.stream().anyMatch(c -> c.getMenuId().getId() == q))
                    .collect(Collectors.toList());

            //lấy những trang bị tước quyền truy cập
            List<RolesAuthorityEntity> removeAuthority = currentAuthority.stream()
                    .filter(q -> !uploadAuthority.stream().anyMatch(c -> c == q.getMenuId().getId()))
                    .collect(Collectors.toList());

            boolean checkRemove = false;
            //xóa quyền của những trang bị tước quyền truy cập
            if (!removeAuthority.isEmpty()) {
                checkRemove = rolesAuthorityService.deleteRolesAuthorityByIdList(removeAuthority);
            }

            //cấp quyền của những trang được thêm quyền truy cập
            if (!newAthority.isEmpty()) {
                for (Integer itemMenuId : newAthority) {
                    RolesAuthorityEntity newRolesAuthority = new RolesAuthorityEntity();
                    DynamicMenuEntity menuEntity = allMenus.stream().filter(q -> q.getId() == itemMenuId)
                            .findFirst().orElse(null);
                    if (menuEntity != null) {
                        //set attribute
                        newRolesAuthority.setMenuId(menuEntity);
                        newRolesAuthority.setRolesId(selectedRole);

                        //lưu xuống
                        rolesAuthorityService.createRolesAuthority(newRolesAuthority);
                    } else {
                        System.out.println("Can't find DynamicMenuId " + itemMenuId);
                        data.addProperty("success", false);
                        data.addProperty("message", "Không tìm thấy trang được chọn");
                        return data;
                    }
                }
            }

            data.addProperty("success", true);
            data.addProperty("message", "Cập nhật quyền thành công");
        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("success", false);
            data.addProperty("message", e.getMessage());
        }
        return data;
    }

}
