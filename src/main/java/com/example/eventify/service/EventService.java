package com.example.eventify.service;

import com.example.eventify.dto.EventCreateDTO;
import com.example.eventify.dto.EventFormDTO;
import com.example.eventify.dto.EventResponseDTO;
import com.example.eventify.dto.EventSummaryDTO;
import com.example.eventify.exception.BadRequestException;
import com.example.eventify.exception.ResourceNotFoundException;
import com.example.eventify.mapper.EventMapper;
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
    private final EventMapper eventMapper;

    public EventService(EventRepository eventRepository,
                        VenueRepository venueRepository,
                        CategoryRepository categoryRepository,
                        EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.categoryRepository = categoryRepository;
        this.eventMapper = eventMapper;
    }

    // ==================== MÉTODOS DESACOPLADOS CON DTOs Y MAPSTRUCT (TASK 1.4) ====================

    @Transactional
    public EventResponseDTO createEvent(EventCreateDTO dto) {
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
                .orElseThrow(() -> new ResourceNotFoundException("El lugar con ID " + dto.getVenueId() + " no existe"));

        Set<Category> categories = resolveCategories(dto.getCategoryIds());

        Event event = eventMapper.toEntity(dto);
        event.setNombre(dto.getNombre().trim());
        event.setActivo(true);
        event.setVenue(venue);
        event.setCategories(categories);

        Event saved = eventRepository.save(event);
        return eventMapper.toResponseDTO(saved);
    }

    @Transactional
    public EventResponseDTO updateEvent(Long id, EventCreateDTO dto) {
        Event existing = getEventById(id);

        Venue venue = venueRepository.findById(dto.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("El lugar con ID " + dto.getVenueId() + " no existe"));

        Set<Category> categories = resolveCategories(dto.getCategoryIds());

        eventMapper.updateEntityFromDTO(dto, existing);
        existing.setNombre(dto.getNombre().trim());
        existing.setVenue(venue);
        existing.setCategories(categories);

        Event saved = eventRepository.save(existing);
        return eventMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public EventResponseDTO getEventResponseById(Long id) {
        Event event = getEventById(id);
        return eventMapper.toResponseDTO(event);
    }

    // Coexistencia con el Record de la Semana 4 (Escenario 1 y 7)
    @Transactional(readOnly = true)
    public Slice<EventSummaryDTO> getEventSummaries(String ciudad, String categoria, Pageable pageable) {
        return eventRepository.findSummariesByFilters(ciudad, categoria, pageable);
    }

    // Soft Delete (TASK 1.2 Semana 4 + CRUD Semana 5)
    @Transactional
    public void softDeleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El evento con ID " + id + " no existe"));
        event.deactivate();
        eventRepository.save(event);
    }

    // ==================== MÉTODOS DE APOYO PARA PANEL ADMINISTRATIVO THYMELEAF ====================

    @Transactional
    public Event createEventFromDTO(EventFormDTO formDto) {
        EventCreateDTO createDTO = eventMapper.formToCreateDTO(formDto);
        createEvent(createDTO);
        // Retornamos la entidad recién creada para compatibilidad si algún llamador la requiere
        return eventRepository.findAll().stream()
                .reduce((first, second) -> second)
                .orElse(null);
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
                .orElseThrow(() -> new ResourceNotFoundException("El evento con ID " + id + " no existe"));
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    private Set<Category> resolveCategories(Set<Long> categoryIds) {
        Set<Category> categories = new HashSet<>();
        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Category> found = categoryRepository.findAllById(categoryIds);
            if (found.isEmpty()) {
                throw new ResourceNotFoundException("No se encontraron las categorías especificadas");
            }
            categories.addAll(found);
        }
        return categories;
    }
}