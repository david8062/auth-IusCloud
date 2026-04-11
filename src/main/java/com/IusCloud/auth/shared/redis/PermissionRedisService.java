package com.IusCloud.auth.shared.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionRedisService {

    private static final String KEY_PATTERN = "auth:perms:%s:%s";

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${security.jwt.expiration}")
    private long jwtExpirationMs;

    public void writePermissions(UUID tenantId, UUID userId, Set<String> permissions) {
        String key = KEY_PATTERN.formatted(tenantId, userId);
        try {
            String json = toJsonArray(permissions);
            redisTemplate.opsForValue().set(key, json, Duration.ofMillis(jwtExpirationMs));
        } catch (RuntimeException e) {
            log.warn("Redis write failed for key {}: {}", key, e.getMessage());
        }
    }

    public void deletePermissions(UUID tenantId, UUID userId) {
        String key = KEY_PATTERN.formatted(tenantId, userId);
        try {
            redisTemplate.delete(key);
        } catch (RuntimeException e) {
            log.warn("Redis delete failed for key {}: {}", key, e.getMessage());
        }
    }

    public List<String> readPermissions(UUID tenantId, UUID userId) {
        String key = KEY_PATTERN.formatted(tenantId, userId);
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) return List.of();
            return parseJsonArray(json);
        } catch (RuntimeException e) {
            log.warn("Redis read failed for key {}: {}", key, e.getMessage());
            return List.of();
        }
    }

    private String toJsonArray(Set<String> values) {
        String joined = values.stream()
                .map(v -> "\"" + v + "\"")
                .collect(Collectors.joining(","));
        return "[" + joined + "]";
    }

    private List<String> parseJsonArray(String json) {
        String trimmed = json.trim();
        if (trimmed.equals("[]")) return List.of();
        trimmed = trimmed.substring(1, trimmed.length() - 1);
        return Arrays.stream(trimmed.split(","))
                .map(s -> s.trim().replaceAll("^\"|\"$", ""))
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
