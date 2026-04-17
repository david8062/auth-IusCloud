package com.IusCloud.auth.core.features.users.service;

import com.IusCloud.auth.core.features.auth.service.RefreshTokenService;
import com.IusCloud.auth.core.features.roles.domain.model.RoleEntity;
import com.IusCloud.auth.core.features.roles.repository.RoleRepository;
import com.IusCloud.auth.core.features.tenants.domain.model.TenantEntity;
import com.IusCloud.auth.core.features.tenants.repository.TenantRepository;
import com.IusCloud.auth.core.features.users.domain.dto.UserOwnerRequestDTO;
import com.IusCloud.auth.core.features.users.domain.dto.UserRequestDTO;
import com.IusCloud.auth.core.features.users.domain.dto.UserResponseDTO;
import com.IusCloud.auth.core.features.users.domain.mapper.UserMapper;
import com.IusCloud.auth.core.features.users.domain.model.UserEntity;
import com.IusCloud.auth.core.features.users.repository.UserRepository;
import com.IusCloud.auth.shared.exceptions.BusinessException;
import com.IusCloud.auth.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return userRepository.findAllByTenantIdAndActiveTrue(TenantContext.getTenantId(), pageable)
                .map(userMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validar tenant
        if (!user.getTenant().getId().equals(TenantContext.getTenantId())) {
            throw new RuntimeException("User not found");
        }

        return userMapper.toDTO(user);
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO userRequestDTO) {
        UUID tenantId = TenantContext.getTenantId();

        if (userRepository.existsByTenantIdAndEmail(tenantId, userRequestDTO.getEmail())) {
            throw new RuntimeException(
                    "User with email " + userRequestDTO.getEmail() + " already exists for this tenant");
        }

        if (userRepository.existsByTenantIdAndUsername(tenantId, userRequestDTO.getUsername())) {
            throw new RuntimeException(
                    "User with username " + userRequestDTO.getUsername() + " already exists for this tenant");
        }

        TenantEntity tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        UserEntity entity = userMapper.toEntity(userRequestDTO);
        entity.setTenant(tenant);
        entity.setPasswordHash(passwordEncoder.encode(userRequestDTO.getPassword()));

        if (userRequestDTO.getRoleIds() != null && !userRequestDTO.getRoleIds().isEmpty()) {
            List<RoleEntity> roles = roleRepository.findAllById(userRequestDTO.getRoleIds());
            // Validar que los roles pertenezcan al tenant
            for (RoleEntity role : roles) {
                if (!role.getTenant().getId().equals(tenantId)) {
                    throw new RuntimeException("Role " + role.getName() + " does not belong to this tenant");
                }
            }
            entity.setRoles(new HashSet<>(roles));
        }

        return userMapper.toDTO(userRepository.save(entity));
    }

    @Transactional
    public UserResponseDTO update(UUID id, UserRequestDTO userRequestDTO) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validar tenant
        if (!entity.getTenant().getId().equals(TenantContext.getTenantId())) {
            throw new RuntimeException("User not found");
        }

        userMapper.updateEntity(entity, userRequestDTO);

        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isBlank()) {
            entity.setPasswordHash(passwordEncoder.encode(userRequestDTO.getPassword()));
        }

        if (userRequestDTO.getRoleIds() != null) {
            List<RoleEntity> roles = roleRepository.findAllById(userRequestDTO.getRoleIds());
            for (RoleEntity role : roles) {
                if (!role.getTenant().getId().equals(TenantContext.getTenantId())) {
                    throw new RuntimeException("Role " + role.getName() + " does not belong to this tenant");
                }
            }
            entity.getRoles().clear();
            entity.getRoles().addAll(roles);
        }

        return userMapper.toDTO(userRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validar tenant
        if (!entity.getTenant().getId().equals(TenantContext.getTenantId())) {
            throw new RuntimeException("User not found");
        }

        // Revocar todos los refresh tokens del usuario
        refreshTokenService.revokeAllUserTokens(id);

        entity.setDeletedAt(Instant.now());
        entity.setActive(false);
        userRepository.save(entity);
    }

    @Transactional
    public void createOwner(UUID tenantId, UserOwnerRequestDTO dto) {
        // Este método se usa durante el onboarding, donde el TenantContext podría no
        // estar establecido aún
        // o se pasa explícitamente. Se mantiene la lógica original.

        TenantEntity tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new BusinessException("TENANT_NOT_FOUND", "Tenant not found"));

        RoleEntity adminRole = roleRepository
                .findByNameAndTenantId("ADMINISTRATOR", tenantId)
                .orElseThrow(() -> new BusinessException(
                        "ADMIN_ROLE_NOT_FOUND",
                        "Admin role not initialized"));

        UserEntity user = new UserEntity();
        user.setTenant(tenant);
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setActive(true);

        user.setRoles(Set.of(adminRole));

        userMapper.toDTO(userRepository.save(user));
    }

}
