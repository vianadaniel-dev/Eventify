package com.example.eventify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar o actualizar un evento con sus relaciones")
public class EventFormDTO {

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Schema(description = "Nombre del evento", example = "Festival Estéreo Picnic")
    private String nombre;

    @NotNull(message = "La fecha es obligatoria")
    @Schema(description = "Fecha del evento", example = "2026-11-25")
    private LocalDate fecha;

    @Schema(description = "Descripción del evento", example = "Tres días de música en vivo")
    private String descripcion;

    @NotNull(message = "Debe seleccionar un lugar (Venue)")
    @Schema(description = "ID de la sede física (Venue)", example = "1")
    private Long venueId;

    @Builder.Default
    @NotEmpty(message = "Debe seleccionar al menos una categoría")
    @Schema(description = "IDs de las categorías asociadas", example = "[1, 6]")
    private Set<Long> categoryIds = new HashSet<>();
}