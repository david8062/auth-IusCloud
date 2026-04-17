package com.IusCloud.auth.core.features.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class LoginRequestDTO {

    @NotBlank
    private String identifier; // email or username

    @NotBlank
    private String password;

}
