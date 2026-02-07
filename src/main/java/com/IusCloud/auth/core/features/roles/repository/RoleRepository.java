package com.IusCloud.auth.core.features.roles.repository;

import com.IusCloud.auth.core.base.BaseRepository;
import com.IusCloud.auth.core.features.roles.domain.model.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends BaseRepository<RoleEntity, UUID> {
    boolean existsByTenantIdAndName(UUID tenantId, String name);

    Optional<RoleEntity> findByNameAndTenantId(String name, UUID tenantId);

    Page<RoleEntity> findAllByTenantIdAndActiveTrue(UUID tenantId, Pageable pageable);

    Optional<RoleEntity> findByIdAndTenantId(UUID id, UUID tenantId);
}
