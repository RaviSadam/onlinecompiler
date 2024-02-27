package com.springboot.compiler.WebConfiguration;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class WebInteseptor  implements HandlerInterceptor { 
  
    // Response is intercepted by this method before reaching the client 
    @SuppressWarnings("null")
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if(request.getSession().getAttribute("TokenExpired")!=null){
            response.sendError(401,"Token Expired. Please login again");
        }
        if(request.getSession().getAttribute("InvalidToken")!=null)
            response.sendError(400, "Invalid Token was given");
    } 
  
} 