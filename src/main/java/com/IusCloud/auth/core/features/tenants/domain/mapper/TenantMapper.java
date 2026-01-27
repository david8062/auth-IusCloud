package com.IusCloud.auth.core.features.tenants.domain.mapper;

import com.IusCloud.auth.core.features.tenants.domain.dto.TenantRequestDTO;
import com.IusCloud.auth.core.features.tenants.domain.dto.TenantResponseDTO;
import com.IusCloud.auth.core.features.tenants.domain.model.TenantEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    TenantResponseDTO toDTO(TenantEntity entity);

    TenantEntity toEntity(TenantRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget TenantEntity entity, TenantRequestDTO dto);
}
