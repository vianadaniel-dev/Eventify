package com.example.eventify.controller;

import com.example.eventify.model.Venue;
import com.example.eventify.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
@Tag(name = "Lugares", description = "Endpoints para la gestión de venues y recintos")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo lugar", description = "Registra un recinto con su respectiva capacidad")
    @ApiResponse(responseCode = "201", description = "Lugar creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<Venue> createVenue(@RequestBody Venue venue) {
        Venue created = venueService.createVenue(venue);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar todos los lugares", description = "Obtiene todos los recintos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de lugares obtenida")
    public ResponseEntity<List<Venue>> getAllVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }
}
