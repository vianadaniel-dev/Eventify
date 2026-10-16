package com.example.eventify.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Eventify API — Plataforma de Gestión de Eventos")
                        .version("2.0.0 (M6.1S5)")
                        .description("API REST desacoplada con DTOs y MapStruct, validación avanzada con Jakarta Bean Validation (@NoPastEvents) y manejo centralizado de errores bajo el estándar RFC 7807 (Problem Details).")
                        .contact(new Contact()
                                .name("Equipo de Arquitectura Eventify")
                                .email("soporte@eventify.com")));
    }
}