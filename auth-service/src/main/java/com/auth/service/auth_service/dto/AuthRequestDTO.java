package com.auth.service.auth_service.dto;


import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(@NotBlank String username,@NotBlank String password) {
}
