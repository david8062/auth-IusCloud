package com.IusCloud.auth.core.features.tenants.controller;

import com.IusCloud.auth.core.features.auth.domain.dto.LoginResponseDTO;
import com.IusCloud.auth.core.features.tenants.domain.dto.TenantOnboardingRequestDTO;
import com.IusCloud.auth.core.features.tenants.service.TenantOnboardingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/onboard")
@RequiredArgsConstructor
public class OnBoardController {

    private final TenantOnboardingService tenantOnboardingService;

    @PostMapping
    public ResponseEntity<LoginResponseDTO> onboard(
            @Valid @RequestBody TenantOnboardingRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(
                tenantOnboardingService.onboard(request, httpRequest)
        );
    }
}
