package com.oak.avatar_tcg.service;

import com.oak.avatar_tcg.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class AuthService {
    private final UserService userService;
    private final Key key = Keys.hmacShaKeyFor(System.getenv("JWT_SECRET").getBytes());

    public AuthService() {
        this.userService = new UserService();
    }

    public String generateToken(User user) {
        // 24 hours
        long EXPIRATION_TIME = 86400000;

        return Jwts.builder()
                .setSubject(user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String validateToken(String token) throws Exception {
        try {
            return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
        } catch (Exception e) {
            throw new Exception("Invalid token");
        }
    }

    public User authenticate(String email, String password) throws Exception {
        User user = userService.findByEmail(email);

        if (user == null || !user.getPassword().equals(password)) {
            throw new Exception("Invalid credentials");
        }
        return user;
    }
}