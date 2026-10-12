package com.example.eventify.service;

import com.example.eventify.exception.BadRequestException;
import com.example.eventify.exception.ResourceNotFoundException;
import com.example.eventify.model.Event;
import com.example.eventify.repository.EventRepository;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    @DisplayName("Crear evento con datos válidos")
    void createEvent_ValidData_Success() {
        Event input = Event.builder().nombre("Concierto Rock").fecha(LocalDate.now()).build();
        Event saved = Event.builder().id(1L).nombre("Concierto Rock").fecha(LocalDate.now()).build();

        when(eventRepository.save(any(Event.class))).thenReturn(saved);

        Event result = eventService.createEvent(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(eventRepository, times(1)).save(input);
    }

    @Test
    @DisplayName("Crear evento con nombre vacío lanza BadRequestException")
    void createEvent_EmptyName_ThrowsException() {
        Event invalid = Event.builder().nombre("   ").fecha(LocalDate.now()).build();

        assertThrows(BadRequestException.class, () -> eventService.createEvent(invalid));
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Buscar por ID inexistente lanza ResourceNotFoundException (404)")
    void getEventById_NotFound_ThrowsException() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.getEventById(999L));
        verify(eventRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Listar eventos con paginación")
    void getAllEvents_Paged_Success() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Event> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(eventRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<Event> result = eventService.getAllEvents(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(eventRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Eliminar evento inexistente lanza ResourceNotFoundException (404)")
    void deleteEvent_NotFound_ThrowsException() {
        when(eventRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> eventService.deleteEvent(999L));
        verify(eventRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Eliminar evento existente ejecuta deleteById")
    void deleteEvent_Success() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> eventService.deleteEvent(1L));
        verify(eventRepository, times(1)).deleteById(1L);
    }
}