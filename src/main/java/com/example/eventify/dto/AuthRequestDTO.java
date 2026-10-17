package com.example.eventify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciales para iniciar sesión")
public record AuthRequestDTO(
        @NotBlank(message = "El username es obligatorio")
        @Schema(description = "Nombre de usuario", example = "admin")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Schema(description = "Contraseña del usuario", example = "admin123")
        String password
) {}