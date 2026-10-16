package com.example.eventify.controller;

import com.example.eventify.dto.VenueCreateDTO;
import com.example.eventify.dto.VenueResponseDTO;
import com.example.eventify.service.VenueService;
import com.example.eventify.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/venues")
@Tag(name = "Lugares", description = "Operaciones CRUD desacopladas con DTOs, Paginación y Ordenamiento para Lugares")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo lugar (VenueCreateDTO)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lugar creado exitosamente",
                    content = @Content(schema = @Schema(implementation = VenueResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (ProblemDetail RFC 7807)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Lugar duplicado (ProblemDetail RFC 7807)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<VenueResponseDTO> createVenue(
            @Validated(ValidationGroups.OnCreate.class) @Valid @RequestBody VenueCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(venueService.createVenue(dto));
    }

    @GetMapping
    @Operation(summary = "Listado paginado de lugares (VenueResponseDTO)")
    public ResponseEntity<Page<VenueResponseDTO>> getAllVenues(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(venueService.getAllVenuesDTOPage(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar lugar por ID (VenueResponseDTO)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lugar encontrado",
                    content = @Content(schema = @Schema(implementation = VenueResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Lugar no encontrado (ProblemDetail RFC 7807)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<VenueResponseDTO> getVenueById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.getVenueResponseById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar lugar por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lugar actualizado",
                    content = @Content(schema = @Schema(implementation = VenueResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Lugar no encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Nombre de lugar duplicado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<VenueResponseDTO> updateVenue(
            @PathVariable Long id,
            @Valid @RequestBody VenueCreateDTO dto) {
        return ResponseEntity.ok(venueService.updateVenue(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar lugar por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lugar eliminado"),
            @ApiResponse(responseCode = "400", description = "No se puede eliminar porque tiene eventos vinculados",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Lugar no encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }
}