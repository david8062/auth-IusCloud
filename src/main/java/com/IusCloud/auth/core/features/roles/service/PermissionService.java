package com.IusCloud.auth.core.features.roles.service;

import com.IusCloud.auth.core.features.roles.domain.dto.PermissionResponseDTO;
import com.IusCloud.auth.core.features.roles.domain.mapper.RoleMapper;
import com.IusCloud.auth.core.features.roles.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public List<PermissionResponseDTO> findAll() {
        return permissionRepository.findAll().stream()
                .map(roleMapper::toPermissionDTO)
                .collect(Collectors.toList());
    }
}
