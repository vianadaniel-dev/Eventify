package com.example.eventify.repository;

import com.example.eventify.dto.EventSummaryDTO;
import com.example.eventify.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // 1. Proyección directa a Java Record con Slice y orden cronológico descendente (TASK 2.1 y 2.2)
    @Query("""
        SELECT DISTINCT new com.example.eventify.dto.EventSummaryDTO(
            e.id, e.nombre, e.fecha, v.nombre, v.ciudad
        )
        FROM Event e
        JOIN e.venue v
        LEFT JOIN e.categories c
        WHERE (:ciudad IS NULL OR :ciudad = '' OR LOWER(v.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%')))
          AND (:categoria IS NULL OR :categoria = '' OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :categoria, '%')))
        ORDER BY e.fecha DESC
    """)
    Slice<EventSummaryDTO> findSummariesByFilters(
            @Param("ciudad") String ciudad,
            @Param("categoria") String categoria,
            Pageable pageable
    );

    // 2. Resolución del problema N+1 con @EntityGraph y filtros avanzados (TASK 2.3 y TASK 3.1)
    @EntityGraph(attributePaths = {"venue", "categories"})
    @Query("""
        SELECT DISTINCT e
        FROM Event e
        JOIN e.venue v
        LEFT JOIN e.categories c
        WHERE (:ciudad IS NULL OR :ciudad = '' OR LOWER(v.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%')))
          AND (:categoria IS NULL OR :categoria = '' OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :categoria, '%')))
          AND (:capacidadMin IS NULL OR v.capacidad >= :capacidadMin)
          AND (:fechaInicio IS NULL OR e.fecha >= :fechaInicio)
          AND (:fechaFin IS NULL OR e.fecha <= :fechaFin)
        ORDER BY e.fecha DESC
    """)
    Slice<Event> findCatalogWithRelations(
            @Param("ciudad") String ciudad,
            @Param("categoria") String categoria,
            @Param("capacidadMin") Integer capacidadMin,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            Pageable pageable
    );

    // 3. Carga ansiosa por ID en una sola consulta
    @EntityGraph(attributePaths = {"venue", "categories"})
    Optional<Event> findWithRelationsById(Long id);

    // 4. Consultas derivadas (Derived Queries - TASK 3.1 y 3.2)
    @EntityGraph(attributePaths = {"venue", "categories"})
    Slice<Event> findByVenueCiudadContainingIgnoreCaseOrderByFechaDesc(String ciudad, Pageable pageable);

    @EntityGraph(attributePaths = {"venue", "categories"})
    Slice<Event> findByCategoriesNombreContainingIgnoreCaseOrderByFechaDesc(String categoria, Pageable pageable);

    @EntityGraph(attributePaths = {"venue", "categories"})
    Slice<Event> findByVenueCapacidadGreaterThanEqualOrderByFechaDesc(Integer capacidad, Pageable pageable);

    @EntityGraph(attributePaths = {"venue", "categories"})
    Slice<Event> findByFechaBetweenOrderByFechaDesc(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);

    // 5. Verificar si existen eventos activos asociados a un Venue (protección de integridad al eliminar - M6.1S5)
    boolean existsByVenueId(Long venueId);
}