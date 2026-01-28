package com.IusCloud.auth.core.features.roles.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class RoleRequestDTO {

    @NotBlank
    @Size(max = 50)
    private String name;

    @Size(max = 255)
    private String description;

    @NotNull
    private UUID tenantId;

    private Set<UUID> permissionIds;

    private Boolean active;
}
