package com.IusCloud.auth.core.features.users.domain.mapper;

import com.IusCloud.auth.core.features.roles.domain.mapper.RoleMapper;
import com.IusCloud.auth.core.features.users.domain.dto.UserRequestDTO;
import com.IusCloud.auth.core.features.users.domain.dto.UserResponseDTO;
import com.IusCloud.auth.core.features.users.domain.model.UserEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    @Mapping(source = "tenant.id", target = "tenantId")
    UserResponseDTO toDTO(UserEntity entity);

    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    UserEntity toEntity(UserRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    void updateEntity(@MappingTarget UserEntity entity, UserRequestDTO dto);
}
