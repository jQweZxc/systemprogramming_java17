// src/main/java/com/example/demo/controller/SetupController.java
package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class SetupController {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/create-admin")
    public String createAdminUser() {
        try {
            // Проверяем, существует ли пользователь
            if (userRepository.findByUsername("admin").isPresent()) {
                return "Пользователь admin уже существует";
            }
            
            // Создаем или получаем роль ADMIN
            Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    return roleRepository.save(role);
                });
            
            // Создаем пользователя
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@system.com");
            admin.setRole(adminRole);
            userRepository.save(admin);
            
            return "✅ Пользователь admin создан!\n" +
                   "👤 Логин: admin\n" +
                   "🔑 Пароль: admin123\n" +
                   "👑 Роль: ADMIN";
            
        } catch (Exception e) {
            return "❌ Ошибка: " + e.getMessage();
        }
    }
}