package com.IusCloud.auth.shared.commandLineRunner;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.IusCloud.auth.core.features.roles.domain.model.PermissionEntity;
import com.IusCloud.auth.core.features.roles.repository.PermissionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActionSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;

    // Recursos del sistema — agregar aquí cuando llegue un módulo nuevo
    private static final List<String> RESOURCES = List.of(
            // ms_auth
            "USERS",
            "ROLES",
            "TENANT",
            // ms_legal_core
            "BRANCHES",
            "SCHEDULES",
            "CASES",
            "CLIENTS",
            "HEARINGS",
            "DOCUMENTS",
            "APPOINTMENTS");

    // Acciones fijas — nunca cambian
    private static final List<String> ACTIONS = List.of(
            "READ",
            "WRITE",
            "UPDATE",
            "DELETE");

    @Override
    public void run(String... args) {
        seedPermissions();
    }

    private void seedPermissions() {
        List<String> expectedCodes = buildExpectedCodes();

        // Busca cuáles faltan (por si ya hay algunos y se agregan recursos nuevos)
        Set<String> existingCodes = permissionRepository.findAll()
                .stream()
                .map(PermissionEntity::getCode)
                .collect(Collectors.toSet());

        List<PermissionEntity> missing = expectedCodes.stream()
                .filter(code -> !existingCodes.contains(code))
                .map(code -> {
                    String[] parts = code.split(":"); // ["CASES", "READ"]
                    String resource = capitalize(parts[0]); // "Cases"
                    String action = capitalize(parts[1]); // "Read"
                    return createPermission(code, action + " " + resource);
                })
                .toList();

        if (missing.isEmpty()) {
            log.info("Permissions already up to date ({} total).", existingCodes.size());
            return;
        }

        permissionRepository.saveAll(missing);
        log.info("Seeded {} new permissions. Total: {}.",
                missing.size(), existingCodes.size() + missing.size());
    }

    private List<String> buildExpectedCodes() {
        // Genera: USERS:READ, USERS:WRITE, USERS:UPDATE, USERS:DELETE, ROLES:READ ...
        return RESOURCES.stream()
                .flatMap(r -> ACTIONS.stream().map(a -> r + ":" + a))
                .toList();
    }

    private PermissionEntity createPermission(String code, String description) {
        PermissionEntity p = new PermissionEntity();
        p.setCode(code);
        p.setDescription(description);
        p.setActive(true);
        return p;
    }

    private String capitalize(String s) {
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}