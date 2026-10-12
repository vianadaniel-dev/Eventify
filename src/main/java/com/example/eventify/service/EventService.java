package com.example.eventify.service;

import com.example.eventify.exception.BadRequestException;
import com.example.eventify.exception.ResourceNotFoundException;
import com.example.eventify.model.Event;
import com.example.eventify.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Event createEvent(Event event) {
        if (event.getNombre() == null || event.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del evento es obligatorio");
        }
        if (event.getFecha() == null) {
            throw new BadRequestException("La fecha del evento es obligatoria");
        }
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Event> searchEvents(String nombre, Pageable pageable) {
        return eventRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @Transactional
    public Event updateEvent(Long id, Event updatedEvent) {
        Event existing = getEventById(id);

        if (updatedEvent.getNombre() == null || updatedEvent.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del evento no puede estar vacío");
        }
        if (updatedEvent.getFecha() == null) {
            throw new BadRequestException("La fecha del evento es obligatoria");
        }

        existing.setNombre(updatedEvent.getNombre());
        existing.setFecha(updatedEvent.getFecha());
        existing.setDescripcion(updatedEvent.getDescripcion());

        return eventRepository.save(existing);
    }

    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Evento no encontrado con ID: " + id);
        }
        eventRepository.deleteById(id);
    }
}