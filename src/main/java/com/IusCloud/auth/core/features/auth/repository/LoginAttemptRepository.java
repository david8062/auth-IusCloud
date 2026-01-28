package com.IusCloud.auth.core.features.auth.repository;

import com.IusCloud.auth.core.features.auth.domain.model.LoginAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttemptEntity, UUID> {
}
