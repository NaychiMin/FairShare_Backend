package com.example.fairsharebackend.security;

import com.example.fairsharebackend.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "tempsecretkeyforfairshareproject"; // temp

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("roles", user.getStaticRoles());

        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserId()))
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12)) // 12 hours
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token, User user) {
        String userId = extractUserId(token);
        return userId.equals(String.valueOf(user.getUserId())) && !isTokenExpired(token);
    }

    // Extract DB user ID from token (subject)
    public String extractUserId(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extract email from claims
    public String extractEmail(String token) {
        return (String) Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .get("email");
    }

    // Extract roles from claims
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return (List<String>) Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .get("roles");
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}
