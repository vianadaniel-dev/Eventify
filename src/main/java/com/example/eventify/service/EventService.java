package com.example.eventify.service;

import com.example.eventify.dto.EventFormDTO;
import com.example.eventify.dto.EventSummaryDTO;
import com.example.eventify.exception.BadRequestException;
import com.example.eventify.exception.ResourceNotFoundException;
import com.example.eventify.model.Category;
import com.example.eventify.model.Event;
import com.example.eventify.model.Venue;
import com.example.eventify.repository.CategoryRepository;
import com.example.eventify.repository.EventRepository;
import com.example.eventify.repository.VenueRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;

    public EventService(EventRepository eventRepository,
                        VenueRepository venueRepository,
                        CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Event createEventFromDTO(EventFormDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del evento es obligatorio");
        }
        if (dto.getFecha() == null) {
            throw new BadRequestException("La fecha del evento es obligatoria");
        }
        if (dto.getVenueId() == null) {
            throw new BadRequestException("El lugar (Venue) es obligatorio");
        }

        Venue venue = venueRepository.findById(dto.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado con ID: " + dto.getVenueId()));

        Set<Category> categories = new HashSet<>();
        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            categories.addAll(categoryRepository.findAllById(dto.getCategoryIds()));
        }

        Event event = Event.builder()
                .nombre(dto.getNombre().trim())
                .fecha(dto.getFecha())
                .descripcion(dto.getDescripcion())
                .activo(true)
                .venue(venue)
                .categories(categories)
                .build();

        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Slice<EventSummaryDTO> getEventSummaries(String ciudad, String categoria, Pageable pageable) {
        return eventRepository.findSummariesByFilters(ciudad, categoria, pageable);
    }

    @Transactional(readOnly = true)
    public Slice<Event> getCatalogEvents(String ciudad, String categoria, Integer capacidadMin,
                                         LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable) {
        return eventRepository.findCatalogWithRelations(
                ciudad, categoria, capacidadMin, fechaInicio, fechaFin, pageable
        );
    }

    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        return eventRepository.findWithRelationsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento activo no encontrado con ID: " + id));
    }

    // Soft Delete (TASK 1.2): actualiza el estado a inactivo en vez de borrar físicamente
    @Transactional
    public void softDeleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede desactivar. Evento no encontrado con ID: " + id));
        event.deactivate();
        eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}