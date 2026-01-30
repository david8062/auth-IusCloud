package com.IusCloud.auth.core.features.tenants.repository;

import com.IusCloud.auth.core.base.BaseRepository;
import com.IusCloud.auth.core.features.tenants.domain.model.TenantEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends BaseRepository<TenantEntity, UUID> {
    boolean existsBySlug(String slug);
    Optional<TenantEntity> findBySlugAndActiveTrue(String slug);
}
