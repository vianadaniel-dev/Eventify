package com.example.eventify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    private Long id;
    private String nombre;
    private String direccion;
    private Integer capacidad;
}