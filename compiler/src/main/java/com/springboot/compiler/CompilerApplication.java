package com.springboot.compiler;



// eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6IltVU0VSXSIsInN1YiI6InJhbWExMjMiLCJpYXQiOjE3MDg5MzQ3NDcsImV4cCI6MTcwOTc5ODc0N30.gy1TsYXY5o_VqZQy1VWcQyTSvSqW5ihcwoeiocS1Gb0
// rama123
// eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6IltVU0VSXSIsInN1YiI6InJhdmkxMjMiLCJpYXQiOjE3MDg5MzU2MTIsImV4cCI6MTcwOTc5OTYxMn0.iPXNrx3ZZGnzH2bjKefOVBMDDsw0l7a9CYQLsPE-OBI
// ravi123


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;


@SpringBootApplication(scanBasePackages = "com.springboot.compiler")
@EnableWebMvc
@EnableCaching
@EnableScheduling
@SecurityScheme(
    name = "Bearer_Token",
    type = SecuritySchemeType.HTTP,
    scheme = "Bearer",
    bearerFormat = "JWT"
)
@OpenAPIDefinition(
	security = @SecurityRequirement(name = "Bearer_Token"),
	info = @Info(
				title="Online Compiler",
				version = "1.0",
				contact = @Contact(
							name = "Contact Developers",
							url = "https://www.linkedin.com/in/sadam-ravi/"
						),
				license = @License(
							name="The Apache License, Version 2.0.",
							url = "https://www.apache.org/licenses/LICENSE-2.0"
						),
				description = "Our online compiler provides a comprehensive platform for developers to compile and execute code snippets in various programming languages(C, C++, Java, Python) without the need to install any software locally. With support for code compilation, execution, input/output handling, and AI Generated code explanation"
		)
)

public class CompilerApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(CompilerApplication.class, args);
	}
	@Override
  	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    	return builder.sources(CompilerApplication.class);
  	}
}
