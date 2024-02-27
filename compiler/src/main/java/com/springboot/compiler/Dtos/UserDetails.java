package com.springboot.compiler.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetails {
    private String username;
    private String firstName;
    private String lastName;
    private String favLanguage;
    private String email;
    private String gender;
    private long totalSubmissions;
    private long successSubmissionsCount;
}
