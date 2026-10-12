package com.example.eventify.service;

import com.example.eventify.exception.BadRequestException;
import com.example.eventify.exception.ResourceNotFoundException;
import com.example.eventify.model.Venue;
import com.example.eventify.repository.VenueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

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
    @DisplayName("Crear lugar con datos válidos")
    void createVenue_ValidData_Success() {
        Venue input = Venue.builder().nombre("Teatro").direccion("Calle 10").capacidad(500).build();
        Venue saved = Venue.builder().id(1L).nombre("Teatro").direccion("Calle 10").capacidad(500).build();

        when(venueRepository.save(any(Venue.class))).thenReturn(saved);

        Venue result = venueService.createVenue(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(venueRepository, times(1)).save(input);
    }

    @Test
    @DisplayName("Crear lugar con capacidad menor o igual a cero lanza BadRequestException")
    void createVenue_InvalidCapacity_ThrowsException() {
        Venue invalid = Venue.builder().nombre("Teatro").direccion("Calle 10").capacidad(0).build();

        assertThrows(BadRequestException.class, () -> venueService.createVenue(invalid));
        verify(venueRepository, never()).save(any());
    }

    @Test
    @DisplayName("Buscar lugar inexistente por ID lanza ResourceNotFoundException (404)")
    void getVenueById_NotFound_ThrowsException() {
        when(venueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> venueService.getVenueById(999L));
        verify(venueRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Listar lugares con paginación")
    void getAllVenues_Paged_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Venue> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(venueRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<Venue> result = venueService.getAllVenues(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(venueRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Eliminar lugar inexistente lanza ResourceNotFoundException (404)")
    void deleteVenue_NotFound_ThrowsException() {
        when(venueRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> venueService.deleteVenue(999L));
        verify(venueRepository, never()).deleteById(anyLong());
    }
}