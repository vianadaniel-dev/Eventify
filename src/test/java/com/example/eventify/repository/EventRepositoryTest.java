package com.example.eventify.repository;

import com.example.eventify.model.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("Debe persistir un evento y recuperarlo por ID")
    void saveAndFindById_Success() {
        Event event = Event.builder()
                .nombre("Concierto de Jazz")
                .fecha(LocalDate.of(2026, 12, 15))
                .descripcion("Encuentro musical anual")
                .build();

        Event saved = eventRepository.save(event);

        assertNotNull(saved.getId());
        Optional<Event> found = eventRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Concierto de Jazz", found.get().getNombre());
    }

    @Test
    @DisplayName("Debe buscar eventos por coincidencia parcial de nombre de forma paginada")
    void findByNombreContainingIgnoreCase_ReturnsPage() {
        eventRepository.save(Event.builder().nombre("Festival de Salsa").fecha(LocalDate.now()).build());
        eventRepository.save(Event.builder().nombre("Festival de Rock").fecha(LocalDate.now()).build());
        eventRepository.save(Event.builder().nombre("Feria del Libro").fecha(LocalDate.now()).build());

        Page<Event> page = eventRepository.findByNombreContainingIgnoreCase("festival", PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements());
    }

    @Test
    @DisplayName("Debe eliminar un evento de la base de datos")
    void deleteEvent_Success() {
        Event saved = eventRepository.save(Event.builder().nombre("Evento Temporal").fecha(LocalDate.now()).build());

        eventRepository.deleteById(saved.getId());

        Optional<Event> deleted = eventRepository.findById(saved.getId());
        assertTrue(deleted.isEmpty());
    }
}