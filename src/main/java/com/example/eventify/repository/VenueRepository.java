package com.example.eventify.repository;

import com.example.eventify.model.Venue;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class VenueRepository {

    private final Map<Long, Venue> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Venue save(Venue venue) {
        if (venue.getId() == null) {
            venue.setId(idGenerator.getAndIncrement());
        }
        storage.put(venue.getId(), venue);
        return venue;
    }

    public List<Venue> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Venue> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }
}