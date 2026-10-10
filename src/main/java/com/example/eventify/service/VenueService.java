package com.example.eventify.service;

import com.example.eventify.exception.BadRequestException;
import com.example.eventify.model.Venue;
import com.example.eventify.repository.VenueRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public Venue createVenue(Venue venue) {
        if (venue == null) {
            throw new BadRequestException("El lugar no puede ser nulo");
        }
        if (venue.getNombre() == null || venue.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del lugar es obligatorio");
        }
        if (venue.getCapacidad() == null || venue.getCapacidad() <= 0) {
            throw new BadRequestException("La capacidad debe ser un número mayor a cero");
        }
        return venueRepository.save(venue);
    }

    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }
}