package com.IusCloud.auth.core.features.users.repository;

import com.IusCloud.auth.core.base.BaseRepository;
import com.IusCloud.auth.core.features.users.domain.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<UserEntity, UUID> {

    boolean existsByTenantIdAndEmail(UUID tenantId, String email);

    boolean existsByTenantIdAndUsername(UUID tenantId, String username);

    Optional<UserEntity> findByTenantIdAndEmail(UUID tenantId, String email);

    @Query("SELECT u FROM UserEntity u WHERE u.tenant.id = :tenantId AND (u.email = :identifier OR u.username = :identifier)")
    Optional<UserEntity> findByTenantIdAndEmailOrUsername(@Param("tenantId") UUID tenantId, @Param("identifier") String identifier);

    Optional<UserEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByTenantId(UUID tenantId);

    Page<UserEntity> findAllByTenantIdAndActiveTrue(UUID tenantId, Pageable pageable);

    List<UserEntity> findAllByRoles_Id(UUID roleId);
}
