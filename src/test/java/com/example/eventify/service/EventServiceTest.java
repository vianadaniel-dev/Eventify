package com.example.eventify.service;

import com.example.eventify.exception.BadRequestException;
import com.example.eventify.model.Event;
import com.example.eventify.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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
    @DisplayName("Escenario 1: Registro exitoso de evento (Camino Feliz)")
    void createEvent_ValidData_ReturnsCreatedEvent() {
        // Arrange (Preparación)
        Event inputEvent = Event.builder()
                .nombre("Tech Conference 2026")
                .fecha(LocalDate.of(2026, 11, 20))
                .descripcion("Conferencia de tecnología e innovación")
                .build();

        Event savedEvent = Event.builder()
                .id(1L)
                .nombre("Tech Conference 2026")
                .fecha(LocalDate.of(2026, 11, 20))
                .descripcion("Conferencia de tecnología e innovación")
                .build();

        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        // Act (Ejecución)
        Event result = eventService.createEvent(inputEvent);

        // Assert (Verificación)
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Tech Conference 2026", result.getNombre());
        verify(eventRepository, times(1)).save(inputEvent);
    }

    @Test
    @DisplayName("Escenario 2: Intento de registro con nombre vacío lanza excepción")
    void createEvent_EmptyName_ThrowsBadRequestException() {
        // Arrange
        Event invalidEvent = Event.builder()
                .nombre("   ")
                .fecha(LocalDate.of(2026, 11, 20))
                .descripcion("Descripción")
                .build();

        // Act & Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> eventService.createEvent(invalidEvent)
        );

        assertTrue(exception.getMessage().contains("nombre del evento es obligatorio"));
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Escenario 2 (Variante): Intento de registro con fecha nula lanza excepción")
    void createEvent_NullDate_ThrowsBadRequestException() {
        Event invalidEvent = Event.builder()
                .nombre("Concierto Acústico")
                .fecha(null)
                .descripcion("Música en vivo")
                .build();

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> eventService.createEvent(invalidEvent)
        );

        assertTrue(exception.getMessage().contains("fecha del evento es obligatoria"));
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Escenario 3: Consulta de catálogo vacío retorna lista vacía (Caso de Borde)")
    void getAllEvents_EmptyCatalog_ReturnsEmptyList() {
        // Arrange
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Event> results = eventService.getAllEvents();

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Consulta de catálogo con datos retorna listado de eventos")
    void getAllEvents_WithData_ReturnsEventList() {
        List<Event> mockList = List.of(
                Event.builder().id(1L).nombre("Evento A").fecha(LocalDate.now()).build(),
                Event.builder().id(2L).nombre("Evento B").fecha(LocalDate.now()).build()
        );
        when(eventRepository.findAll()).thenReturn(mockList);

        List<Event> results = eventService.getAllEvents();

        assertEquals(2, results.size());
        verify(eventRepository, times(1)).findAll();
    }
}