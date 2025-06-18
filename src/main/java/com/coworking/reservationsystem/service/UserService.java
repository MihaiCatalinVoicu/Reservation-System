package com.coworking.reservationsystem.service;

import com.coworking.reservationsystem.model.dto.PasswordDto;
import com.coworking.reservationsystem.model.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto, PasswordDto passwordDto);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    List<UserDto> getUsersByTenantId(Long tenantId);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    void changePassword(Long userId, PasswordDto currentPassword, PasswordDto newPassword);
}
