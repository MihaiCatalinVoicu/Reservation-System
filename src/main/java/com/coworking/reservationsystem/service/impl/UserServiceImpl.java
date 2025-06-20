package com.coworking.reservationsystem.service.impl;

import com.coworking.reservationsystem.exception.ResourceNotFoundException;
import com.coworking.reservationsystem.exception.ValidationException;
import com.coworking.reservationsystem.model.dto.PasswordDto;
import com.coworking.reservationsystem.model.dto.UserDto;
import com.coworking.reservationsystem.model.entity.Tenant;
import com.coworking.reservationsystem.model.entity.User;
import com.coworking.reservationsystem.repository.TenantRepository;
import com.coworking.reservationsystem.repository.UserRepository;
import com.coworking.reservationsystem.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto, PasswordDto passwordDto) {
        User user = UserDto.Mapper.toEntity(userDto);
        user.setPassword(passwordDto.password()); // Store password in plain text since security is disabled
        user.setCreatedAt(LocalDateTime.now());

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(List.of("USER"));
        }

        // Set tenant if tenantId is provided
        if (userDto.tenantId() != null) {
            Tenant tenant = tenantRepository.findById(userDto.tenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + userDto.tenantId()));
            user.setTenant(tenant);
        }

        user = userRepository.save(user);
        return UserDto.Mapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserDto.Mapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByTenantId(Long tenantId) {
        return userRepository.findByTenantId(tenantId).stream()
                .map(UserDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setEmail(userDto.email());
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        
        // Update tenant if tenantId is provided
        if (userDto.tenantId() != null) {
            Tenant tenant = tenantRepository.findById(userDto.tenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + userDto.tenantId()));
            user.setTenant(tenant);
        }
        
        user = userRepository.save(user);
        return UserDto.Mapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, PasswordDto currentPassword, PasswordDto newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Simple password comparison since security is disabled
        if (!currentPassword.password().equals(user.getPassword())) {
            throw new ValidationException("Current password is incorrect");
        }

        user.setPassword(newPassword.password()); // Store new password in plain text
        userRepository.save(user);
    }

    @PostConstruct
    public void testCreateUser() {
        // Only create test user if no users exist and if we have a tenant
        if (userRepository.count() == 0 && tenantRepository.count() > 0) {
            // Get the first available tenant
            Tenant firstTenant = tenantRepository.findAll().get(0);
            
            UserDto userDto = new UserDto(
                    null,
                    "test3@example.com",
                    "Test3",
                    "User3",
                    LocalDateTime.now(),
                    List.of("USER"),
                    firstTenant.getId()
            );
            PasswordDto passwordDto = new PasswordDto("Test123!@#");
            createUser(userDto, passwordDto);
        }
    }
} 