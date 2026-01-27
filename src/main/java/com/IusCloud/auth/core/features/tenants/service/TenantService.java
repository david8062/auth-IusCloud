package com.IusCloud.auth.core.features.tenants.service;

import com.IusCloud.auth.core.features.tenants.domain.dto.TenantRequestDTO;
import com.IusCloud.auth.core.features.tenants.domain.dto.TenantResponseDTO;
import com.IusCloud.auth.core.features.tenants.domain.mapper.TenantMapper;
import com.IusCloud.auth.core.features.tenants.domain.model.TenantEntity;
import com.IusCloud.auth.core.features.tenants.domain.model.TenantEnum;
import com.IusCloud.auth.core.features.tenants.repository.TenantRepository;
import com.IusCloud.auth.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    @Transactional(readOnly = true)
    public Page<TenantResponseDTO> findAll(Pageable pageable) {
        return tenantRepository
                .findAllByActiveTrue(pageable)
                .map(tenantMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public TenantResponseDTO findById(UUID id) {
        return tenantRepository.findById(id)
                .map(tenantMapper::toDTO)
                .orElseThrow(() ->
                        new BusinessException(
                                "TENANT_NOT_FOUND",
                                "Tenant not found with id: " + id
                        )
                );
    }

    @Transactional
    public TenantResponseDTO create(TenantRequestDTO dto) {

        String slug = dto.getSlug().toLowerCase().trim();

        if (tenantRepository.existsBySlug(slug)) {
            throw new BusinessException(
                    "TENANT_SLUG_ALREADY_EXISTS",
                    "A tenant with slug '" + slug + "' already exists"
            );
        }

        validateDates(dto);

        TenantEntity entity = tenantMapper.toEntity(dto);
        entity.setSlug(slug);

        if (entity.getStatus() == null) {
            entity.setStatus(TenantEnum.TRIAL);
        }

        if (entity.getActive() == null) {
            entity.setActive(true);
        }

        TenantEntity saved = tenantRepository.save(entity);
        return tenantMapper.toDTO(saved);
    }

    @Transactional
    public TenantResponseDTO update(UUID id, TenantRequestDTO dto) {

        TenantEntity entity = tenantRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                "TENANT_NOT_FOUND",
                                "Tenant not found with id: " + id
                        )
                );

        tenantMapper.updateEntity(entity, dto);
        return tenantMapper.toDTO(tenantRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {

        TenantEntity entity = tenantRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                "TENANT_NOT_FOUND",
                                "Tenant not found with id: " + id
                        )
                );

        entity.setDeletedAt(Instant.now());
        entity.setActive(false);

        tenantRepository.save(entity);
    }

    private void validateDates(TenantRequestDTO dto) {

        if (dto.getTrialEndsAt() != null && dto.getSubscriptionEndsAt() != null) {
            if (dto.getSubscriptionEndsAt().isBefore(dto.getTrialEndsAt())) {
                throw new BusinessException(
                        "INVALID_SUBSCRIPTION_DATES",
                        "subscriptionEndsAt must be after trialEndsAt"
                );
            }
        }
    }
}
