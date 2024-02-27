package com.springboot.compiler.Dtos;

import java.util.Date;

import com.springboot.compiler.Models.Status;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserSubmission {
    private String language;
    private Status status;
    private String submissionId;
    private String username;
    private Date submissionTime;
}
