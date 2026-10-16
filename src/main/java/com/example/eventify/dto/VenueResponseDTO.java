package com.example.eventify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de salida de un Lugar (Venue)")
public class VenueResponseDTO {

    @Schema(description = "ID único del lugar", example = "1")
    private Long id;

    @Schema(description = "Nombre del lugar", example = "Movistar Arena")
    private String nombre;

    @Schema(description = "Dirección física", example = "Dg. 61c #26-36")
    private String direccion;

    @Schema(description = "Ciudad", example = "Bogotá")
    private String ciudad;

    @Schema(description = "Capacidad máxima", example = "14000")
    private Integer capacidad;
}