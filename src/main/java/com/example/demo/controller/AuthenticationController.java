package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserLoggedDto;
import com.example.demo.service.UserService;
import com.example.demo.service.impl.AuthServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Authentication", description = "API для аутентификации и управления сессиями")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthServiceImpl authService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Логин пользователя")
    @ApiResponse(responseCode = "200", description = "Успешная аутентификация")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @CookieValue(name = "access_token", required = false) String accessToken,
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest, accessToken, refreshToken);
    }

    @Operation(summary = "Обновление токена")
    @ApiResponse(responseCode = "200", description = "Токен успешно обновлен")
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.notFound().build();
        }
        return authService.refresh(refreshToken);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Выход из системы")
    @ApiResponse(responseCode = "200", description = "Сессия завершена")
    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout(
            @CookieValue(name = "access_token", required = false) String accessToken,
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        return authService.logout(accessToken, refreshToken);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Информация о пользователе", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Данные пользователя")
    @GetMapping("/info")
    public ResponseEntity<UserLoggedDto> userLoggedInfo() {
        return ResponseEntity.ok(authService.getUserLoggedInfo());
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/change_password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        if (!request.confirmPassword().equals(request.newPassword())) {
            return ResponseEntity.badRequest().build();
        }
        UserDto user = userService.getUser(authService.getUserLoggedInfo().username());
        if (passwordEncoder.matches(request.currentPassword(), user.password())) {
            userService.updateUser(user.id(),
                    new UserDto(user.id(), user.username(),
                            request.newPassword(), user.role(), user.permissions()));
            return ResponseEntity.ok("пароль успешно изменен");
        }
        return ResponseEntity.notFound().build();
    }
}