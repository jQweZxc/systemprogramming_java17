package com.example.demo.service;

import com.example.demo.model.Permission;
import com.example.demo.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {
    
    private final PermissionRepository permissionRepository;
    
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
    
    public Permission getPermission(String resource, String operation) {
        return permissionRepository.findByResourceAndOperation(resource, operation)
            .orElseThrow(() -> new RuntimeException("Permission not found"));
    }
}