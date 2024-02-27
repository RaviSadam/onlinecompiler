package com.springboot.compiler.WebConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfiguration{

    @Bean
	public WebMvcConfigurer configurer(){
        return new WebMvcConfigurer() {

            //configuration for CROS
            @SuppressWarnings("null")			
            @Override
			public void addCorsMappings(CorsRegistry corsRegistry){
				corsRegistry
                        .addMapping("/**")
                        .allowedOrigins("*")
                        .allowedHeaders("*");
			}

            //configuration for content negotiation
		    @SuppressWarnings("null")
            @Override
            public void configureContentNegotiation(ContentNegotiationConfigurer configurer){
                configurer
                        .defaultContentType(MediaType.APPLICATION_JSON)
                        .favorParameter(true)
                        .mediaType("json", MediaType.APPLICATION_JSON)
                        .mediaType("xml", MediaType.APPLICATION_XML);
            }
		};
	}

}
