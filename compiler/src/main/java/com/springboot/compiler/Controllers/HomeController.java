package com.springboot.compiler.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@ResponseBody
@Tag(name="Home Controller")
public class HomeController {
    @RequestMapping("/")
    public ResponseEntity<String> home(){
        return ResponseEntity.ok().body("Welcome to Online Compiler. Visit /v3/api-docs to see all services");
    }
}
