package com.example.eventify.controller;

import com.example.eventify.dto.EventCreateDTO;
import com.example.eventify.dto.EventResponseDTO;
import com.example.eventify.dto.EventSummaryDTO;
import com.example.eventify.mapper.EventMapper;
import com.example.eventify.service.EventService;
import com.example.eventify.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Eventos", description = "Catálogo desacoplado con DTOs, MapStruct, Record EventSummaryDTO, Slice y Soft Delete")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    // Escenario 1 y 7: Coexistencia de Record EventSummaryDTO con Slice en GET /api/events y /api/events/summary
    @GetMapping({"", "/summary"})
    @Operation(
            summary = "Catálogo resumido de alto rendimiento (Record + Slice)",
            description = "Retorna un Slice de EventSummaryDTO ordenado por fecha DESC sin ejecutar COUNT(*). " +
                    "Excluye automáticamente registros con borrado lógico (activo = false)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fragmento (Slice) de eventos resumidos recuperado con éxito"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Slice<EventSummaryDTO>> getEventSummaries(
            @Parameter(description = "Filtro parcial e insensible a mayúsculas por ciudad (ej. 'bog')")
            @RequestParam(required = false) String ciudad,
            @Parameter(description = "Filtro parcial e insensible a mayúsculas por categoría (ej. 'rock')")
            @RequestParam(required = false) String categoria,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventService.getEventSummaries(ciudad, categoria, pageable));
    }

    @GetMapping("/catalog")
    @Operation(
            summary = "Motor de búsqueda avanzada desacoplado (@EntityGraph + EventResponseDTO)",
            description = "Resuelve el problema N+1 trayendo Evento, Lugar y Categorías en una única consulta SQL y mapeando a EventResponseDTO."
    )
    public ResponseEntity<Slice<EventResponseDTO>> searchCatalog(
            @Parameter(description = "Ciudad de la sede (búsqueda parcial insensible)")
            @RequestParam(required = false) String ciudad,
            @Parameter(description = "Nombre de categoría temática (búsqueda parcial insensible)")
            @RequestParam(required = false) String categoria,
            @Parameter(description = "Capacidad mínima requerida en el recinto")
            @RequestParam(required = false) Integer capacidadMin,
            @Parameter(description = "Fecha inicial del rango (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha final del rango (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        Slice<EventResponseDTO> slice = eventService
                .getCatalogEvents(ciudad, categoria, capacidadMin, fechaInicio, fechaFin, pageable)
                .map(eventMapper::toResponseDTO);
        return ResponseEntity.ok(slice);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar detalle de un evento activo por ID (EventResponseDTO)",
            description = "Retorna un EventResponseDTO con venueName y categoryNames planos sin exponer la entidad JPA."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento encontrado",
                    content = @Content(schema = @Schema(implementation = EventResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado (ProblemDetail RFC 7807)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventResponseById(id));
    }

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo evento validado (EventCreateDTO)",
            description = "Valida campos obligatorios y @NoPastEvents, resuelve relaciones con MapStruct y retorna EventResponseDTO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento creado y vinculado exitosamente",
                    content = @Content(schema = @Schema(implementation = EventResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o fecha pasada (ProblemDetail con mapa de errores)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Venue o Categorías especificadas no existen",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<EventResponseDTO> createEvent(
            @Validated(ValidationGroups.OnCreate.class) @Valid @RequestBody EventCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un evento existente por ID",
            description = "Actualiza los datos y relaciones de un evento mediante EventCreateDTO y MapStruct."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = EventResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Errores de validación en los datos enviados",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Evento o Venue no encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventCreateDTO dto) {
        return ResponseEntity.ok(eventService.updateEvent(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Borrado lógico de un evento (Soft Delete)",
            description = "Por políticas de auditoría no realiza DELETE físico. Actualiza activo = false " +
                    "y el registro deja de aparecer en consultas gracias a @SQLRestriction."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Evento desactivado lógicamente (Soft Delete)"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado (ProblemDetail RFC 7807)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> softDeleteEvent(@PathVariable Long id) {
        eventService.softDeleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}