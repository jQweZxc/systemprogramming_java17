// src/main/java/com/example/demo/TestUserCreator.java
package com.example.demo;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class TestUserCreator {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PostConstruct
    public void createTestUser() {
        try {
            // Создаем роль ADMIN если нет
            Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    return roleRepository.save(role);
                });
            
            // Создаем пользователя admin если нет
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@system.com");
                admin.setRole(adminRole);
                userRepository.save(admin);
                
                System.out.println("✅ Создан тестовый пользователь:");
                System.out.println("👤 Логин: admin");
                System.out.println("🔑 Пароль: admin123");
                System.out.println("👑 Роль: ADMIN");
            } else {
                System.out.println("✅ Пользователь admin уже существует");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Ошибка создания тестового пользователя: " + e.getMessage());
        }
    }
}