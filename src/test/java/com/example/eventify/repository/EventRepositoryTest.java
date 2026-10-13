package com.example.eventify.repository;

import com.example.eventify.dto.EventSummaryDTO;
import com.example.eventify.model.Category;
import com.example.eventify.model.Event;
import com.example.eventify.model.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Venue savedVenue;
    private Category savedCategory;

    @BeforeEach
    void setUp() {
        savedVenue = venueRepository.save(Venue.builder()
                .nombre("Movistar Arena")
                .ciudad("Bogotá")
                .direccion("Dg. 61c #26-36")
                .capacidad(14000)
                .build());

        savedCategory = categoryRepository.save(Category.builder()
                .nombre("Conciertos")
                .descripcion("Música en vivo")
                .build());
    }

    @Test
    @DisplayName("Escenario 6: Búsqueda insensible a mayúsculas y parcial con Record y Slice")
    void findSummariesByFilters_CaseInsensitiveAndPartial_Success() {
        eventRepository.save(Event.builder()
                .nombre("Concierto de ROCK")
                .fecha(LocalDate.now().plusDays(5))
                .activo(true)
                .venue(savedVenue)
                .categories(Set.of(savedCategory))
                .build());

        Slice<EventSummaryDTO> result = eventRepository.findSummariesByFilters(
                "bog", "concier", PageRequest.of(0, 10)
        );

        assertFalse(result.isEmpty());
        assertEquals("Concierto de ROCK", result.getContent().get(0).nombreEvento());
        assertEquals("Bogotá", result.getContent().get(0).ciudad());
    }

    @Test
    @DisplayName("TASK 1.2: Soft Delete (@SQLRestriction) oculta eventos inactivos automáticamente")
    void softDelete_HidesInactiveEvent() {
        Event event = eventRepository.save(Event.builder()
                .nombre("Evento Temporal")
                .fecha(LocalDate.now())
                .activo(true)
                .venue(savedVenue)
                .build());

        event.deactivate();
        eventRepository.saveAndFlush(event);

        // Al tener activo = false, @SQLRestriction impide que aparezca en consultas
        Slice<Event> activeEvents = eventRepository.findCatalogWithRelations(
                null, null, null, null, null, PageRequest.of(0, 10)
        );
        assertTrue(activeEvents.isEmpty());
    }
}