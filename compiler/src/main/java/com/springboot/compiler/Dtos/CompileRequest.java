package com.springboot.compiler.Dtos;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompileRequest {

    @NotBlank(message = "language is requried for compilation")
    @Pattern(regexp = "[a-z]+",message = "only small case letters allowed")
    private String language;

    private String code;

    private String input;
    
    @JsonAlias({"expectedoutput","expected_output","expectedOutput"})
    private String expectedOutput;

    @JsonAlias({"runtime","run_time","runTime"})
    private long runTime;
}
