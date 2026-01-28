package com.IusCloud.auth.core.features.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String type = "Bearer";
    private String email;
    private String firstName;
    private String lastName;
}
