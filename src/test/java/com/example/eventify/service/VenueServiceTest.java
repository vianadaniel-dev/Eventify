package com.example.eventify.service;

import com.example.eventify.dto.VenueCreateDTO;
import com.example.eventify.dto.VenueResponseDTO;
import com.example.eventify.exception.BadRequestException;
import com.example.eventify.exception.ResourceNotFoundException;
import com.example.eventify.mapper.VenueMapper;
import com.example.eventify.model.Venue;
import com.example.eventify.repository.EventRepository;
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

    @Mock
    private EventRepository eventRepository;

    @Mock
    private VenueMapper venueMapper;

    @InjectMocks
    private VenueService venueService;

    @Test
    @DisplayName("Crear lugar con DTO válido exitoso")
    void createVenue_ValidData_Success() {
        VenueCreateDTO inputDto = VenueCreateDTO.builder()
                .nombre("Teatro")
                .ciudad("Bogotá")
                .direccion("Calle 10")
                .capacidad(500)
                .build();

        Venue venueEntity = Venue.builder()
                .nombre("Teatro")
                .ciudad("Bogotá")
                .direccion("Calle 10")
                .capacidad(500)
                .build();

        Venue savedEntity = Venue.builder()
                .id(1L)
                .nombre("Teatro")
                .ciudad("Bogotá")
                .direccion("Calle 10")
                .capacidad(500)
                .build();

        VenueResponseDTO responseDTO = VenueResponseDTO.builder()
                .id(1L)
                .nombre("Teatro")
                .ciudad("Bogotá")
                .direccion("Calle 10")
                .capacidad(500)
                .build();

        when(venueRepository.existsByNombreIgnoreCase(anyString())).thenReturn(false);
        when(venueMapper.toEntity(inputDto)).thenReturn(venueEntity);
        when(venueRepository.save(any(Venue.class))).thenReturn(savedEntity);
        when(venueMapper.toResponseDTO(savedEntity)).thenReturn(responseDTO);

        VenueResponseDTO result = venueService.createVenue(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Teatro", result.getNombre());
        verify(venueRepository, times(1)).save(any(Venue.class));
    }

    @Test
    @DisplayName("Crear lugar con capacidad menor o igual a cero lanza BadRequestException")
    void createVenue_InvalidCapacity_ThrowsException() {
        VenueCreateDTO invalid = VenueCreateDTO.builder()
                .nombre("Teatro")
                .ciudad("Bogotá")
                .direccion("Calle 10")
                .capacidad(0)
                .build();

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
    @DisplayName("Listar lugares con paginación DTO")
    void getAllVenues_Paged_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Venue> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(venueRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<VenueResponseDTO> result = venueService.getAllVenuesDTOPage(pageable);

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