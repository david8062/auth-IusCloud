package com.IusCloud.auth.core.features.auth.service;

import com.IusCloud.auth.config.security.JwtService;
import com.IusCloud.auth.config.security.TenantAuthenticationDetails;
import com.IusCloud.auth.core.features.auth.domain.dto.LoginRequestDTO;
import com.IusCloud.auth.core.features.auth.domain.dto.LoginResponseDTO;
import com.IusCloud.auth.core.features.auth.domain.model.LoginAttemptEntity;
import com.IusCloud.auth.core.features.auth.repository.LoginAttemptRepository;
import com.IusCloud.auth.core.features.roles.domain.model.RoleEntity;
import com.IusCloud.auth.core.features.users.domain.dto.UserResponseDTO;
import com.IusCloud.auth.core.features.users.domain.mapper.UserMapper;
import com.IusCloud.auth.core.features.users.domain.model.UserEntity;
import com.IusCloud.auth.core.features.users.repository.UserRepository;
import com.IusCloud.auth.shared.tenant.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @Transactional(noRollbackFor = RuntimeException.class)
    public LoginResponseDTO login(LoginRequestDTO loginRequest, HttpServletRequest request) {

        String email = loginRequest.getEmail();
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        UUID tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant not resolved");
        }

        Optional<UserEntity> userOpt =
                userRepository.findByTenantIdAndEmail(tenantId, email);

        boolean success = false;
        UserEntity user = null;

        if (userOpt.isPresent()) {
            user = userOpt.get();
            if (
                    passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())
                            && Boolean.TRUE.equals(user.getActive())
            ) {
                success = true;
            }
        }

        saveLoginAttempt(email, ipAddress, userAgent, success, success ? user : null);

        if (!success) {
            throw new BadCredentialsException("Credenctial not match");
        }

        String token = jwtService.generateToken(user);

        List<String> roles = user.getRoles().stream()
                .map(RoleEntity::getName)
                .toList();

        return new LoginResponseDTO(
                token,
                "Bearer",
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles
        );
    }

    //Login para el proceso de onBoarding
    public LoginResponseDTO loginWithTenant(
            UUID tenantId,
            LoginRequestDTO loginRequest,
            HttpServletRequest request
    ) {
        String email = loginRequest.getEmail();
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        Optional<UserEntity> userOpt =
                userRepository.findByTenantIdAndEmail(tenantId, email);

        if (userOpt.isEmpty()) {
            saveLoginAttempt(email, ipAddress, userAgent, false, null);
            throw new BadCredentialsException("Credentials not match");
        }

        UserEntity user = userOpt.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())
                || !Boolean.TRUE.equals(user.getActive())) {

            saveLoginAttempt(email, ipAddress, userAgent, false, user);
            throw new BadCredentialsException("Credentials not match");
        }

        saveLoginAttempt(email, ipAddress, userAgent, true, user);

        String token = jwtService.generateToken(user);

        List<String> roles = user.getRoles().stream()
                .map(RoleEntity::getName)
                .toList();

        return new LoginResponseDTO(
                token,
                "Bearer",
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles
        );
    }




    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLoginAttempt(String email, String ipAddress, String userAgent, boolean success, UserEntity user) {
        LoginAttemptEntity attempt = new LoginAttemptEntity();
        attempt.setEmailAttempted(email);
        attempt.setIpAddress(ipAddress);
        attempt.setUserAgent(userAgent);
        attempt.setSuccess(success);
        attempt.setUser(user);
        loginAttemptRepository.save(attempt);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO me() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }

        UUID userId = UUID.fromString(authentication.getPrincipal().toString());

        TenantAuthenticationDetails details =
                (TenantAuthenticationDetails) authentication.getDetails();

        String tenantId = details.tenantId();

        UserEntity user = userRepository
                .findByIdAndTenantId(userId, UUID.fromString(tenantId))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return userMapper.toDTO(user);
    }

}
