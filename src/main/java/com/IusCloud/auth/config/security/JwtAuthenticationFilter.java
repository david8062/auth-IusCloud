package com.IusCloud.auth.config.security;

import com.IusCloud.auth.core.features.tenants.domain.model.TenantEntity;
import com.IusCloud.auth.core.features.tenants.service.TenantResolver;
import com.IusCloud.auth.shared.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TenantResolver tenantResolver;

    public JwtAuthenticationFilter(JwtService jwtService, TenantResolver tenantResolver) {
        this.jwtService = jwtService;
        this.tenantResolver = tenantResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth")
                || path.startsWith("/api/v1/onboard");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        boolean tenantSetLocally = false;

        try {
            // 1. Forzar resolución del Tenant si aún no existe en el contexto (Fix de Orden de Filtros)
            if (!TenantContext.hasTenant()) {
                TenantEntity tenant = tenantResolver.resolveByHost(request.getServerName());
                if (tenant != null) {
                    TenantContext.setTenantId(tenant.getId());
                    tenantSetLocally = true;
                }
            }

            Claims claims = jwtService.extractClaims(token);

            String userId = claims.getSubject();
            String tenantId = claims.get("tenantId", String.class);

            if (TenantContext.hasTenant()) {
                String currentTenantId = TenantContext.getTenantId().toString();
                if (tenantId == null || !currentTenantId.equals(tenantId)) {
                    throw new SecurityException("Token invalid for this tenant scope");
                }
            }

            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            var authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            authorities
                    );

            // Opcional pero MUY útil
            authentication.setDetails(
                    new TenantAuthenticationDetails(tenantId)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ex) {
            // Token inválido → limpiamos contexto
            SecurityContextHolder.clearContext();
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Limpieza para evitar memory leaks si nosotros establecimos el contexto aquí
            if (tenantSetLocally) {
                TenantContext.clear();
            }
        }
    }
}
