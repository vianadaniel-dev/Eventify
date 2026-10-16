package com.example.eventify.dto;

import com.example.eventify.validation.NoPastEvents;
import com.example.eventify.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de entrada para crear o actualizar un Evento")
public class EventCreateDTO {

    @Null(groups = ValidationGroups.OnCreate.class, message = "El ID no debe enviarse en la creación")
    @NotNull(groups = ValidationGroups.OnUpdate.class, message = "El ID es obligatorio en la actualización")
    @Schema(description = "ID del evento (solo para validación en actualización)", example = "1")
    private Long id;

    @NotBlank(message = "no debe estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nombre del evento", example = "Festival Estéreo Picnic", minLength = 3, maxLength = 100)
    private String nombre;

    @NotNull(message = "La fecha del evento es obligatoria")
    @NoPastEvents(message = "debe ser una fecha futura")
    @Schema(description = "Fecha programada del evento (hoy o futura)", example = "2026-11-25")
    private LocalDate fecha;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    @Schema(description = "Descripción del evento", example = "Tres días de música en vivo", maxLength = 500)
    private String descripcion;

    @Email(message = "El correo del organizador debe tener un formato válido")
    @Schema(description = "Correo de contacto del organizador (opcional)", example = "contacto@eventify.com")
    private String emailOrganizador;

    @NotNull(message = "Debe seleccionar un lugar (Venue)")
    @Schema(description = "ID de la sede física (Venue)", example = "1")
    private Long venueId;

    @Builder.Default
    @NotEmpty(message = "Debe seleccionar al menos una categoría")
    @Schema(description = "IDs de las categorías asociadas", example = "[1, 3, 5]")
    private Set<Long> categoryIds = new HashSet<>();
}