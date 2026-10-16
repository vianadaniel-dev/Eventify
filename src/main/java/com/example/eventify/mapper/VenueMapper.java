package com.example.eventify.mapper;

import com.example.eventify.dto.VenueCreateDTO;
import com.example.eventify.dto.VenueResponseDTO;
import com.example.eventify.model.Venue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VenueMapper {

    VenueResponseDTO toResponseDTO(Venue venue);

    List<VenueResponseDTO> toResponseDTOList(List<Venue> venues);

    @Mapping(target = "id", ignore = true)
    Venue toEntity(VenueCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(VenueCreateDTO dto, @MappingTarget Venue venue);
}