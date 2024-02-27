package com.springboot.compiler.Projections;

public interface SubmissionCountProjection {
    String getLanguage();
    long getCount();
    long getPastCount();
    long getSuccessCount();
}
