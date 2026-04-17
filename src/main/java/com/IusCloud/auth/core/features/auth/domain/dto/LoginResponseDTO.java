package com.IusCloud.auth.core.features.auth.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private String type = "Bearer";
    private String refreshToken;
    private UUID userId;
    private UUID tenantId;

    public LoginResponseDTO(String token, String type, String refreshToken) {
        this.token = token;
        this.type = type;
        this.refreshToken = refreshToken;
    }

    public LoginResponseDTO(String token, String type, String refreshToken, UUID userId, UUID tenantId) {
        this.token = token;
        this.type = type;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.tenantId = tenantId;
    }
}
