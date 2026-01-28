package com.IusCloud.auth.core.features.roles.controller;

import com.IusCloud.auth.core.features.roles.domain.dto.PermissionResponseDTO;
import com.IusCloud.auth.core.features.roles.service.PermissionService;
import com.IusCloud.auth.shared.responses.ListResponse;
import com.IusCloud.auth.shared.responses.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<ListResponse<PermissionResponseDTO>> getAll() {
        List<PermissionResponseDTO> permissions = permissionService.findAll();
        return ResponseUtil.list(permissions);
    }
}
