package com.IusCloud.auth.core.features.tenants.domain.dto;

import com.IusCloud.auth.core.base.BaseDTO;
import com.IusCloud.auth.core.features.tenants.domain.model.TenantEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class TenantResponseDTO extends BaseDTO {
    private String name;
    private String slug;
    private String email;
    private String plan;
    private String phone;
    private String country;
    private String billingEmail;
    private TenantEnum status;
    private Instant trialEndsAt;
    private Instant subscriptionEndsAt;
}
