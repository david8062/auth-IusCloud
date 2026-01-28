package com.IusCloud.auth.core.features.auth.service;

import com.IusCloud.auth.core.features.auth.domain.dto.LoginRequestDTO;
import com.IusCloud.auth.core.features.auth.domain.dto.LoginResponseDTO;
import com.IusCloud.auth.core.features.auth.domain.model.LoginAttemptEntity;
import com.IusCloud.auth.core.features.auth.repository.LoginAttemptRepository;
import com.IusCloud.auth.core.features.users.domain.model.UserEntity;
import com.IusCloud.auth.core.features.users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest, HttpServletRequest request) {
        String email = loginRequest.getEmail();
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        Optional<UserEntity> userOpt;
        if (loginRequest.getTenantId() != null) {
             userOpt = userRepository.findByTenantIdAndEmail(loginRequest.getTenantId(), email);
        } else {
             // Si no se envía tenantId, buscamos el primer usuario con ese email (podría mejorarse si el email es único globalmente o requerir tenantId)
             userOpt = userRepository.findAll().stream()
                     .filter(u -> u.getEmail().equals(email))
                     .findFirst();
        }

        boolean success = false;
        UserEntity user = null;

        if (userOpt.isPresent()) {
            user = userOpt.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash()) && Boolean.TRUE.equals(user.getActive())) {
                success = true;
            }
        }

        // Registrar intento de login
        LoginAttemptEntity attempt = new LoginAttemptEntity();
        attempt.setEmailAttempted(email);
        attempt.setIpAddress(ipAddress);
        attempt.setUserAgent(userAgent);
        attempt.setSuccess(success);
        if (success) {
            attempt.setUser(user);
        }
        loginAttemptRepository.save(attempt);

        if (!success) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // TODO: Generar JWT real aquí
        String token = "dummy-jwt-token"; 

        return new LoginResponseDTO(token, "Bearer", user.getEmail(), user.getFirstName(), user.getLastName());
    }
}
