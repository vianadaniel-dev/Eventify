package com.example.eventify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Proyección ligera inmutable (Record) para listados masivos de eventos")
public record EventSummaryDTO(
        @Schema(description = "ID único del evento", example = "1")
        Long id,
        @Schema(description = "Nombre del evento", example = "Concierto de ROCK")
        String nombreEvento,
        @Schema(description = "Fecha programada", example = "2026-11-20")
        LocalDate fecha,
        @Schema(description = "Nombre de la sede física", example = "Movistar Arena")
        String nombreLugar,
        @Schema(description = "Ciudad de la sede", example = "Bogotá")
        String ciudad
) {
    // Constructor de 4 argumentos por si se invoca sin ID en JPQL
    public EventSummaryDTO(String nombreEvento, LocalDate fecha, String nombreLugar, String ciudad) {
        this(null, nombreEvento, fecha, nombreLugar, ciudad);
    }
}