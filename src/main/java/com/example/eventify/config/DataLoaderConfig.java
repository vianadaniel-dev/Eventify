package com.example.eventify.config;

import com.example.eventify.model.Event;
import com.example.eventify.model.Role;
import com.example.eventify.model.User;
import com.example.eventify.model.Venue;
import com.example.eventify.repository.EventRepository;
import com.example.eventify.repository.UserRepository;
import com.example.eventify.repository.VenueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataLoaderConfig {

    @Bean
    @ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true")
    public CommandLineRunner initDatabase(EventRepository eventRepository, VenueRepository venueRepository) {
        return args -> {
            if (venueRepository.count() == 0) {
                Venue venue = venueRepository.save(Venue.builder()
                        .nombre("Centro de Convenciones Puerta de Oro")
                        .ciudad("Barranquilla")
                        .direccion("Vía 40 # 79B-06")
                        .capacidad(8000)
                        .build());

                eventRepository.save(Event.builder()
                        .nombre("Tech Caribe Expo 2026")
                        .fecha(LocalDate.now().plusDays(30))
                        .descripcion("Encuentro de innovación y desarrollo")
                        .activo(true)
                        .venue(venue)
                        .build());
            }
        };
    }

    // Crea un usuario admin por defecto en la tabla 'users' si aún no existe
    @Bean
    public CommandLineRunner initAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                userRepository.save(User.builder()
                        .username("admin")
                        .email("admin@eventify.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ROLE_ADMIN)
                        .build());
            }
        };
    }
}