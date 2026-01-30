package com.IusCloud.auth.core.features.tenants.domain.dto;

import com.IusCloud.auth.core.features.tenants.domain.model.TenantEnum;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class TenantRequestDTO {

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    @Size(max = 100)
    @Pattern(
            regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
            message = "Slug inválido. Use minúsculas, números y guiones"
    )
    private String slug;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 50)
    private String plan;


    @Size(max = 50)
    private String phone;

    @Size(max = 50)
    private String country;

    @Email
    @Size(max = 150)
    private String billingEmail;

    @Future
    private Instant trialEndsAt;

    private Instant subscriptionEndsAt;
}
