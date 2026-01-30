package com.IusCloud.auth.core.features.tenants.service;

import com.IusCloud.auth.core.features.tenants.domain.model.TenantEntity;
import com.IusCloud.auth.core.features.tenants.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantResolver {

    private final TenantRepository tenantRepository;

    public TenantEntity resolveByHost(String host) {

        if (host.equals("localhost") || host.startsWith("127.")) {
            return null; // no tenant por host
        }

        String[] parts = host.split("\\.");

        String slug = parts[0];

        return tenantRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() ->
                        new RuntimeException("Tenant not found for host: " + host)
                );
    }
}
