package com.IusCloud.auth.core.features.roles.controller;

import com.IusCloud.auth.core.features.roles.domain.dto.RoleRequestDTO;
import com.IusCloud.auth.core.features.roles.domain.dto.RoleResponseDTO;
import com.IusCloud.auth.core.features.roles.service.RoleService;
import com.IusCloud.auth.shared.responses.ApiResponse;
import com.IusCloud.auth.shared.responses.PagedResponse;
import com.IusCloud.auth.shared.responses.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<PagedResponse<RoleResponseDTO>> getAll(Pageable pageable) {
        Page<RoleResponseDTO> page = roleService.findAll(pageable);
        return ResponseUtil.paged(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseUtil.ok(roleService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponseDTO>> create(@Valid @RequestBody RoleRequestDTO roleRequestDTO) {
        return ResponseUtil.created(roleService.create(roleRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody RoleRequestDTO roleRequestDTO) {
        return ResponseUtil.ok(roleService.update(id, roleRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roleService.delete(id);
        return ResponseUtil.noContent();
    }
}
