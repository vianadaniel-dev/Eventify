package com.example.eventify.controller.web;

import com.example.eventify.dto.EventFormDTO;
import com.example.eventify.model.Event;
import com.example.eventify.service.EventService;
import com.example.eventify.service.VenueService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;
    private final VenueService venueService;

    public AdminEventController(EventService eventService, VenueService venueService) {
        this.eventService = eventService;
        this.venueService = venueService;
    }

    @GetMapping
    public String showEventsPanel(
            @RequestParam(defaultValue = "") String ciudad,
            @RequestParam(defaultValue = "") String categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Slice<Event> eventsSlice = eventService.getCatalogEvents(
                ciudad, categoria, null, null, null, PageRequest.of(page, size)
        );

        model.addAttribute("eventsSlice", eventsSlice);
        model.addAttribute("events", eventsSlice.getContent());
        model.addAttribute("venues", venueService.getAllVenuesList());
        model.addAttribute("categories", eventService.getAllCategories());
        model.addAttribute("eventForm", new EventFormDTO());

        // Mantener filtros activos en la vista para la barra de navegación (Escenario 3)
        model.addAttribute("ciudad", ciudad);
        model.addAttribute("categoria", categoria);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return "admin/events";
    }

    @PostMapping
    public String saveEvent(@ModelAttribute("eventForm") EventFormDTO eventForm) {
        eventService.createEventFromDTO(eventForm);
        return "redirect:/admin/events";
    }

    @PostMapping("/{id}/delete")
    public String deactivateEvent(@PathVariable Long id) {
        eventService.softDeleteEvent(id);
        return "redirect:/admin/events";
    }
}