package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.PasswordDto;
import com.coworking.reservationsystem.model.dto.UserDto;
import com.coworking.reservationsystem.model.entity.Tenant;
import com.coworking.reservationsystem.model.entity.User;
import com.coworking.reservationsystem.repository.TenantRepository;
import com.coworking.reservationsystem.repository.UserRepository;
import com.coworking.reservationsystem.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto validUserDto;
    private User validUser;
    private Tenant validTenant;
    private PasswordDto validPasswordDto;

    @BeforeEach
    void setUp() {
        validTenant = new Tenant();
        validTenant.setId(1L);
        validTenant.setName("hotel-exemplu");
        validTenant.setSubdomain("hotel-exemplu");
        validTenant.setStatus(Tenant.TenantStatus.ACTIVE);

        validUserDto = new UserDto(
                null,
                "test@example.com",
                "John",
                "Doe",
                LocalDateTime.now(),
                Arrays.asList("ROLE_USER"),
                1L // tenantId
        );

        validUser = new User();
        validUser.setId(1L);
        validUser.setEmail("test@example.com");
        validUser.setFirstName("John");
        validUser.setLastName("Doe");
        validUser.setCreatedAt(LocalDateTime.now());
        validUser.setPassword("encodedPassword");
        validUser.setRoles(Arrays.asList("ROLE_USER"));
        validUser.setTenant(validTenant);

        validPasswordDto = new PasswordDto("Test123!@#");
    }

    @Test
    void createUser_Success() {
        // Given
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(validTenant));
        when(passwordEncoder.encode(validPasswordDto.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        // When
        UserDto result = userService.createUser(validUserDto, validPasswordDto);

        // Then
        assertNotNull(result);
        assertEquals(validUserDto.email(), result.email());
        assertEquals(validUserDto.firstName(), result.firstName());
        assertEquals(validUserDto.lastName(), result.lastName());
        assertEquals(validUserDto.tenantId(), result.tenantId());

        verify(tenantRepository).findById(1L);
        verify(passwordEncoder).encode(validPasswordDto.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithoutTenantId_Success() {
        // Given
        UserDto userDtoWithoutTenant = new UserDto(
                null,
                "test@example.com",
                "John",
                "Doe",
                LocalDateTime.now(),
                Arrays.asList("ROLE_USER"),
                null // no tenantId
        );

        when(passwordEncoder.encode(validPasswordDto.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        // When
        UserDto result = userService.createUser(userDtoWithoutTenant, validPasswordDto);

        // Then
        assertNotNull(result);
        assertEquals(userDtoWithoutTenant.email(), result.email());

        verify(tenantRepository, never()).findById(any());
        verify(passwordEncoder).encode(validPasswordDto.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_TenantNotFound_ThrowsException() {
        // Given
        when(tenantRepository.findById(999L)).thenReturn(Optional.empty());

        UserDto userDtoWithInvalidTenant = new UserDto(
                null,
                "test@example.com",
                "John",
                "Doe",
                LocalDateTime.now(),
                Arrays.asList("ROLE_USER"),
                999L // invalid tenantId
        );

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.createUser(userDtoWithInvalidTenant, validPasswordDto));
        assertEquals("Tenant not found with id: 999", exception.getMessage());

        verify(tenantRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_DefaultRolesSet() {
        // Given
        UserDto userDtoWithoutRoles = new UserDto(
                null,
                "test@example.com",
                "John",
                "Doe",
                LocalDateTime.now(),
                null, // no roles
                1L
        );

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(validTenant));
        when(passwordEncoder.encode(validPasswordDto.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        // When
        UserDto result = userService.createUser(userDtoWithoutRoles, validPasswordDto);

        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));

        // When
        UserDto result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(validUser.getId(), result.id());
        assertEquals(validUser.getEmail(), result.email());
        assertEquals(validUser.getFirstName(), result.firstName());
        assertEquals(validUser.getLastName(), result.lastName());
        assertEquals(validUser.getTenant().getId(), result.tenantId());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(999L));
        assertEquals("User not found with id: 999", exception.getMessage());

        verify(userRepository).findById(999L);
    }

    @Test
    void getAllUsers_Success() {
        // Given
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("test2@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setTenant(validTenant);

        List<User> users = Arrays.asList(validUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<UserDto> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(validUser.getEmail(), result.get(0).email());
        assertEquals(user2.getEmail(), result.get(1).email());

        verify(userRepository).findAll();
    }

    @Test
    void getUsersByTenantId_Success() {
        // Given
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("test2@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setTenant(validTenant);

        List<User> users = Arrays.asList(validUser, user2);
        when(userRepository.findByTenantId(1L)).thenReturn(users);

        // When
        List<UserDto> result = userService.getUsersByTenantId(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(validUser.getEmail(), result.get(0).email());
        assertEquals(user2.getEmail(), result.get(1).email());

        verify(userRepository).findByTenantId(1L);
    }

    @Test
    void updateUser_Success() {
        // Given
        UserDto updateDto = new UserDto(
                1L,
                "updated@example.com",
                "John Updated",
                "Doe Updated",
                LocalDateTime.now(),
                Arrays.asList("ROLE_ADMIN"),
                1L
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(validTenant));
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        // When
        UserDto result = userService.updateUser(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(tenantRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_NotFound_ThrowsException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(999L, validUserDto));
        assertEquals("User not found with id: 999", exception.getMessage());

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_InvalidTenant_ThrowsException() {
        // Given
        UserDto updateDtoWithInvalidTenant = new UserDto(
                1L,
                "updated@example.com",
                "John Updated",
                "Doe Updated",
                LocalDateTime.now(),
                Arrays.asList("ROLE_ADMIN"),
                999L // invalid tenant
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        when(tenantRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(1L, updateDtoWithInvalidTenant));
        assertEquals("Tenant not found with id: 999", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(tenantRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteUser(999L));
        assertEquals("User not found with id: 999", exception.getMessage());

        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void changePassword_Success() {
        // Given
        PasswordDto currentPassword = new PasswordDto("CurrentPass123!@#");
        PasswordDto newPassword = new PasswordDto("NewPass123!@#");

        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        when(passwordEncoder.matches(currentPassword.password(), "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode(newPassword.password())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        // When
        userService.changePassword(1L, currentPassword, newPassword);

        // Then
        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches(currentPassword.password(), "encodedPassword");
        verify(passwordEncoder).encode(newPassword.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_UserNotFound_ThrowsException() {
        // Given
        PasswordDto currentPassword = new PasswordDto("CurrentPass123!@#");
        PasswordDto newPassword = new PasswordDto("NewPass123!@#");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.changePassword(999L, currentPassword, newPassword));
        assertEquals("User not found with id: 999", exception.getMessage());

        verify(userRepository).findById(999L);
        verify(passwordEncoder, never()).matches(any(), any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_CurrentPasswordIncorrect_ThrowsException() {
        // Given
        PasswordDto currentPassword = new PasswordDto("WrongPass123!@#");
        PasswordDto newPassword = new PasswordDto("NewPass123!@#");

        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        when(passwordEncoder.matches(currentPassword.password(), "encodedPassword")).thenReturn(false);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.changePassword(1L, currentPassword, newPassword));
        assertEquals("Current password is incorrect", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches(currentPassword.password(), "encodedPassword");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
    }
} 