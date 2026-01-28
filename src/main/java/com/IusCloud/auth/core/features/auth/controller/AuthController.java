package com.IusCloud.auth.core.features.auth.controller;

import com.IusCloud.auth.core.features.auth.domain.dto.LoginRequestDTO;
import com.IusCloud.auth.core.features.auth.domain.dto.LoginResponseDTO;
import com.IusCloud.auth.core.features.auth.service.AuthService;
import com.IusCloud.auth.shared.responses.ApiResponse;
import com.IusCloud.auth.shared.responses.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletRequest request) {
        return ResponseUtil.ok(authService.login(loginRequest, request));
    }
}
