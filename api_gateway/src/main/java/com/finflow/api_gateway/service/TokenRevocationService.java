package com.finflow.api_gateway.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenRevocationService {

    private final Key signingKey;
    private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    public TokenRevocationService(@Value("${jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public void revoke(String token) {
        Instant expiresAt = extractExpiration(token);
        if (!expiresAt.isAfter(Instant.now())) {
            revokedTokens.remove(token);
            return;
        }

        revokedTokens.put(token, expiresAt);
    }

    public boolean isRevoked(String token) {
        Instant expiresAt = revokedTokens.get(token);
        if (expiresAt == null) {
            return false;
        }

        if (!expiresAt.isAfter(Instant.now())) {
            revokedTokens.remove(token, expiresAt);
            return false;
        }

        return true;
    }

    private Instant extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .toInstant();
    }
}
