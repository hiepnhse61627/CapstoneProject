package com.capstone.services.customSecurity;

import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

public class MyCustomMethodSecurityExpressionHandler
//        extends DefaultMethodSecurityExpressionHandler
        extends DefaultWebSecurityExpressionHandler {

//    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    public MyCustomMethodSecurityExpressionHandler() {
        super();
    }

    //inject our MyCustomMethodSecurityExpressionRoot here

//    @Override
//    public StandardEvaluationContext createEvaluationContextInternal(Authentication auth, MethodInvocation mi) {
//        StandardEvaluationContext ctx = (StandardEvaluationContext) super.createEvaluationContextInternal(auth, mi);
//        ctx.setRootObject(new MyCustomMethodSecurityExpressionRoot(auth));
//        return ctx;
//    }


    @Override
    protected SecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, FilterInvocation fi) {
        WebSecurityExpressionRoot root = new MyCustomMethodSecurityExpressionRoot(authentication, fi);

        return root;
    }
}
