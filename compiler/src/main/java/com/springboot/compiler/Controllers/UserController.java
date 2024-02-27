package com.springboot.compiler.Controllers;

import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springboot.compiler.Dtos.UserDetails;
import com.springboot.compiler.Dtos.UserUpdateDto;
import com.springboot.compiler.Models.User;
import com.springboot.compiler.Services.MainService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Controller
@ResponseBody
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name="User Controller",description = "Responsible for handling all user related requests")
public class UserController {
    private final MainService mainService;
    private final CacheManager cacheManager;


    @GetMapping("/")
    @Operation(description = "Gives the details of logged in user(requested user)")
    public ResponseEntity<UserDetails> getUserDetails(@AuthenticationPrincipal User user){
        return ResponseEntity.ok().body(mainService.getUserDetails(user.getUsername()));
    }

    @GetMapping("/{username}")
    @Operation(description = "Gives the details of user with given username")
    public ResponseEntity<UserDetails> getUserDetailsWithName(@PathVariable(value = "username",required = true)String username){
        return ResponseEntity.ok().body(mainService.getUserDetails(username));
    }

    @Operation(description = "Updates the User details such as firstName,lastName,gender,password,favourate language. Should give username and email can't modify. But you should specify the username and email to any valid username,email")
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@AuthenticationPrincipal User user,@RequestBody UserUpdateDto userUpdateDto){
        mainService.updateUser(user.getUsername(),userUpdateDto);
        return ResponseEntity.ok().body("User updated");
    }

    @SuppressWarnings("null")
    @Operation(description = "Schedule to delete current requested(logged in) user. User data compeletely deleted At 2 AM")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal User user,@RequestHeader(value="Authorization",required = true) String token){
        mainService.deleteUser(user.getUsername());
        if(token.startsWith("Bearer ")){
            token=token.substring(7);
            cacheManager.getCache("InvalidTokens").put(token,1);
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bearer Token Requried");
        }
        cacheManager.getCache("mainCache").evict(user.getUsername());
        return ResponseEntity.ok().body("We are marked for deletion. Your data compeletly deleted At 2 AM");
    }
}
