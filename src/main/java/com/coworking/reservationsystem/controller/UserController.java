package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.model.dto.CreateUserRequest;
import com.coworking.reservationsystem.model.dto.UserDto;
import com.coworking.reservationsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        // Set tenantId in userDto if provided in request
        UserDto userDto = request.getUser();
        if (request.getTenantId() != null) {
            userDto = new UserDto(
                    userDto.id(),
                    userDto.email(),
                    userDto.firstName(),
                    userDto.lastName(),
                    userDto.createdAt(),
                    userDto.roles(),
                    request.getTenantId()
            );
        }
        
        UserDto createdUser = userService.createUser(userDto, request.getPassword());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<UserDto>> getUsersByTenantId(@PathVariable Long tenantId) {
        List<UserDto> users = userService.getUsersByTenantId(tenantId);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
