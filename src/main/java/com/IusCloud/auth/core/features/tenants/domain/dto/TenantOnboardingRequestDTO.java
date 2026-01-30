package com.IusCloud.auth.core.features.tenants.domain.dto;

import com.IusCloud.auth.core.features.users.domain.dto.UserOwnerRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantOnboardingRequestDTO {

    @NotNull
    @Valid
    private TenantRequestDTO tenant;

    @NotNull
    @Valid
    private UserOwnerRequestDTO owner;
}
