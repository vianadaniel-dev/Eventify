package com.example.eventify.controller;

import com.example.eventify.dto.EventFormDTO;
import com.example.eventify.dto.EventSummaryDTO;
import com.example.eventify.model.Event;
import com.example.eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Eventos", description = "Catálogo optimizado con Records, Slice, filtros relacionales y Soft Delete")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/summary")
    @Operation(
            summary = "Catálogo resumido de alto rendimiento (Record + Slice)",
            description = "Retorna un Slice de EventSummaryDTO ordenado por fecha DESC sin ejecutar COUNT(*). " +
                    "Excluye automáticamente registros con borrado lógico (activo = false)."
    )
    @ApiResponse(responseCode = "200", description = "Fragmento (Slice) de eventos resumidos recuperado con éxito")
    public ResponseEntity<Slice<EventSummaryDTO>> getEventSummaries(
            @Parameter(description = "Filtro parcial e insensible a mayúsculas por ciudad (ej. 'bog')")
            @RequestParam(required = false) String ciudad,
            @Parameter(description = "Filtro parcial e insensible a mayúsculas por categoría (ej. 'rock')")
            @RequestParam(required = false) String categoria,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventService.getEventSummaries(ciudad, categoria, pageable));
    }

    @GetMapping
    @Operation(
            summary = "Motor de búsqueda avanzada con carga ansiosa (@EntityGraph)",
            description = "Resuelve el problema N+1 trayendo Evento, Lugar y Categorías en una única consulta SQL. " +
                    "Ordenado cronológicamente por fecha descendente."
    )
    public ResponseEntity<Slice<Event>> searchCatalog(
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
        return ResponseEntity.ok(
                eventService.getCatalogEvents(ciudad, categoria, capacidadMin, fechaInicio, fechaFin, pageable)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar detalle de un evento activo por ID")
    @ApiResponse(responseCode = "200", description = "Evento encontrado")
    @ApiResponse(responseCode = "404", description = "Evento no existe o fue desactivado mediante Soft Delete")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo evento relacional",
            description = "Vincula obligatoriamente un Venue existente y una colección de Categories."
    )
    @ApiResponse(responseCode = "201", description = "Evento creado y vinculado exitosamente")
    public ResponseEntity<Event> createEvent(@Valid @RequestBody EventFormDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEventFromDTO(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Borrado lógico de un evento (Soft Delete)",
            description = "Por políticas de auditoría no realiza DELETE físico. Actualiza activo = false " +
                    "y el registro deja de aparecer en consultas gracias a @SQLRestriction."
    )
    @ApiResponse(responseCode = "204", description = "Evento desactivado lógicamente (Soft Delete)")
    @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    public ResponseEntity<Void> softDeleteEvent(@PathVariable Long id) {
        eventService.softDeleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}