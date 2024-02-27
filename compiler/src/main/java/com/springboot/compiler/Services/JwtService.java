package com.springboot.compiler.Services;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${security.secretkey}")
    public String SECRET;

    public String generateToken(String username,Set<SimpleGrantedAuthority> authorities) {
        return createToken(username,authorities);
    }
    private String createToken(String username,Set<SimpleGrantedAuthority> authorities) {
        Map<String,Set<SimpleGrantedAuthority>> claims=new HashMap<>();
        claims.put("roles",authorities); 
        return Jwts.builder() 
                .claim("roles",authorities.toString())
                .setSubject(username) 
                .setIssuedAt(new Date(System.currentTimeMillis())) 
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 10))) 
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact(); 
    } 
    private Key getSignKey() { 
        byte[] keyBytes= Decoders.BASE64.decode(SECRET); 
        return Keys.hmacShaKeyFor(keyBytes); 
    } 
    public String extractUsername(String token) { 
        return extractClaim(token, Claims::getSubject); 
    }
  
    public Date extractExpiration(String token) { 
        return extractClaim(token, Claims::getExpiration); 
    }
    public Set<SimpleGrantedAuthority> extractRoles(String token){
        Claims claims=extractAllClaims(token);
        String roles=(String)claims.get("roles");
        roles = roles.replace("[", "").replace("]", "");
        String[] roleNames = roles.split(",");
        Set<SimpleGrantedAuthority> authorities=new HashSet<>();
        for(String rName:roleNames){
            authorities.add(new SimpleGrantedAuthority(rName));
        }
        return authorities;
    }
  
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { 
        final Claims claims = extractAllClaims(token); 
        return claimsResolver.apply(claims); 
    } 
  
    private Claims extractAllClaims(String token) { 
        try{
            return Jwts 
                .parserBuilder() 
                .setSigningKey(getSignKey()) 
                .build() 
                .parseClaimsJws(token) 
                .getBody();
        }
        catch(MalformedJwtException exception){
            throw exception;
        } 
    } 
  
    private Boolean isTokenExpired(String token) { 
        return extractExpiration(token).before(new Date()); 
    } 

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        try{
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); 
        }
        catch(Exception ex){
            return false;
        }
    }    
}
