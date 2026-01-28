package com.IusCloud.auth.core.features.roles.domain.dto;

import com.IusCloud.auth.core.base.BaseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionResponseDTO extends BaseDTO {
    private String code;
    private String description;
}
