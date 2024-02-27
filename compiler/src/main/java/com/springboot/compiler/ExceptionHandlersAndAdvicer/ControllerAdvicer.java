package com.springboot.compiler.ExceptionHandlersAndAdvicer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@ResponseBody
@SuppressWarnings("null")
@Log4j2
public class ControllerAdvicer {
    @ExceptionHandler(UsernameNotFoundException.class)
    public ProblemDetail usernameNotFoundException(HttpServletRequest request,UsernameNotFoundException ex){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Not Found");
        return problemDetail;
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ProblemDetail malformedJwtException(MalformedJwtException Ex){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Invalid Authentication token");
        problemDetail.setTitle("Invalid Token");
        return problemDetail;
    }

    @ExceptionHandler(RequestRateLimitException.class)
    public ProblemDetail requestRateLimitException(RequestRateLimitException ex){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
        problemDetail.setTitle("Request Rate Limit Exceded");
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail methodArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ProblemDetail problemDetail=ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperties(errors);
        problemDetail.setTitle("Invalid Request Body");
        return problemDetail;
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail httpMessageNotReadableException(HttpMessageNotReadableException ex){
        ProblemDetail problemDetails=ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
        problemDetails.setTitle("Json parse Error");
        return problemDetails;
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ProblemDetail unsupportedOperationException(UnsupportedOperationException ex){
        ProblemDetail problemDetails=ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetails.setTitle("Language Not Supported");
        return problemDetails;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail illegalArgumentException(IllegalArgumentException ex){
        ProblemDetail problemDetails=ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetails.setTitle("Invalid Argument");
        return problemDetails;
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ProblemDetail missingRequestHeaderException(MissingRequestHeaderException ex){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle(ex.getTitleMessageCode());
        return problemDetail;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ProblemDetail responseStatusException(HttpServletRequest request,NoHandlerFoundException ex){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,ex.getMessage());
        problemDetail.setTitle("Page not found");
        return problemDetail;
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail noResourceFoundException(NoResourceFoundException ex){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Resource NOt FOund");        
        return problemDetail;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED,ex.getMessage());
        problemDetail.setTitle("Method Not Supported");
        return problemDetail;
    }

    @ExceptionHandler(IOException.class)
    public ProblemDetail iOException(IOException exception){
        log.error(exception.getCause());
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ProblemDetail exceptionDetail(Exception ex){
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ProblemDetail fileNotFoundException(FileNotFoundException ex){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "SUbmission file not present");
        problemDetail.setTitle("FIle Not Found");
        return problemDetail;
    }

    @ExceptionHandler(DirectoryNotEmptyException.class)
    public ProblemDetail directoryNotEmptyException(DirectoryNotEmptyException ex){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An error occured while deleting files");
        problemDetail.setTitle("Directory not empty");
        return problemDetail;
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail badCredentialsException(BadCredentialsException ex){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Your creadentials not matching or Your request for deletetion of account");
        problemDetail.setTitle("Bad Credentials");
        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail accessDeniedException(AccessDeniedException ex){
        ProblemDetail problemDetail=ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("Access Denied");
        return problemDetail;
    }

}
