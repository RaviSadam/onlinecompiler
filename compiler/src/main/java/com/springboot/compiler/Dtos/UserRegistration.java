package com.springboot.compiler.Dtos;


import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegistration {
    @Size(min = 5,max = 10,message = "username contains 5 to 10 characters only")
    @NotBlank(message = "Blank username not allowed")
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9]*",message = "invalid username")
    private String username;

    @NotBlank(message = "First name of user not blank")
    @Size(min=3,max = 50,message ="Firstname contains 5 to 50 characters only")
    @Pattern(regexp = "[a-zA-Z]+(\\s+[a-zA-Z]+)?",message = "First name should contains Alphabets only")
    @JsonAlias({"first_name","firstname","firstName"})
    private String firstName;
    
    @Pattern(regexp = "[a-zA-Z]+(\\s+[a-zA-Z]+)?",message = "Last name should contains Alphabets only")
    @JsonAlias({"last_name","lastname","lastName"})
    private String lastName;

    @NotBlank(message = "Blank password not allowed")
    @Size(min=8,max = 20,message ="Password length between 8 to 20 characters only")
    private String password;

    @Pattern(regexp = "[a-zA-Z]{4,6}$",message = "Invalid gender is given")
    private String gender;
    
    @Email(message = "Invalid Email is given")
    @NotBlank(message = "email requried")
    private String email;


    @Pattern(regexp = "[a-z]+",message = "only small letters are allowed")
    private String faviourateLanguage;

    private List<String> roles=new LinkedList<>();
}
