package com.taskmanager.service;

import com.taskmanager.dto.*;

import java.util.List;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserDto getCurrentUser(String email);
    List<UserDto> getAllUsers();
}
