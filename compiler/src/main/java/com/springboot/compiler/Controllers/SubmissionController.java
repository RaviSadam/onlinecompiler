package com.springboot.compiler.Controllers;

import java.io.IOException;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springboot.compiler.Dtos.CompilationResponse;
import com.springboot.compiler.Dtos.SubmissionDetails;
import com.springboot.compiler.Dtos.UserSubmissionDetails;
import com.springboot.compiler.Models.User;
import com.springboot.compiler.Services.MainService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Controller
@ResponseBody
@RequestMapping("/submissions")
@RequiredArgsConstructor
@Tag(name = "Submission controller",description = "Handle all requests related to user submissions")
public class SubmissionController {

    private final MainService mainService;
    
    @Operation(description ="Gives the details of submissions")
    @GetMapping("/{submissionId}")
    @Cacheable(cacheNames ="mainCache",key="#submissionId")
    public ResponseEntity<CompilationResponse> getSubmissionDetails(@PathVariable(name ="submissionId",required = true) String submissionId){
        return ResponseEntity.ok().body(mainService.getSubmissionDetails(submissionId));
    }

    @Operation(description= "Return logged in user submissions supports filters based on language and status. it supports pagination")
    @GetMapping("/")
    public ResponseEntity<UserSubmissionDetails> getUserSubmissionDetails(
            @AuthenticationPrincipal User user,
            @RequestParam(name="pageNumber",required = false,defaultValue = "0") int pageNumber,
            @RequestParam(name="pageSize",required = false,defaultValue = "10") int pageSize,
            @RequestParam(name="language",required = false) String language,
            @RequestParam(name="status",required = false) String status  
            ){

                return ResponseEntity.ok().body(mainService.getUserSubmissionDetails(user.getUsername(),pageNumber, pageSize, language, status));
    }
    
    
    @Operation(description = "deletes the submission. The current logged in user should be owner of submission")
    @DeleteMapping("/delete/{submissionId}")
    public ResponseEntity<String> deleteSubmission(@AuthenticationPrincipal User user,@PathVariable(value="submissionId",required = true) String submissionId){
        mainService.deleteSubmission(user.getUsername(), submissionId);
        return ResponseEntity.ok().body("Submission Deleted");
    }

    @Operation(description = "downloads the source code file based on submission id")
    @GetMapping("/download/{submissionId}")
    public ResponseEntity<Resource> download(@PathVariable(value ="submissionId",required = true)String submissionId) throws IOException{
        
        Object[] data=mainService.downloadFile(submissionId);
        if(data.length==0)
            throw new UsernameNotFoundException("Submission not exists");
        String fileName=(String)data[0];
        Resource resource=(Resource)data[1];
        // Resource resource2=new ByteArrayResource(null);
        MediaType contentType=MediaType.parseMediaType("text/plain");
        ContentDisposition contentDisposition=ContentDisposition.attachment().filename(fileName).build();
        return ResponseEntity.ok().contentType(contentType).header(HttpHeaders.CONTENT_DISPOSITION,contentDisposition.toString()).body(resource);
    }  



    @Operation(description = "Gives the submission details such are language,submissionCount,success submissions and pst 24 hours submission count")
    @GetMapping("/details")
    public ResponseEntity<SubmissionDetails> details(
        @RequestParam(value="username",required = false) String username,
        @AuthenticationPrincipal User user
    ){
        System.out.println(user.getUsername());
        if(username==null)
            username=user.getUsername();
        return ResponseEntity.ok().body(mainService.getUserSubmissionDetails(username));
    }


    @Operation(description = "Gives all the submissions maded by users in past hours. By default it will give submissions maded in past hours by all users you can specify hours as query parameter. It support username based, languages based, status based filters. Also support pagination")
    @GetMapping("/history")
    public ResponseEntity<UserSubmissionDetails> history(
        @RequestParam(value="username",required = false) String username,
        @RequestParam(value="hour",required = false,defaultValue = "1") int hour,
        @RequestParam(value="language",required = false) String languages,
        @RequestParam(value="status",required = false) String status,
        @RequestParam(value = "pageNumber",required = false,defaultValue = "0") int pageNumber,
        @RequestParam(value = "pageSize",required = false,defaultValue = "10") int pageSize
    ){

        return ResponseEntity.ok().body(mainService.getSubmissionsInLastHours(username, hour, languages, status, pageNumber, pageSize));
    }

}
