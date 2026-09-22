package com.auth.service.auth_service.controller;

import com.auth.service.auth_service.dto.AuthRequestDTO;
import com.auth.service.auth_service.dto.AuthResponseDTO;
import com.auth.service.auth_service.dto.ErrorResponseDTO;
import com.auth.service.auth_service.security.config.service.IAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticación", description = "Endpoints para el inicio de sesión y validación de credenciales")
public class AuthenticationController {

    private final IAuthenticationService authenticationService;

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario mediante username y password, retornando un token JWT de acceso si las credenciales son válidas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticación exitosa. Retorna el token JWT y los datos del usuario.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error de validación en los campos de entrada (por ejemplo, campos vacíos o nulos).",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas (usuario no encontrado o contraseña incorrecta).",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestBody @Valid AuthRequestDTO authRequestDTO) {
        return ResponseEntity.ok(authenticationService.loginUser(authRequestDTO));
    }
}
