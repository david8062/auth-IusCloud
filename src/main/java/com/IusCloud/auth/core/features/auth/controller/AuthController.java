package com.IusCloud.auth.core.features.auth.controller;

import com.IusCloud.auth.core.features.auth.domain.dto.LoginRequestDTO;
import com.IusCloud.auth.core.features.auth.domain.dto.LoginResponseDTO;
import com.IusCloud.auth.core.features.auth.service.AuthService;
import com.IusCloud.auth.core.features.users.domain.dto.UserResponseDTO;
import com.IusCloud.auth.shared.responses.ApiResponse;
import com.IusCloud.auth.shared.responses.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletRequest request) {
        return ResponseUtil.ok(authService.login(loginRequest, request));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> me() {
        return ResponseUtil.ok(authService.me());
    }

}
