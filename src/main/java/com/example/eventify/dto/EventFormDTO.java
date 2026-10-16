package com.example.eventify.dto;

import com.example.eventify.validation.NoPastEvents;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar o actualizar un evento desde el formulario administrativo")
public class EventFormDTO {

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nombre del evento", example = "Festival Estéreo Picnic")
    private String nombre;

    @NotNull(message = "La fecha es obligatoria")
    @NoPastEvents
    @Schema(description = "Fecha del evento", example = "2026-11-25")
    private LocalDate fecha;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
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