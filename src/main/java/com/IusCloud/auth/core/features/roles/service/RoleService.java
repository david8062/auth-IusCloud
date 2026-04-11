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
import com.IusCloud.auth.core.features.users.repository.UserRepository;
import com.IusCloud.auth.shared.redis.PermissionRedisService;
import com.IusCloud.auth.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;
    private final UserRepository userRepository;
    private final PermissionRedisService permissionRedisService;

    @Transactional(readOnly = true)
    public Page<RoleResponseDTO> findAll(Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return roleRepository.findAllByTenantIdAndActiveTrue(tenantId, pageable).map(roleMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public RoleResponseDTO findById(UUID id) {
        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Validar que el rol pertenezca al tenant actual
        if (!role.getTenant().getId().equals(TenantContext.getTenantId())) {
            throw new RuntimeException("Role not found"); // Ocultar existencia
        }

        return roleMapper.toDTO(role);
    }

    @Transactional
    public RoleResponseDTO create(RoleRequestDTO roleRequestDTO) {
        UUID tenantId = TenantContext.getTenantId();

        if (roleRepository.existsByTenantIdAndName(tenantId, roleRequestDTO.getName())) {
            throw new RuntimeException("Role with name " + roleRequestDTO.getName() + " already exists for this tenant");
        }

        TenantEntity tenant = tenantRepository.findById(tenantId)
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

        // Validar tenant
        if (!entity.getTenant().getId().equals(TenantContext.getTenantId())) {
            throw new RuntimeException("Role not found");
        }

        roleMapper.updateEntity(entity, roleRequestDTO);

        if (roleRequestDTO.getPermissionIds() != null) {
            List<PermissionEntity> permissions = permissionRepository.findAllById(roleRequestDTO.getPermissionIds());
            entity.setPermissions(new HashSet<>(permissions));

            userRepository.findAllByRoles_Id(id)
                    .forEach(u -> permissionRedisService.deletePermissions(u.getTenant().getId(), u.getId()));
        }

        return roleMapper.toDTO(roleRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        RoleEntity entity = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Validar tenant
        if (!entity.getTenant().getId().equals(TenantContext.getTenantId())) {
            throw new RuntimeException("Role not found");
        }

        entity.setDeletedAt(Instant.now());
        entity.setActive(false);
        roleRepository.save(entity);
    }
}
