package com.capstone.services.customSecurity;

import com.capstone.entities.CredentialsEntity;
import com.capstone.entities.CredentialsRolesEntity;
import com.capstone.entities.DynamicMenuEntity;
import com.capstone.entities.RolesEntity;
import com.capstone.models.CustomUser;
import com.capstone.services.CredentialsRolesServiceImpl;
import com.capstone.services.CredentialsServiceImpl;
import com.capstone.services.DynamicMenuServiceImpl;
import com.capstone.services.RolesAuthorityServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


public class MySecurity {
    public static boolean hasPermission(String url) {

        DynamicMenuServiceImpl menuService = new DynamicMenuServiceImpl();
        RolesAuthorityServiceImpl authorityService = new RolesAuthorityServiceImpl();
        CredentialsRolesServiceImpl usersRolesService = new CredentialsRolesServiceImpl();


//        get authenticated user
        CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CredentialsEntity user = customUser.getUser();

        List<CredentialsRolesEntity> credentialsRolesList = usersRolesService.getCredentialsRolesByCredentialsId(user.getId());
        List<String> rolesList =
                credentialsRolesList.stream().map(q -> q.getRolesId().getName()).collect(Collectors.toList());

//        get menu by requested link
        DynamicMenuEntity menu = menuService.findDynamicMenuByLink(url);

        //check if user has permission to access requested url
        boolean isAllowed = false;
        for (String role : rolesList) {
            isAllowed = authorityService
                    .findRolesAuthorityByRoleIdAndMenuId(role, menu.getId());
            if(isAllowed){
                break;
            }
        }

        return isAllowed;
    }
}
