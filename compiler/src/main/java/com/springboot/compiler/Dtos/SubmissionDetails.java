package com.springboot.compiler.Dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionDetails {
    private String username;
    private long totalCount;
    private long past24;
    private long successCount;
    private List<SubmissionCount> submissions;
}
