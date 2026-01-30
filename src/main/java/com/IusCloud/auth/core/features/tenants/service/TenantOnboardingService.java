package com.IusCloud.auth.core.features.tenants.service;

import com.IusCloud.auth.core.features.auth.domain.dto.LoginRequestDTO;
import com.IusCloud.auth.core.features.auth.domain.dto.LoginResponseDTO;
import com.IusCloud.auth.core.features.auth.service.AuthService;
import com.IusCloud.auth.core.features.tenants.domain.dto.TenantOnboardingRequestDTO;
import com.IusCloud.auth.core.features.tenants.domain.dto.TenantResponseDTO;
import com.IusCloud.auth.core.features.tenants.domain.model.TenantEntity;
import com.IusCloud.auth.core.features.tenants.repository.TenantRepository;
import com.IusCloud.auth.core.features.users.service.UserService;
import com.IusCloud.auth.shared.Helper.RoleHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantOnboardingService {

    private final TenantService tenantService;
    private final UserService userService;
    private final AuthService authService;
    private final TenantRepository tenantRepository;
    private final RoleHelper roleHelper;

    @Transactional
    public LoginResponseDTO onboard(
            TenantOnboardingRequestDTO request,
            HttpServletRequest httpRequest
    ) {

        TenantResponseDTO tenantDTO = tenantService.create(request.getTenant());
        TenantEntity tenant = tenantRepository.getReferenceById(tenantDTO.getId());

        roleHelper.createAdminRole(tenant);

        userService.createOwner(tenant.getId(), request.getOwner());

        LoginRequestDTO loginRequest = new LoginRequestDTO(
                request.getOwner().getEmail(),
                request.getOwner().getPassword()
        );

        return authService.loginWithTenant(
                tenant.getId(),
                loginRequest,
                httpRequest
        );
    }

}

