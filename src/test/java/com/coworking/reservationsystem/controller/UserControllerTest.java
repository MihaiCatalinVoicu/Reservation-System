package com.coworking.reservationsystem.controller;

import com.coworking.reservationsystem.config.TestSecurityConfig;
import com.coworking.reservationsystem.model.dto.CreateUserRequest;
import com.coworking.reservationsystem.model.dto.PasswordDto;
import com.coworking.reservationsystem.model.dto.UserDto;
import com.coworking.reservationsystem.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto validUserDto;
    private UserDto createdUserDto;
    private PasswordDto validPasswordDto;
    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        validUserDto = new UserDto(
                null,
                "test@example.com",
                "John",
                "Doe",
                LocalDateTime.now(),
                Arrays.asList("ROLE_USER"),
                1L
        );
        createdUserDto = new UserDto(
                1L,
                "test@example.com",
                "John",
                "Doe",
                LocalDateTime.now(),
                Arrays.asList("ROLE_USER"),
                1L
        );
        validPasswordDto = new PasswordDto("Test123!@#");
        createUserRequest = new CreateUserRequest();
        createUserRequest.setUser(validUserDto);
        createUserRequest.setPassword(validPasswordDto);
        createUserRequest.setTenantId(1L);
    }

    @Test
    void createUser_Success() throws Exception {
        when(userService.createUser(any(UserDto.class), any(PasswordDto.class))).thenReturn(createdUserDto);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.tenantId").value(1));

        verify(userService).createUser(any(UserDto.class), any(PasswordDto.class));
    }

    @Test
    void getUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(createdUserDto);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).getUserById(1L);
    }

    @Test
    void getAllUsers_Success() throws Exception {
        List<UserDto> users = Arrays.asList(createdUserDto);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(userService).getAllUsers();
    }

    @Test
    void getUsersByTenantId_Success() throws Exception {
        List<UserDto> users = Arrays.asList(createdUserDto);
        when(userService.getUsersByTenantId(1L)).thenReturn(users);

        mockMvc.perform(get("/api/v1/users/tenant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].tenantId").value(1));

        verify(userService).getUsersByTenantId(1L);
    }

    @Test
    void updateUser_Success() throws Exception {
        UserDto updateDto = new UserDto(
                1L,
                "updated@example.com",
                "John Updated",
                "Doe Updated",
                LocalDateTime.now(),
                Arrays.asList("ROLE_ADMIN"),
                1L
        );
        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(updateDto);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        verify(userService).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }
} 