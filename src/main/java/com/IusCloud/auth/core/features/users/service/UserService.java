package com.IusCloud.auth.core.features.users.service;

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


    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return userRepository.findAllByTenantIdAndActiveTrue(TenantContext.getTenantId(), pageable).map(userMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(UUID id) {
        return userRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                .map(userMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByTenantIdAndEmail(userRequestDTO.getTenantId(), userRequestDTO.getEmail())) {
            throw new RuntimeException("User with email " + userRequestDTO.getEmail() + " already exists for this tenant");
        }

        TenantEntity tenant = tenantRepository.findById(userRequestDTO.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        UserEntity entity = userMapper.toEntity(userRequestDTO);
        entity.setTenant(tenant);
        entity.setPasswordHash(passwordEncoder.encode(userRequestDTO.getPassword()));

        if (userRequestDTO.getRoleIds() != null && !userRequestDTO.getRoleIds().isEmpty()) {
            List<RoleEntity> roles = roleRepository.findAllById(userRequestDTO.getRoleIds());
            entity.setRoles(new HashSet<>(roles));
        }

        return userMapper.toDTO(userRepository.save(entity));
    }

    @Transactional
    public UserResponseDTO update(UUID id, UserRequestDTO userRequestDTO) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateEntity(entity, userRequestDTO);

        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isBlank()) {
            entity.setPasswordHash(passwordEncoder.encode(userRequestDTO.getPassword()));
        }

        if (userRequestDTO.getRoleIds() != null) {
            List<RoleEntity> roles = roleRepository.findAllById(userRequestDTO.getRoleIds());
            entity.setRoles(new HashSet<>(roles));
        }

        return userMapper.toDTO(userRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        entity.setDeletedAt(Instant.now());
        entity.setActive(false);
        userRepository.save(entity);
    }

    @Transactional
    public UserResponseDTO createOwner(UUID tenantId, UserOwnerRequestDTO dto) {

        TenantEntity tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new BusinessException("TENANT_NOT_FOUND", "Tenant not found"));

        RoleEntity adminRole = roleRepository
                .findByNameAndTenantId("ADMINISTRATOR",tenantId )
                .orElseThrow(() -> new BusinessException(
                        "ADMIN_ROLE_NOT_FOUND",
                        "Admin role not initialized"
                ));

        UserEntity user = new UserEntity();
        user.setTenant(tenant);
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setActive(true);

        user.setRoles(Set.of(adminRole));

        return userMapper.toDTO(userRepository.save(user));
    }


}
