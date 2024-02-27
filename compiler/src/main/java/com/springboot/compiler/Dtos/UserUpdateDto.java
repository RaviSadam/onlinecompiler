package com.springboot.compiler.Dtos;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    
    @Pattern(regexp = "[a-zA-Z]+(\\s+[a-zA-Z]+)?",message = "First name should contains Alphabets only")
    @JsonAlias({"first_name","firstname","firstName"})
    private String firstName;
    
    @Pattern(regexp = "[a-zA-Z]+(\\s+[a-zA-Z]+)?",message = "Last name should contains Alphabets only")
    @JsonAlias({"last_name","lastname","lastName"})
    private String lastName;


    @Size(min=8,max = 20,message ="Password length between 8 to 20 characters only")
    private String password;

    @Pattern(regexp = "[a-zA-Z]{4,6}$",message = "Invalid gender is given")
    private String gender;


    @Pattern(regexp = "[a-z]+",message = "only small letters are allowed")
    private String faviourateLanguage;
    
}
