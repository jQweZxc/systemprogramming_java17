package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import com.example.demo.enums.TokenType;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private TokenType type;
    
    private String value;
    
    private LocalDateTime expiryDate;
    
    private boolean disabled;
    
    @ManyToOne
    private User user;

    // Конструкторы
    public Token() {}
    
    public Token(TokenType type, String value, LocalDateTime expiryDate, boolean disabled, User user) {
        this.type = type;
        this.value = value;
        this.expiryDate = expiryDate;
        this.disabled = disabled;
        this.user = user;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public TokenType getType() { return type; }
    public void setType(TokenType type) { this.type = type; }
    
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    
    public boolean isDisabled() { return disabled; }
    public void setDisabled(boolean disabled) { this.disabled = disabled; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}