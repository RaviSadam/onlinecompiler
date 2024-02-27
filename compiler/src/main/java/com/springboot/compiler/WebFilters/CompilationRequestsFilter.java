package com.springboot.compiler.WebFilters;

// import java.io.IOException;
// import org.springframework.cache.Cache;
// import org.springframework.cache.CacheManager;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import com.springboot.compiler.ExceptionHandlersAndAdvicer.RequestRateLimitException;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;


// @Component
// @RequiredArgsConstructor
// public class CompilationRequestsFilter extends OncePerRequestFilter {

//     private final CacheManager cacheManager;

//     @SuppressWarnings("null")
//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//         Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
//         if(authentication!=null && request.getServletPath().equals("/compiler/")){
//             Cache cache=cacheManager.getCache("CompilationRequests");
//             if(cache.get(authentication.getName())!=null){
//                 throw new RequestRateLimitException("Time gap between two compiler requests should be more then 30 SEC. Previously compiler Accessed at"+cache.get(authentication.getName()));
//             }
//         }
//         filterChain.doFilter(request,response);
//     }
    
// }
