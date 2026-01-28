package com.IusCloud.auth.core.features.roles.service;

import com.IusCloud.auth.core.features.roles.domain.dto.RoleRequestDTO;
import com.IusCloud.auth.core.features.roles.domain.dto.RoleResponseDTO;
import com.IusCloud.auth.core.features.roles.domain.mapper.RoleMapper;
import com.IusCloud.auth.core.features.roles.domain.model.PermissionEntity;
import com.IusCloud.auth.core.features.roles.domain.model.RoleEntity;
import com.IusCloud.auth.core.features.roles.repository.PermissionRepository;
import com.IusCloud.auth.core.features.roles.repository.RoleRepository;
import com.IusCloud.auth.core.features.tenants.domain.model.TenantEntity;
import com.IusCloud.auth.core.features.tenants.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public Page<RoleResponseDTO> findAll(Pageable pageable) {
        return roleRepository.findAllByActiveTrue(pageable).map(roleMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public RoleResponseDTO findById(UUID id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Transactional
    public RoleResponseDTO create(RoleRequestDTO roleRequestDTO) {
        if (roleRepository.existsByTenantIdAndName(roleRequestDTO.getTenantId(), roleRequestDTO.getName())) {
            throw new RuntimeException("Role with name " + roleRequestDTO.getName() + " already exists for this tenant");
        }

        TenantEntity tenant = tenantRepository.findById(roleRequestDTO.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        RoleEntity entity = roleMapper.toEntity(roleRequestDTO);
        entity.setTenant(tenant);

        if (roleRequestDTO.getPermissionIds() != null && !roleRequestDTO.getPermissionIds().isEmpty()) {
            List<PermissionEntity> permissions = permissionRepository.findAllById(roleRequestDTO.getPermissionIds());
            entity.setPermissions(new HashSet<>(permissions));
        }

        return roleMapper.toDTO(roleRepository.save(entity));
    }

    @Transactional
    public RoleResponseDTO update(UUID id, RoleRequestDTO roleRequestDTO) {
        RoleEntity entity = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        roleMapper.updateEntity(entity, roleRequestDTO);

        if (roleRequestDTO.getPermissionIds() != null) {
            List<PermissionEntity> permissions = permissionRepository.findAllById(roleRequestDTO.getPermissionIds());
            entity.setPermissions(new HashSet<>(permissions));
        }

        return roleMapper.toDTO(roleRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        RoleEntity entity = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        entity.setDeletedAt(Instant.now());
        entity.setActive(false);
        roleRepository.save(entity);
    }
}
