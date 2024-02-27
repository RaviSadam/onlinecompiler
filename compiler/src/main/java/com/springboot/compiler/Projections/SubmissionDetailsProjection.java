package com.springboot.compiler.Projections;

import java.util.Date;

import com.springboot.compiler.Models.Status;

public interface SubmissionDetailsProjection {
    String getSubmissionId();
    Status getStatus();
    String getErrorDetails();
    String getRunTime();
    String getOutput();
    String getInput();
    String getLanguage();
    String getUsername();
    Date getDate();
}
