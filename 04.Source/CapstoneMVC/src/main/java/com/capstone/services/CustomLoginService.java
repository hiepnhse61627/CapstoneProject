package com.capstone.services;

import com.capstone.entities.CredentialsEntity;
import com.capstone.models.CustomUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class CustomLoginService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ICredentialsService service = new CredentialsServiceImpl();
        CredentialsEntity user = service.findCredential(username);

        if(user == null){
            System.out.println("User not found");
            throw new UsernameNotFoundException("Username not found");
        } else {
            System.out.println("User: " + user.getUsername());
        }

        return new CustomUser(user.getUsername(), user.getPassword(), getGrantedAuthorities(user), user.getFullname(),user.getPicture(), user.getStudentRollNumber());
    }

    private List<GrantedAuthority> getGrantedAuthorities(CredentialsEntity user){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return authorities;
    }

}