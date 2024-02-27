package com.springboot.compiler.Dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    String token;
}
