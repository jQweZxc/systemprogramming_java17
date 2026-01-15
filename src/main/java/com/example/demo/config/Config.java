package com.example.demo.config;

import java.time.LocalDate;
import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;

@Configuration
public class Config implements WebMvcConfigurer {
    
    // Используйте значение по умолчанию
    @Value("${upload.path:uploads}")
    private String uploadPath;
    
    @Autowired
    private RequestLoggingInterceptor requestLoggingInterceptor;
    
    @PostConstruct
    public void init() throws IOException {
        // Создаем папку для загрузок
        if (uploadPath == null || uploadPath.trim().isEmpty()) {
            uploadPath = "uploads";
        }
        
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (created) {
                System.out.println("✓ Создана папка для загрузок: " + uploadDir.getAbsolutePath());
            } else {
                System.err.println("✗ Не удалось создать папку: " + uploadDir.getAbsolutePath());
            }
        } else {
            System.out.println("✓ Папка для загрузок уже существует: " + uploadDir.getAbsolutePath());
        }
        
        // Убедимся, что путь правильный для Windows
        uploadPath = uploadDir.getAbsolutePath();
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor);
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        try {
            String normalizedPath = uploadPath.replace("\\", "/");
            if (!normalizedPath.endsWith("/")) {
                normalizedPath += "/";
            }
            
            // Для Windows нужно добавить file:///
            String resourcePath = "file:///" + normalizedPath;
            
            registry.addResourceHandler("/img/**")
                    .addResourceLocations(resourcePath);
            
            System.out.println("✓ Статические ресурсы настроены:");
            System.out.println("  URL: /img/**");
            System.out.println("  Папка: " + resourcePath);
            
        } catch (Exception e) {
            System.err.println("✗ Ошибка настройки статических ресурсов: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        registry.addConverter(new StringToLocalDateConverter());
    }

    private static class StringToLocalDateConverter implements Converter<String, LocalDate> {
        @Override
        public LocalDate convert(@NonNull String source) {
            if (source == null || source.trim().isEmpty()) {
                return null;
            }
            return LocalDate.parse(source);
        }
    }
}