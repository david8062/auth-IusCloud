package com.IusCloud.auth.core.features.users.controller;

import com.IusCloud.auth.core.features.users.domain.dto.UserRequestDTO;
import com.IusCloud.auth.core.features.users.domain.dto.UserResponseDTO;
import com.IusCloud.auth.core.features.users.service.UserService;
import com.IusCloud.auth.shared.responses.ApiResponse;
import com.IusCloud.auth.shared.responses.PagedResponse;
import com.IusCloud.auth.shared.responses.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<PagedResponse<UserResponseDTO>> getAll(Pageable pageable) {
        Page<UserResponseDTO> page = userService.findAll(pageable);
        return ResponseUtil.paged(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseUtil.ok(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> create(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseUtil.created(userService.create(userRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseUtil.ok(userService.update(id, userRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseUtil.noContent();
    }
}
