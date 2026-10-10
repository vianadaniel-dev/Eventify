package com.example.eventify.config;

import com.example.eventify.model.Event;
import com.example.eventify.model.Venue;
import com.example.eventify.repository.EventRepository;
import com.example.eventify.repository.VenueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDate;

@Configuration
public class DataLoaderConfig {

    @Bean
    @ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true", matchIfMissing = true)
    public CommandLineRunner initDatabase(EventRepository eventRepository, VenueRepository venueRepository) {
        return args -> {
            Venue venue1 = venueRepository.save(Venue.builder()
                    .nombre("Centro de Convenciones Puerta de Oro")
                    .direccion("Vía 40 # 79B-06, Barranquilla")
                    .capacidad(3000)
                    .build());

            eventRepository.save(Event.builder()
                    .nombre("Tech Conference 2026")
                    .fecha(LocalDate.of(2026, 11, 15))
                    .descripcion("Encuentro anual de desarrollo e innovación tecnológica")
                    .build());
        };
    }
}