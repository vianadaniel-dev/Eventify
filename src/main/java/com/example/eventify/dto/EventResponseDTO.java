package com.example.eventify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de salida desacoplados de un Evento")
public class EventResponseDTO {

    @Schema(description = "ID único del evento", example = "1")
    private Long id;

    @Schema(description = "Nombre del evento", example = "Festival Estéreo Picnic")
    private String nombre;

    @Schema(description = "Fecha del evento", example = "2026-11-25")
    private LocalDate fecha;

    @Schema(description = "Descripción del evento", example = "Tres días de música en vivo")
    private String descripcion;

    @Schema(description = "Nombre plano del lugar (Venue)", example = "Movistar Arena")
    private String venueName;

    @Schema(description = "Nombres planos de las categorías asociadas", example = "[\"Conciertos\", \"Festival\"]")
    private Set<String> categoryNames;
}