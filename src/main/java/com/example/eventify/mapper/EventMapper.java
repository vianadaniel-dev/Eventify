package com.example.eventify.mapper;

import com.example.eventify.dto.EventCreateDTO;
import com.example.eventify.dto.EventFormDTO;
import com.example.eventify.dto.EventResponseDTO;
import com.example.eventify.model.Category;
import com.example.eventify.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "venue.nombre", target = "venueName")
    @Mapping(source = "categories", target = "categoryNames", qualifiedByName = "categoriesToNames")
    EventResponseDTO toResponseDTO(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Event toEntity(EventCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "categories", ignore = true)
    void updateEntityFromDTO(EventCreateDTO dto, @MappingTarget Event event);

    // Mapeo de apoyo para convertir EventFormDTO (Thymeleaf) a EventCreateDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emailOrganizador", ignore = true)
    EventCreateDTO formToCreateDTO(EventFormDTO formDTO);

    @Named("categoriesToNames")
    default Set<String> categoriesToNames(Set<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return Collections.emptySet();
        }
        return categories.stream()
                .map(Category::getNombre)
                .collect(Collectors.toSet());
    }
}