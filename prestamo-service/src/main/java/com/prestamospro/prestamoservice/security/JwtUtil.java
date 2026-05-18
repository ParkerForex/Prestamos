package com.prestamospro.prestamoservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getKey() {
        byte[] keyBytes = new byte[32];
        byte[] secretBytes = secret.getBytes();
        for (int i = 0; i < keyBytes.length; i++) {
            keyBytes[i] = secretBytes[i % secretBytes.length];
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String getRol(String token) {
        return getClaims(token).get("rol", String.class);
    }

    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}