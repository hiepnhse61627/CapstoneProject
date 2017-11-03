package com.capstone.services;

import com.capstone.entities.CredentialsEntity;
import com.capstone.entities.DynamicMenuEntity;
import com.capstone.models.CustomUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@WebListener
public class MyServletContextListener implements ServletContextListener {
    IDynamicMenuService dynamicMenuService = new DynamicMenuServiceImpl();

//    private CustomUser getPrincipal() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUser user = (CustomUser) authentication.getPrincipal();
//        return user;
//    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Initiated!");
//
//        CustomUser principal = getPrincipal();
//        ICredentialsService credentialsService = new CredentialsServiceImpl();
//        CredentialsEntity role = credentialsService.findCredential(principal.getUser().getRole());

        List<DynamicMenuEntity> menuNoFunctionGroup = dynamicMenuService.getAllMenu().stream().filter(s -> s.getFunctionGroup() == null
                ).collect(Collectors.toList());
        List<DynamicMenuEntity> menuImport = dynamicMenuService.getAllMenu().stream().filter(s -> s.getFunctionGroup() != null
                && s.getFunctionGroup().contains("Import") ).collect(Collectors.toList());
        List<DynamicMenuEntity> menuStatistic = dynamicMenuService.getAllMenu().stream().filter(s -> s.getFunctionGroup() != null
                && s.getFunctionGroup().contains("Statistic") ).collect(Collectors.toList());
        List<DynamicMenuEntity> menuManage = dynamicMenuService.getAllMenu().stream().filter(s -> s.getFunctionGroup() != null
                && s.getFunctionGroup().contains("Manage") ).collect(Collectors.toList());
        List<DynamicMenuEntity> menuChecking = dynamicMenuService.getAllMenu().stream().filter(s -> s.getFunctionGroup() != null
                && s.getFunctionGroup().contains("Checking") ).collect(Collectors.toList());

        ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.setAttribute("menuNoFunctionGroup", menuNoFunctionGroup);
        servletContext.setAttribute("menuImport", menuImport);
        servletContext.setAttribute("menuStatistic", menuStatistic);
        servletContext.setAttribute("menuManage", menuManage);
        servletContext.setAttribute("menuChecking", menuChecking);


    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
