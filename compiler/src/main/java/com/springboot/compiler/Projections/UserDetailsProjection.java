package com.springboot.compiler.Projections;

public interface UserDetailsProjection {
    String getUsername();
    String getFirstName();
    String getLastName();
    String getFavLanguage();
    String getEmail();
    String getGender();
    long getTotalSubmissions();
    long getSuccessSubmissionsCount();
}
