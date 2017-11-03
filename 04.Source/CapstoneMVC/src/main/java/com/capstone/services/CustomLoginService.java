package com.capstone.services;

import com.capstone.entities.CredentialsEntity;
import com.capstone.models.CustomUser;
import com.capstone.models.Ultilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

public class CustomLoginService implements UserDetailsService, ServletContextAware {

    private ServletContext context;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ICredentialsService service = new CredentialsServiceImpl();
        CredentialsEntity user = service.findCredential(username);

        if(user == null){
            System.out.println("User not found");
            throw new UsernameNotFoundException("Username not found");
        } else {
            System.out.println("User: " + user.getUsername());
            Ultilities.GetMenu(context, user);
        }

        return new CustomUser(user.getUsername(), user.getPassword(), getGrantedAuthorities(user), user);
    }

    private List<GrantedAuthority> getGrantedAuthorities(CredentialsEntity user){
        List<GrantedAuthority> authorities = new ArrayList<>();
        String[] roles = user.getRole().split(",");
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.trim()));
        }
        return authorities;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        context = servletContext;
    }
}