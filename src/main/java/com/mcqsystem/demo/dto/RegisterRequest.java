package com.mcqsystem.demo.dto;

import com.mcqsystem.demo.enums.Role;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
}
