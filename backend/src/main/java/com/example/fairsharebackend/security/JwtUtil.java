package com.example.fairsharebackend.security;

import com.example.fairsharebackend.entity.StaticRole;
import com.example.fairsharebackend.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtUtil {
    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey =
                Keys.hmacShaKeyFor(
                        Base64.getDecoder().decode(secret)
                );
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("roles",
                user.getStaticRoles().stream()
                        .map(StaticRole::getName)
                        .toList()
        );

        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserId()))
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12)) // 12 hours
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token, User user) {
        String userId = extractUserId(token);
        return userId.equals(String.valueOf(user.getUserId())) && !isTokenExpired(token);
    }

    // Extract DB user ID from token (subject)
    public String extractUserId(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId");
    }

    // Extract email from claims
    public String extractEmail(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email");
    }

    // Extract roles from claims
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return (List<String>) Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles");
    }

    public boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}
