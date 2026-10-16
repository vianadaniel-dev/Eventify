package com.example.eventify.dto;

import com.example.eventify.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de entrada para crear o actualizar un Lugar (Venue)")
public class VenueCreateDTO {

    @Null(groups = ValidationGroups.OnCreate.class, message = "El ID no debe enviarse al crear un lugar")
    @NotNull(groups = ValidationGroups.OnUpdate.class, message = "El ID es obligatorio al actualizar un lugar")
    @Schema(description = "ID del lugar (solo requerido en actualización)", example = "1")
    private Long id;

    @NotBlank(message = "El nombre del lugar es obligatorio")
    @Size(min = 3, max = 120, message = "El nombre del lugar debe tener entre 3 y 120 caracteres")
    @Schema(description = "Nombre del lugar", example = "Movistar Arena", minLength = 3, maxLength = 120)
    private String nombre;

    @NotBlank(message = "La dirección del lugar es obligatoria")
    @Size(min = 5, max = 255, message = "La dirección debe tener entre 5 y 255 caracteres")
    @Schema(description = "Dirección física del lugar", example = "Dg. 61c #26-36", minLength = 5, maxLength = 255)
    private String direccion;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(min = 2, max = 100, message = "La ciudad debe tener entre 2 y 100 caracteres")
    @Schema(description = "Ciudad donde se ubica el lugar", example = "Bogotá", minLength = 2, maxLength = 100)
    private String ciudad;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor a cero")
    @Max(value = 200000, message = "La capacidad no puede superar los 200,000 asistentes")
    @Schema(description = "Capacidad máxima del lugar", example = "14000", minimum = "1", maximum = "200000")
    private Integer capacidad;
}