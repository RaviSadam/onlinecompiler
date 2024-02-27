package com.springboot.compiler.WebFilters;


import java.io.IOException;

import org.springframework.cache.CacheManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.springboot.compiler.Models.User;
import com.springboot.compiler.Services.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter{
    private final JwtService jwtService;
    private final CacheManager cacheManager;

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHead=request.getHeader("Authorization");
        String token=null;
        String username=null;
        if(authHead!=null && authHead.startsWith("Bearer ")){
            token=authHead.substring(7);
            if(cacheManager.getCache("InvalidTokens").get(token)==null){
                try{
                    username=jwtService.extractUsername(token); 
                }
                catch(MalformedJwtException exception){
                    //ignore                    
                }
                catch(ExpiredJwtException exception){
                    //ignore
                }
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user=User.builder().username(username).authorities(jwtService.extractRoles(token)).build();
            UserPrincipalAuthenticationToken userPrincipalAuthenticationToken=new UserPrincipalAuthenticationToken(user);
            if(request.getServletPath().startsWith("/actuator")){
                if(user.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))){
                    SecurityContextHolder.getContext().setAuthentication(userPrincipalAuthenticationToken);
                }
            }
            else{
                SecurityContextHolder.getContext().setAuthentication(userPrincipalAuthenticationToken);
            }
        } 
        filterChain.doFilter(request, response);   
    }
}
