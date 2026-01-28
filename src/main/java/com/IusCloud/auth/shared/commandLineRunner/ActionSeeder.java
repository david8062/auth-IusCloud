package com.IusCloud.auth.shared.commandLineRunner;

import com.IusCloud.auth.core.features.roles.domain.model.PermissionEntity;
import com.IusCloud.auth.core.features.roles.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActionSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) throws Exception {
        seedPermissions();
    }

    private void seedPermissions() {
        if (permissionRepository.count() > 0) {
            log.info("Permissions already seeded.");
            return;
        }

        log.info("Seeding permissions...");

        List<PermissionEntity> permissions = List.of(
                // ROLES
                createPermission("ROLE_READ", "Ver roles"),
                createPermission("ROLE_WRITE", "Crear y modificar roles"),
                // USERS
                createPermission("USER_READ", "Ver usuarios"),
                createPermission("USER_WRITE", "Crear y modificar usuarios"),
                // TENANTS
                createPermission("TENANT_READ", "Ver información del tenant"),
                createPermission("TENANT_ADMIN", "Administrar tenant")
        );

        permissionRepository.saveAll(permissions);
        log.info("Permissions seeded successfully.");
    }

    private PermissionEntity createPermission(String code, String description) {
        PermissionEntity permission = new PermissionEntity();
        permission.setCode(code);
        permission.setDescription(description);
        return permission;
    }
}
