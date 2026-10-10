package com.example.eventify.service;

import com.example.eventify.exception.BadRequestException;
import com.example.eventify.model.Event;
import com.example.eventify.repository.EventRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    // Inyección por constructor explícita
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event createEvent(Event event) {
        if (event == null) {
            throw new BadRequestException("El evento no puede ser nulo");
        }
        if (event.getNombre() == null || event.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del evento es obligatorio y no puede estar vacío");
        }
        if (event.getFecha() == null) {
            throw new BadRequestException("La fecha del evento es obligatoria");
        }
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}