package com.capstone.services.customSecurity;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

//setup method security configuration
//this line can be set up in security.xml
//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class MyMethodSecurityConfig extends GlobalMethodSecurityConfiguration {
//
//
//    //user our MyCustomMethodSecurityExpressionHandler here
//    @Override
//    protected MethodSecurityExpressionHandler createExpressionHandler() {
//
//        MyCustomMethodSecurityExpressionHandler expressionHandler
//                = new MyCustomMethodSecurityExpressionHandler();
////        expressionHandler.setPermissionEvaluator(new MyCustomPermissionEvaluator());
//        return expressionHandler;
//    }
//}

