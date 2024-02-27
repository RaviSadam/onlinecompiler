package com.springboot.compiler;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;



@Configuration
public class Beans {

    @Bean
    @Scope("singleton")
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(12);
	}
	@Bean("singleton")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { 
        return config.getAuthenticationManager(); 
    }


    @SuppressWarnings("null")
    @Bean
    public CacheManager cacheManager(){
        CaffeineCacheManager manager=new CaffeineCacheManager();
        manager.registerCustomCache("mainCache", Caffeine.newBuilder().expireAfterAccess(5,TimeUnit.MINUTES).maximumSize(100).initialCapacity(40).build());
        manager.registerCustomCache("InvalidTokens", Caffeine.newBuilder().maximumSize(100).initialCapacity(100).build());
        manager.registerCustomCache("CompilationRequests", Caffeine.newBuilder().expireAfterAccess(30,TimeUnit.SECONDS).maximumSize(100).initialCapacity(40).build());
        manager.setAllowNullValues(false);
        return manager;
    }

    @Bean
    @Scope("singleton")
    public Caffeine<Object,Object> caffeineConfig(){
        return Caffeine.newBuilder().expireAfterAccess(300, TimeUnit.SECONDS).maximumSize(500).initialCapacity(10);
    }

    @Bean
    @Scope("singleton")
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    @Scope("singleton")
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("HH:mm:ss");
    }

    @Bean
    @Scope("singleton")
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
