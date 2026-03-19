package com.IusCloud.auth.config.security;

import com.IusCloud.auth.core.features.roles.domain.model.PermissionEntity;
import com.IusCloud.auth.core.features.roles.domain.model.RoleEntity;
import com.IusCloud.auth.core.features.users.domain.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtService {


    @Value("${security.jwt.secret}")
    private String secretKey;

    @Value("${security.jwt.expiration}")
    private long expirationMs;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserEntity user) {

        // ── Roles (como estaba) ───────────────────────────────────────
        List<String> roles = user.getRoles()
                .stream()
                .map(RoleEntity::getName)
                .toList();

        // ── Permissions (nuevo) ───────────────────────────────────────
        // Aplana roles → permissions, deduplica con Set
        Set<String> permissions = user.getRoles()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(PermissionEntity::getCode)   // "CASE_WRITE", "USER_READ", etc.
                .collect(Collectors.toSet());

        Map<String, Object> claims = new HashMap<>();
        claims.put("email",       user.getEmail());
        claims.put("tenantId",    user.getTenant().getId());
        claims.put("firstName",   user.getFirstName());
        claims.put("lastName",    user.getLastName());
        claims.put("active",      user.getActive());
        claims.put("roles",       roles);
        claims.put("permissions", permissions);   // 👈 único cambio visible

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
