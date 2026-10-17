package com.example.eventify.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de autenticación con el Token JWT generado")
public record AuthResponseDTO(
        @Schema(description = "Token JWT de acceso (Bearer)")
        String token,

        @Schema(description = "Nombre de usuario autenticado", example = "admin")
        String username,

        @Schema(description = "Rol asignado", example = "ROLE_ADMIN")
        String role
) {}