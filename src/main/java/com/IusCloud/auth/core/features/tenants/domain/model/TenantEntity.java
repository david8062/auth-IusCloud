package com.IusCloud.auth.core.features.tenants.domain.model;

import com.IusCloud.auth.core.base.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantEntity extends BaseModel {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "slug", nullable = false, length = 100)
    private String slug;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "plan", length = 50)
    private String plan;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "billing_email", length = 150)
    private String billingEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TenantEnum status = TenantEnum.TRIAL;

    @Column(name = "trial_ends_at")
    private Instant trialEndsAt;

    @Column(name = "subscription_ends_at")
    private Instant subscriptionEndsAt;

}
