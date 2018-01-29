package com.capstone.services.customSecurity;

import com.capstone.entities.CredentialsEntity;
import com.capstone.entities.DynamicMenuEntity;
import com.capstone.models.CustomUser;
import com.capstone.services.CredentialsServiceImpl;
import com.capstone.services.DynamicMenuServiceImpl;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;
import org.springframework.stereotype.Component;


import javax.jws.WebMethod;
import javax.servlet.http.HttpServletRequest;

public class MyCustomMethodSecurityExpressionRoot
//        extends SecurityExpressionRoot
        extends WebSecurityExpressionRoot {

    public MyCustomMethodSecurityExpressionRoot(Authentication authentication, FilterInvocation fi) {
        super(authentication, fi);
    }

//    public boolean GGGGGG(HttpServletRequest request) {
//
//        CredentialsServiceImpl credentialsService = new CredentialsServiceImpl();
//        DynamicMenuServiceImpl menuService = new DynamicMenuServiceImpl();
//        CredentialsAuthorityServiceImpl authorityService = new CredentialsAuthorityServiceImpl();
//
//        //get requested link
//        String url = request.getRequestURL().toString();
//
////        get authenticated user
//        CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        CredentialsEntity user = customUser.getUser();
//
////        get menu by requested link
//        DynamicMenuEntity menu = menuService.findDynamicMenuByLink(url);
//
//        //check if user has permission to access requested url
//        boolean isAllowed = authorityService
//                .findCredentialsAuthorityByDynamicMenuIdAndCredentialsId(user.getId(), menu.getId());
//
//        return isAllowed;
//    }


}
