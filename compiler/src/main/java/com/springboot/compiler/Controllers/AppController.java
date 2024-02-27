package com.springboot.compiler.Controllers;

import java.util.HashSet;
import java.util.Set;

import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springboot.compiler.Dtos.LoginRequest;
import com.springboot.compiler.Dtos.LoginResponse;
import com.springboot.compiler.Dtos.UserRegistration;
import com.springboot.compiler.Services.AppService;
import com.springboot.compiler.Services.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@ResponseBody
@RequestMapping("/app")
@RequiredArgsConstructor
@Tag(name = "App Controller",description = "Endpoints for user login, logout, registration")
@Log4j2
public class AppController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppService appService;

    private final CacheManager chacheManager;


    @PostMapping("/login")
    @Operation(description = "Authenticate user and generate JWT token")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description  = "Login successful")
        }
    )
    public LoginResponse login(HttpServletResponse response,@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        if(authentication.isAuthenticated()){
            //this we generate jwt token and return response
            Set<SimpleGrantedAuthority> authorities=new HashSet<>();
            for(GrantedAuthority aGrantedAuthority:authentication.getAuthorities()){
                authorities.add(new SimpleGrantedAuthority(aGrantedAuthority.getAuthority()));
            }
            return LoginResponse.builder().token(jwtService.generateToken(loginRequest.getUsername(),authorities)).build();
        }
        throw new UsernameNotFoundException("Invalid user request!");
    }

    @Operation(description = "New user registration")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegistration userRegistration){
        appService.registration(userRegistration);
        log.info("New user registred with username::"+userRegistration.getUsername());
        return ResponseEntity.ok().body("User registration success with username::"+userRegistration.getUsername());
    }

    @SuppressWarnings("null")
    @GetMapping("/logout")
    @Operation(description = "New user registration")
    public ResponseEntity<String> logout(@RequestHeader(value="Authorization",required = true)String token,@AuthenticationPrincipal UserDetails user){
        
        if(token.startsWith("Bearer ")){
            token=token.substring(7);
            chacheManager.getCache("InvalidTokens").put(token,1);
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token Requried");
        }
        return ResponseEntity.ok().body("Logout successfull");
    }
}