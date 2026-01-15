package com.example.demo.jwt;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import com.example.demo.model.Token;
import com.example.demo.enums.TokenType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtTokenProviderImpl implements JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    private SecretKey getSigningKey() {
        byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(decodedKey);
    }
    
    @Override
    public Token generateAccessToken(Map<String, Object> extraClaims, 
            long duration, TemporalUnit durationType, UserDetails user) {
        
        String username = user.getUsername();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plus(duration, durationType);
        
        String token = Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(toDate(now))
                .expiration(toDate(expiryDate))
                .signWith(getSigningKey())
                .compact();

        return new Token(TokenType.ACCESS, token, expiryDate, false, null);
    }
    
    @Override
    public Token generateRefreshToken(long duration, 
            TemporalUnit durationType, UserDetails user) {
        
        String username = user.getUsername();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plus(duration, durationType);
        
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(toDate(now))
                .expiration(toDate(expiryDate))
                .signWith(getSigningKey())
                .compact();

        return new Token(TokenType.REFRESH, token, expiryDate, false, null);
    }
    
    @Override
    public boolean validateToken(String tokenValue) {
        if (tokenValue == null) return false;
        
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(tokenValue);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    
    @Override
    public String getUsernameFromToken(String tokenValue) {
        return extractClaim(tokenValue, Claims::getSubject);
    }
    
    @Override
    public LocalDateTime getExpiryDateFromToken(String tokenValue) {
        return toLocalDateTime(extractClaim(tokenValue, Claims::getExpiration));
    }
    
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }
    
    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime();
    }
}