package com.example.eventify.service;

import com.example.eventify.dto.EventFormDTO;
import com.example.eventify.exception.BadRequestException;
import com.example.eventify.exception.ResourceNotFoundException;
import com.example.eventify.model.Category;
import com.example.eventify.model.Event;
import com.example.eventify.model.Venue;
import com.example.eventify.repository.CategoryRepository;
import com.example.eventify.repository.EventRepository;
import com.example.eventify.repository.VenueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    @DisplayName("Escenario 1: Crear evento relacional con Venue y Category")
    void createEventFromDTO_ValidData_Success() {
        Venue mockVenue = Venue.builder().id(1L).nombre("Movistar Arena").ciudad("Bogotá").capacidad(14000).build();
        Category mockCategory = Category.builder().id(1L).nombre("Conciertos").build();

        EventFormDTO dto = EventFormDTO.builder()
                .nombre("Concierto de ROCK")
                .fecha(LocalDate.now().plusDays(10))
                .descripcion("Show en vivo")
                .venueId(1L)
                .categoryIds(Set.of(1L))
                .build();

        when(venueRepository.findById(1L)).thenReturn(Optional.of(mockVenue));
        when(categoryRepository.findAllById(Set.of(1L))).thenReturn(List.of(mockCategory));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
            Event e = inv.getArgument(0);
            e.setId(10L);
            return e;
        });

        Event result = eventService.createEventFromDTO(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Bogotá", result.getVenue().getCiudad());
        assertEquals(1, result.getCategories().size());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Crear evento con nombre vacío lanza BadRequestException")
    void createEventFromDTO_EmptyName_ThrowsException() {
        EventFormDTO invalid = EventFormDTO.builder().nombre("  ").fecha(LocalDate.now()).venueId(1L).build();

        assertThrows(BadRequestException.class, () -> eventService.createEventFromDTO(invalid));
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Soft Delete cambia estado activo a false en lugar de borrar físicamente")
    void softDeleteEvent_DeactivatesEvent() {
        Event existing = Event.builder().id(5L).nombre("Evento").activo(true).build();
        when(eventRepository.findById(5L)).thenReturn(Optional.of(existing));

        eventService.softDeleteEvent(5L);

        assertFalse(existing.getActivo());
        verify(eventRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Soft Delete de ID inexistente lanza ResourceNotFoundException")
    void softDeleteEvent_NotFound_ThrowsException() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.softDeleteEvent(999L));
    }
}