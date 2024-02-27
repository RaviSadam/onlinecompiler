package com.springboot.compiler.Dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.springboot.compiler.Models.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompilationResponse {

    @JsonAlias("submission_id")
    private String submissionId;

    @JsonAlias("status")
    private Status status;

    @JsonAlias("error_details")
    private String errorDetails;
    
    @JsonAlias("run_time")
    private String runTime;

    @JsonAlias("output")
    private String output;

    @JsonAlias("input")
    private String input;

    private String language;

    private String username;

    private String code;
    
}
