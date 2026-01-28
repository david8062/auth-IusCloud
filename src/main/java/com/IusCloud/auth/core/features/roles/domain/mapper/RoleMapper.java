package com.IusCloud.auth.core.features.roles.domain.mapper;

import com.IusCloud.auth.core.features.roles.domain.dto.PermissionResponseDTO;
import com.IusCloud.auth.core.features.roles.domain.dto.RoleRequestDTO;
import com.IusCloud.auth.core.features.roles.domain.dto.RoleResponseDTO;
import com.IusCloud.auth.core.features.roles.domain.model.PermissionEntity;
import com.IusCloud.auth.core.features.roles.domain.model.RoleEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(source = "tenant.id", target = "tenantId")
    RoleResponseDTO toDTO(RoleEntity entity);

    PermissionResponseDTO toPermissionDTO(PermissionEntity entity);

    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    RoleEntity toEntity(RoleRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    void updateEntity(@MappingTarget RoleEntity entity, RoleRequestDTO dto);
}
