package com.example.eventify.service;

import com.example.eventify.exception.BadRequestException;
import com.example.eventify.model.Venue;
import com.example.eventify.repository.VenueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueService venueService;

    @Test
    @DisplayName("Registro exitoso de lugar (Camino Feliz)")
    void createVenue_ValidData_ReturnsCreatedVenue() {
        // Arrange
        Venue inputVenue = Venue.builder()
                .nombre("Puerta de Oro")
                .direccion("Vía 40 # 79B-06")
                .capacidad(4000)
                .build();

        Venue savedVenue = Venue.builder()
                .id(1L)
                .nombre("Puerta de Oro")
                .direccion("Vía 40 # 79B-06")
                .capacidad(4000)
                .build();

        when(venueRepository.save(any(Venue.class))).thenReturn(savedVenue);

        // Act
        Venue result = venueService.createVenue(inputVenue);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Puerta de Oro", result.getNombre());
        assertEquals(4000, result.getCapacidad());
        verify(venueRepository, times(1)).save(inputVenue);
    }

    @Test
    @DisplayName("Error al registrar lugar con nombre vacío")
    void createVenue_EmptyName_ThrowsBadRequestException() {
        Venue invalidVenue = Venue.builder()
                .nombre("   ")
                .direccion("Calle 100 # 50")
                .capacidad(500)
                .build();

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> venueService.createVenue(invalidVenue)
        );

        assertTrue(exception.getMessage().contains("nombre del lugar es obligatorio"));
        verify(venueRepository, never()).save(any());
    }

    @Test
    @DisplayName("Error al registrar lugar con capacidad menor o igual a cero")
    void createVenue_InvalidCapacity_ThrowsBadRequestException() {
        Venue invalidVenue = Venue.builder()
                .nombre("Teatro Municipal")
                .direccion("Carrera 53 # 70")
                .capacidad(0)
                .build();

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> venueService.createVenue(invalidVenue)
        );

        assertTrue(exception.getMessage().contains("capacidad debe ser un número mayor a cero"));
        verify(venueRepository, never()).save(any());
    }

    @Test
    @DisplayName("Consulta de recintos con catálogo vacío retorna lista vacía")
    void getAllVenues_EmptyCatalog_ReturnsEmptyList() {
        when(venueRepository.findAll()).thenReturn(Collections.emptyList());

        List<Venue> results = venueService.getAllVenues();

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(venueRepository, times(1)).findAll();
    }
}