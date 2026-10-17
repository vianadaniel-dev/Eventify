package com.example.eventify.controller.web;

import com.example.eventify.model.Event;
import com.example.eventify.model.Venue;
import com.example.eventify.security.JwtService;
import com.example.eventify.service.EventService;
import com.example.eventify.service.VenueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminEventController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private VenueService venueService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("Escenario 3: Carga el panel manteniendo filtros y Slice en el Model")
    void showEventsPanel_ReturnsViewAndModelWithSlice() throws Exception {
        Venue venue = Venue.builder().id(1L).nombre("Movistar Arena").ciudad("Bogotá").capacidad(14000).build();
        Event event = Event.builder().id(1L).nombre("Concierto de ROCK").fecha(LocalDate.now()).venue(venue).build();
        Slice<Event> slice = new SliceImpl<>(List.of(event), PageRequest.of(0, 10), true);

        when(eventService.getCatalogEvents(anyString(), anyString(), any(), any(), any(), any())).thenReturn(slice);
        when(venueService.getAllVenuesList()).thenReturn(List.of(venue));
        when(eventService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/events")
                        .param("ciudad", "bog")
                        .param("categoria", "rock")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events"))
                .andExpect(model().attributeExists("eventsSlice", "events", "venues", "categories"))
                .andExpect(model().attribute("ciudad", "bog"))
                .andExpect(model().attribute("categoria", "rock"));
    }

    @Test
    @DisplayName("Guardar evento relacional aplica redirección Post-Redirect-Get")
    void saveEvent_RedirectsSuccessfully() throws Exception {
        when(eventService.createEventFromDTO(any())).thenReturn(new Event());

        mockMvc.perform(post("/admin/events")
                        .param("nombre", "Conferencia Cloud")
                        .param("fecha", "2026-12-01")
                        .param("venueId", "1")
                        .param("categoryIds", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"));

        verify(eventService, times(1)).createEventFromDTO(any());
    }
}