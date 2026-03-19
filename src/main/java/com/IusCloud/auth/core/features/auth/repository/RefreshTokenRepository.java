package com.IusCloud.auth.core.features.auth.repository;

import com.IusCloud.auth.core.base.BaseRepository;
import com.IusCloud.auth.core.features.auth.domain.model.RefreshTokenEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends BaseRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByToken(String token);
    
    List<RefreshTokenEntity> findAllByUserIdAndRevokedFalse(UUID userId);
}
