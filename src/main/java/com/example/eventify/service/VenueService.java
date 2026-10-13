package com.example.eventify.service;

import com.example.eventify.exception.BadRequestException;
import com.example.eventify.exception.ResourceNotFoundException;
import com.example.eventify.model.Venue;
import com.example.eventify.repository.VenueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Transactional
    public Venue createVenue(Venue venue) {
        if (venue.getNombre() == null || venue.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del lugar es obligatorio");
        }
        if (venue.getCapacidad() == null || venue.getCapacidad() <= 0) {
            throw new BadRequestException("La capacidad debe ser un número mayor a cero");
        }
        return venueRepository.save(venue);
    }

    @Transactional(readOnly = true)
    public Venue getVenueById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Venue> getAllVenues(Pageable pageable) {
        return venueRepository.findAll(pageable);
    }

    @Transactional
    public Venue updateVenue(Long id, Venue updatedVenue) {
        Venue existing = getVenueById(id);

        if (updatedVenue.getNombre() == null || updatedVenue.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre no puede estar vacío");
        }
        if (updatedVenue.getCapacidad() == null || updatedVenue.getCapacidad() <= 0) {
            throw new BadRequestException("La capacidad debe ser mayor a cero");
        }
        if (updatedVenue.getCiudad() == null || updatedVenue.getCiudad().trim().isEmpty()) {
            throw new BadRequestException("La ciudad es obligatoria");
        }

        existing.setNombre(updatedVenue.getNombre());
        existing.setDireccion(updatedVenue.getDireccion());
        existing.setCiudad(updatedVenue.getCiudad());
        existing.setCapacidad(updatedVenue.getCapacidad());

        return venueRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public java.util.List<Venue> getAllVenuesList() {
        return venueRepository.findAll();
    }

    @Transactional
    public void deleteVenue(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Lugar no encontrado con ID: " + id);
        }
        venueRepository.deleteById(id);
    }
}