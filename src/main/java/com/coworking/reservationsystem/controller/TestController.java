package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/load")
    public String testLoadUser() {
        try {
            userDetailsService.loadUserByUsername("test@example.com");
            return "User loaded successfully";
        } catch (Exception e) {
            return "Error loading user: " + e.getMessage();
        }
    }
} 