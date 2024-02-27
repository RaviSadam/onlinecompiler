package com.springboot.compiler.Services;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.compiler.Dtos.UserRegistration;
import com.springboot.compiler.Models.Roles;
import com.springboot.compiler.Models.User;
import com.springboot.compiler.Repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registration(UserRegistration userRegistration) {
        long cnt=userRepository.countByUsernameOrEmail(userRegistration.getUsername(), userRegistration.getEmail());
        if(cnt!=0)
            throw new IllegalArgumentException("username or email already in use");
        if(userRegistration.getRoles().isEmpty())
            userRegistration.getRoles().add("ROLE_USER");

        Set<Roles> roles=userRegistration.getRoles().stream().map(rolename->this.getRoleObject(rolename)).collect(Collectors.toSet());
        User newUser=User.builder().username(userRegistration.getUsername()).firstName(userRegistration.getFirstName()).lastName(userRegistration.getLastName()).gender(userRegistration.getGender()).email(userRegistration.getEmail()).password(passwordEncoder.encode(userRegistration.getPassword())).createdDate(new Date(System.currentTimeMillis())).roles(roles).build();
        if(newUser!=null)
            userRepository.save(newUser);
        
        if(SecurityContextHolder.getContext().getAuthentication()!=null){
            SecurityContextHolder.clearContext();
        }
        //User object no more use full so making availabel for GC
        newUser=null;
        roles=null;
    }

    private Roles getRoleObject(String role){
        if(role.equals("ADMIN"))
            return Roles.builder().id(1).rolename(role).build();
        return Roles.builder().id(2).rolename(role).build();
    }
}
