package com.IusCloud.auth.core.features.tenants.controller;

import com.IusCloud.auth.core.features.tenants.domain.dto.TenantRequestDTO;
import com.IusCloud.auth.core.features.tenants.domain.dto.TenantResponseDTO;
import com.IusCloud.auth.core.features.tenants.service.TenantService;
import com.IusCloud.auth.shared.responses.ApiResponse;
import com.IusCloud.auth.shared.responses.PagedResponse;
import com.IusCloud.auth.shared.responses.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    @PreAuthorize("hasAuthority('TENANT:READ')")
    public ResponseEntity<PagedResponse<TenantResponseDTO>> getAll(Pageable pageable) {
        Page<TenantResponseDTO> page = tenantService.findAll(pageable);
        return ResponseUtil.paged(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TENANT:READ')")
    public ResponseEntity<ApiResponse<TenantResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseUtil.ok(tenantService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('TENANT:WRITE')")
    public ResponseEntity<ApiResponse<TenantResponseDTO>> create(@Valid @RequestBody  TenantRequestDTO tenantRequestDTO) {
        return ResponseUtil.created(tenantService.create(tenantRequestDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TENANT:UPDATE')")
    public ResponseEntity<ApiResponse<TenantResponseDTO>> update(@PathVariable UUID id, @RequestBody TenantRequestDTO tenantRequestDTO) {
        return ResponseUtil.ok(tenantService.update(id, tenantRequestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TENANT:DELETE')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tenantService.delete(id);
        return ResponseUtil.noContent();
    }
}
