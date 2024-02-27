package com.springboot.compiler.Dtos;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSubmissionDetails {
    private List<UserSubmission> userSubmissions;
    private int totlaPages;
    private int pageSize;
    private int pageNumber;
    private long totalSubmissions;
    private String username;
}
