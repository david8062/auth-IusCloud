package com.IusCloud.auth.core.features.users.repository;

import com.IusCloud.auth.core.base.BaseRepository;
import com.IusCloud.auth.core.features.users.domain.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<UserEntity, UUID> {

    boolean existsByTenantIdAndEmail(UUID tenantId, String email);

    Optional<UserEntity> findByTenantIdAndEmail(UUID tenantId, String email);

    Optional<UserEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByTenantId(UUID tenantId);

    Page<UserEntity> findAllByTenantIdAndActiveTrue(UUID tenantId, Pageable pageable);
}
