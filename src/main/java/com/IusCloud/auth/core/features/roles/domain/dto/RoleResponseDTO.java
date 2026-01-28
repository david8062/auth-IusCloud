package com.IusCloud.auth.core.features.roles.domain.dto;

import com.IusCloud.auth.core.base.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class RoleResponseDTO extends BaseDTO {
    private String name;
    private String description;
    private UUID tenantId;
    private Set<PermissionResponseDTO> permissions;
}
