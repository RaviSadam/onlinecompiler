package com.springboot.compiler.Repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.compiler.Models.User;
import com.springboot.compiler.Projections.UserAuthProjection;
import com.springboot.compiler.Projections.UserDetailsProjection;

import jakarta.transaction.Transactional;


public interface UserRepository extends JpaRepository<User,Integer> {

    //query to get the password and roles of user
    @Query("SELECT u.password AS password, GROUP_CONCAT(r.rolename) AS rolename FROM User u JOIN u.roles r WHERE u.username=:username AND u.makrForDeletion=0 GROUP BY u.username")
    public UserAuthProjection getUserForAuth(@Param("username")String username);


    //quert to mark the user for deletion
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.makrForDeletion=1 WHERE u.username=:username")
    public void makrForDeletion(@Param("username") String username);

    //count the no.of users based on username and email
    public long countByUsernameOrEmail(String username,String email);

    //delete all users who are marked for deletion
    @Query(value="CALL deleteUser()",nativeQuery = true)
    public List<String> deleteUser();

    @Query(value = "CALL getMarkedUsers()",nativeQuery = true)
    public List<String> getMarkedUsers();

    @Query(value="UPDATE users u  SET u.first_name=IF(:firstName IS NOT NULL,:firstName,u.first_name), u.last_name=IF(:lastName IS NOT NULL,:lastName,u.last_name), u.faviourate_language= IF(:favLanguage IS NOT NULL,:favLanguage,u.faviourate_language), u.password=IF(:password IS NOT NULL,:password,u.password),u.gender=IF(:gender IS NOT NULL,:gender,u.gender) WHERE u.username=:username",nativeQuery=true)
    public void updateUser(@Param("firstName")String firstName,@Param("lastName")String lastName, @Param("favLanguage") String favLanguage,@Param("password") String password, @Param("gender") String gender);
    

    @Query(value="SELECT u.username AS username, u.firstName AS firstName, u.lastName AS lastName, u.gender AS gender, u.email AS email,u.favLanguage AS favLanguage, SUM(IF(s.status='SUCCESS',1,0)) AS successSubmissionsCount, COUNT(s.username) AS totalSubmissions FROM User u JOIN u.submissions s WHERE u.username=:username")
    public UserDetailsProjection getUserDetails(@Param("username") String username);

}
