package com.auth.service.auth_service.controller;

import com.auth.service.auth_service.dto.AuthRequestDTO;
import com.auth.service.auth_service.dto.AuthResponseDTO;
import com.auth.service.auth_service.service.IAuthenticationService;
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
public class AuthenticationController {

    private final IAuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestBody @Valid AuthRequestDTO authRequestDTO) {
        return ResponseEntity.ok(authenticationService.loginUser(authRequestDTO));
    }
}
