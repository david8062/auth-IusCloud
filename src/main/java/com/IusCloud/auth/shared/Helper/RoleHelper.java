package com.IusCloud.auth.shared.Helper;

import com.IusCloud.auth.core.features.roles.domain.model.RoleEntity;
import com.IusCloud.auth.core.features.roles.repository.PermissionRepository;
import com.IusCloud.auth.core.features.roles.repository.RoleRepository;
import com.IusCloud.auth.core.features.tenants.domain.model.TenantEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@AllArgsConstructor
public class RoleHelper {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;


    public RoleEntity createAdminRole(TenantEntity tenant) {
        RoleEntity admin = new RoleEntity();
        admin.setName("ADMINISTRATOR");
        admin.setTenant(tenant);
        admin.setActive(true);

        admin.setPermissions(
                new HashSet<>(permissionRepository.findAll())
        );

        return roleRepository.save(admin);
    }

}
