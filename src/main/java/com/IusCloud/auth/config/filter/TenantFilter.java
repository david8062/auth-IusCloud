package com.IusCloud.auth.config.filter;

import com.IusCloud.auth.core.features.tenants.domain.model.TenantEntity;
import com.IusCloud.auth.core.features.tenants.service.TenantResolver;
import com.IusCloud.auth.shared.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private final TenantResolver tenantResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String host = request.getServerName();
        TenantEntity tenant = null;

        try {
            tenant = tenantResolver.resolveByHost(host);

            if (tenant == null) {
                String tenantIdHeader = request.getHeader("X-Tenant-ID");
                if (tenantIdHeader != null && !tenantIdHeader.isEmpty()) {
                    try {
                        tenant = tenantResolver.resolveById(UUID.fromString(tenantIdHeader));
                    } catch (Exception e) {
                        // Ignorar si el ID no es válido o no existe
                    }
                }
            }

            if (tenant != null) {
                TenantContext.setTenantId(tenant.getId());
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // El login necesita resolver el tenant, por lo que NO se debe excluir /api/v1/auth
        return path.startsWith("/api/v1/onboard");
    }
}
