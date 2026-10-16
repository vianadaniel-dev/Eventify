package com.example.eventify.service;

import com.example.eventify.dto.VenueCreateDTO;
import com.example.eventify.dto.VenueResponseDTO;
import com.example.eventify.exception.BadRequestException;
import com.example.eventify.exception.BusinessRuleViolationException;
import com.example.eventify.exception.DuplicateResourceException;
import com.example.eventify.exception.ResourceNotFoundException;
import com.example.eventify.mapper.VenueMapper;
import com.example.eventify.model.Venue;
import com.example.eventify.repository.EventRepository;
import com.example.eventify.repository.VenueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VenueService {

    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final VenueMapper venueMapper;

    public VenueService(VenueRepository venueRepository,
                        EventRepository eventRepository,
                        VenueMapper venueMapper) {
        this.venueRepository = venueRepository;
        this.eventRepository = eventRepository;
        this.venueMapper = venueMapper;
    }

    // ==================== MÉTODOS DESACOPLADOS CON DTOs Y MAPSTRUCT (API / WEB) ====================

    @Transactional
    public VenueResponseDTO createVenue(VenueCreateDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del lugar es obligatorio");
        }
        if (dto.getCapacidad() == null || dto.getCapacidad() <= 0) {
            throw new BadRequestException("La capacidad debe ser un número mayor a cero");
        }
        if (venueRepository.existsByNombreIgnoreCase(dto.getNombre().trim())) {
            throw new DuplicateResourceException("Ya existe un lugar registrado con el nombre: " + dto.getNombre());
        }

        Venue venue = venueMapper.toEntity(dto);
        venue.setNombre(dto.getNombre().trim());
        Venue saved = venueRepository.save(venue);
        return venueMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public VenueResponseDTO getVenueResponseById(Long id) {
        Venue venue = getVenueById(id);
        return venueMapper.toResponseDTO(venue);
    }

    @Transactional(readOnly = true)
    public List<VenueResponseDTO> getAllVenueResponses() {
        return venueMapper.toResponseDTOList(venueRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Page<VenueResponseDTO> getAllVenuesDTOPage(Pageable pageable) {
        return venueRepository.findAll(pageable).map(venueMapper::toResponseDTO);
    }

    @Transactional
    public VenueResponseDTO updateVenue(Long id, VenueCreateDTO dto) {
        Venue existing = getVenueById(id);

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre no puede estar vacío");
        }
        if (dto.getCapacidad() == null || dto.getCapacidad() <= 0) {
            throw new BadRequestException("La capacidad debe ser mayor a cero");
        }
        if (dto.getCiudad() == null || dto.getCiudad().trim().isEmpty()) {
            throw new BadRequestException("La ciudad es obligatoria");
        }
        if (venueRepository.existsByNombreIgnoreCaseAndIdNot(dto.getNombre().trim(), id)) {
            throw new DuplicateResourceException("Ya existe otro lugar con el nombre: " + dto.getNombre());
        }

        venueMapper.updateEntityFromDTO(dto, existing);
        existing.setNombre(dto.getNombre().trim());
        Venue saved = venueRepository.save(existing);
        return venueMapper.toResponseDTO(saved);
    }

    @Transactional
    public void deleteVenue(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new ResourceNotFoundException("El lugar con ID " + id + " no existe");
        }
        if (eventRepository.existsByVenueId(id)) {
            throw new BusinessRuleViolationException(
                    "No se puede eliminar el lugar porque tiene eventos activos vinculados"
            );
        }
        venueRepository.deleteById(id);
    }

    // ==================== MÉTODOS INTERNOS Y DE APOYO PARA VISTAS THYMELEAF ====================

    @Transactional(readOnly = true)
    public Venue getVenueById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El lugar con ID " + id + " no existe"));
    }

    @Transactional(readOnly = true)
    public Page<Venue> getAllVenues(Pageable pageable) {
        return venueRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Venue> getAllVenuesList() {
        return venueRepository.findAll();
    }
}