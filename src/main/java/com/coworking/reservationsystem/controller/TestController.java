package com.coworking.reservationsystem.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}, allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class TestController {

    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of("message", "Backend is working!");
    }

    @PostMapping("/test-login")
    public Map<String, String> testLogin(@RequestBody Map<String, String> request) {
        return Map.of(
            "message", "Login endpoint reached!",
            "email", request.get("email"),
            "password", request.get("password")
        );
    }

    class SimpleLoginRequest {
        @JsonProperty("email")
        public String email;
        @JsonProperty("password")
        public String password;
    }

    @PostMapping("/simple-login")
    public Map<String, Object> simpleLogin(@RequestBody SimpleLoginRequest request) {
        String email = request.email;
        String password = request.password;
        
        // Simple hardcoded check
        if ("admin@central.ro".equals(email) && "Password123!".equals(password)) {
            return Map.of(
                "success", true,
                "message", "Simple login successful!",
                "accessToken", "fake-token-123",
                "refreshToken", "fake-refresh-456",
                "user", Map.of(
                    "email", email,
                    "roles", new String[]{"ADMIN"}
                )
            );
        } else {
            return Map.of(
                "success", false,
                "message", "Invalid credentials"
            );
        }
    }

    @GetMapping("/simple-login-get")
    public Map<String, Object> simpleLoginGet() {
        return Map.of(
            "success", true,
            "message", "GET endpoint works!",
            "accessToken", "fake-token-123",
            "refreshToken", "fake-refresh-456",
            "user", Map.of(
                "email", "admin@central.ro",
                "roles", new String[]{"ADMIN"}
            )
        );
    }
} 