package com.example.eventify.repository;

import com.example.eventify.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // Consulta derivada con paginación
    Page<Event> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}