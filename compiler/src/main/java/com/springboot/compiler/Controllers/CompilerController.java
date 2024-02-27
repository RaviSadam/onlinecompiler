package com.springboot.compiler.Controllers;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springboot.compiler.Dtos.AlgorithmRequest;
import com.springboot.compiler.Dtos.CompilationResponse;
import com.springboot.compiler.Dtos.CompileRequest;
import com.springboot.compiler.ExceptionHandlersAndAdvicer.RequestRateLimitException;
import com.springboot.compiler.Models.User;
import com.springboot.compiler.Services.MainService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@ResponseBody
@Tag(description = "It is responsible for handling compilation related requests", name = "Main Croller")
@RequestMapping("/compiler")
@RequiredArgsConstructor
public class CompilerController {

    private final MainService mainService;
    private final CacheManager cacheManager;
    private final SimpleDateFormat format;

    @SuppressWarnings("null")
    @Operation(description="Compilation request")
    @PostMapping("/")
    public ResponseEntity<CompilationResponse> compile(@AuthenticationPrincipal User user,@Valid @RequestBody CompileRequest compileRequest){
        Cache cache=cacheManager.getCache("CompilationRequests");
        if(cache.get(user.getUsername())!=null){
            throw new RequestRateLimitException("Time gap between two compiler requests should be more then 30 SEC. Previously compiler Accessed at"+cache.get(user.getUsername()));
        }
        ResponseEntity<CompilationResponse> result=ResponseEntity.status(HttpStatus.CREATED).body(mainService.compileAndRunCode(user.getUsername(),compileRequest));
        
        cache.put(user.getUsername(),format.format(new Date()));
        return result;
    }

    @Operation(description = "Gives all suporting languages")
    @GetMapping("/languages")
    public ResponseEntity<Set<String>> getLanguages(){
        return ResponseEntity.ok().body(mainService.getLanguages());
    }

    
    @Operation(description = "Gives the all the avaiable status of code")
    @GetMapping("/status")
    public ResponseEntity<List<String>> getStatus(){
        return ResponseEntity.ok().body(List.of("SUCCESS","RUN_TIME_ERROR","COMPILE_TIME_ERROR","WRONG_ANSWER","TIME_LIMIT_EXCEDDED","MEMORY_LIMIT_EXCEDDED"));
    }

    @SuppressWarnings("null")
    @Operation(description="Gives the AI generated algorithm for given code")
    @PostMapping("/algorithm")
    public ResponseEntity<String> getAlgorithm(@RequestBody AlgorithmRequest request,@AuthenticationPrincipal User user){
        Cache cache=cacheManager.getCache("CompilationRequests");
        if(cache.get(user.getUsername())!=null){
            throw new RequestRateLimitException("Time gap between two AI algorithm requests should be more then 30 SEC. Previously compiler Accessed at"+cache.get(user.getUsername()));
        }
        ResponseEntity<String> response=ResponseEntity.ok().body(mainService.getAlgorithm(request.getCode()));
        cache.put(user.getUsername(),format.format(new Date()));
        return response;
    }
}
