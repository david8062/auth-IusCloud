package com.IusCloud.auth.core.features.users.domain.dto;

import com.IusCloud.auth.core.base.BaseDTO;
import com.IusCloud.auth.core.features.roles.domain.dto.RoleResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserResponseDTO extends BaseDTO {
    private UUID tenantId;
    private String email;
    private String firstName;
    private String lastName;
    private Set<RoleResponseDTO> roles;
}
