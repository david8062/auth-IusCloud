package com.IusCloud.auth.core.features.roles.repository;

import com.IusCloud.auth.core.base.BaseRepository;
import com.IusCloud.auth.core.features.roles.domain.model.PermissionEntity;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PermissionRepository extends BaseRepository<PermissionEntity, UUID> {
}
