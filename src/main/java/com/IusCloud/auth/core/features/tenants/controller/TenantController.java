package com.IusCloud.auth.core.features.tenants.controller;

import com.IusCloud.auth.core.features.tenants.domain.dto.TenantRequestDTO;
import com.IusCloud.auth.core.features.tenants.domain.dto.TenantResponseDTO;
import com.IusCloud.auth.core.features.tenants.service.TenantService;
import com.IusCloud.auth.shared.responses.ApiResponse;
import com.IusCloud.auth.shared.responses.PagedResponse;
import com.IusCloud.auth.shared.responses.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public ResponseEntity<PagedResponse<TenantResponseDTO>> getAll(Pageable pageable) {
        Page<TenantResponseDTO> page = tenantService.findAll(pageable);
        return ResponseUtil.paged(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseUtil.ok(tenantService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TenantResponseDTO>> create(@RequestBody TenantRequestDTO tenantRequestDTO) {
        return ResponseUtil.created(tenantService.create(tenantRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantResponseDTO>> update(@PathVariable UUID id, @RequestBody TenantRequestDTO tenantRequestDTO) {
        return ResponseUtil.ok(tenantService.update(id, tenantRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tenantService.delete(id);
        return ResponseUtil.noContent();
    }
}
