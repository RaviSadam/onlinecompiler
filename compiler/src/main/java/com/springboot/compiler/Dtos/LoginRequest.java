package com.springboot.compiler.Dtos;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @JsonAlias({"username","user_name"})
    @NotBlank(message = "username requried")
    @Size(min = 5,max = 10,message = "username conains 5 to 10 characters only")
    private String username;
    
    @NotBlank(message = "password requried")
    @Size(min=8,max=20,message = "password length between 8 to 20 characters only")
    private String password;
}
