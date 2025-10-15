package com.example.demo.models;

import java.security.PublicKey;
import java.util.Set;

import org.hibernate.annotations.ManyToAny;
import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"resource", "operation"})})
public class Permission implements GrantedAuthority{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String resource;

    @Column(nullable = false)
    private String operation;

    @OneToMany(mappedBy = "permissions")
    private Set<Role> roles;

    
    public Permission(String resource, String operation) {
        this.resource = resource;
        this.operation = operation;
    }

    @Override
    public String getAuthority()
    {
        return String.format(
            "%s:%s",
            resource.toUpperCase(),
            operation.toUpperCase());
    }
    }