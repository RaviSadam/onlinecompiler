package com.springboot.compiler.ExceptionHandlersAndAdvicer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestRateLimitException extends RuntimeException {

    String message;
    public RequestRateLimitException(String message){
        super(message);
        this.message=message;
    }
    
}
