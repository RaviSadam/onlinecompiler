package com.springboot.compiler.Services;


import java.util.HashSet;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.springboot.compiler.Models.User;
import com.springboot.compiler.Projections.UserAuthProjection;
import com.springboot.compiler.Repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{

    private final UserRepository userRepository;

    @Override
    @Cacheable(cacheNames ="mainCache",key="#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuthProjection userProjection=userRepository.getUserForAuth(username);
        if(userProjection==null)
            throw new UsernameNotFoundException("user not found");

        User user=new User();
        user.setPassword(userProjection.getPassword());
        user.setUsername(username);

        Set<SimpleGrantedAuthority> authorities=new HashSet<>();
        for(String role:userProjection.getRolename().split(" ")){
            authorities.add(new SimpleGrantedAuthority(role));
        }
        user.setAuthorities(authorities);
        return user;
    } 
}
