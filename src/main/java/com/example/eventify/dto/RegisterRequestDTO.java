package com.example.eventify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para registrar un nuevo usuario en la plataforma")
public record RegisterRequestDTO(
        @NotBlank(message = "El nombre de usuario no debe estar vacío")
        @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
        @Schema(description = "Nombre de usuario único", example = "spidey_admin")
        String username,

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El correo debe tener un formato válido")
        @Schema(description = "Correo electrónico", example = "admin@eventify.com")
        String email,

        @NotBlank(message = "La contraseña no debe estar vacía")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        @Schema(description = "Contraseña en texto plano (se encriptará con BCrypt)", example = " claveSegura123")
        String password,

        @Schema(description = "Rol del usuario (ROLE_ADMIN, ROLE_COORDINATOR, ROLE_USER)", example = "ROLE_ADMIN")
        String role
) {}