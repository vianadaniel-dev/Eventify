package com.example.eventify.controller.web;

import com.example.eventify.dto.EventFormDTO;
import com.example.eventify.exception.BusinessRuleViolationException;
import com.example.eventify.exception.ResourceNotFoundException;
import com.example.eventify.model.Event;
import com.example.eventify.service.EventService;
import com.example.eventify.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        populateCatalogModel(ciudad, categoria, page, size, model);
        if (!model.containsAttribute("eventForm")) {
            model.addAttribute("eventForm", new EventFormDTO());
        }
        return "admin/events";
    }

    @PostMapping
    public String saveEvent(
            @Valid @ModelAttribute("eventForm") EventFormDTO eventForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Task 4.1 y Escenario 3: Si hay errores de validación, recargar vista preservando datos ingresados
        if (bindingResult.hasErrors()) {
            populateCatalogModel("", "", 0, 10, model);
            return "admin/events";
        }

        try {
            eventService.createEventFromDTO(eventForm);
            redirectAttributes.addFlashAttribute("successMessage", "¡Evento registrado exitosamente en el catálogo!");
        } catch (ResourceNotFoundException | BusinessRuleViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/events";
    }

    @PostMapping("/{id}/delete")
    public String deactivateEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.softDeleteEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", "El evento fue desactivado correctamente.");
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/events";
    }

    private void populateCatalogModel(String ciudad, String categoria, int page, int size, Model model) {
        Slice<Event> eventsSlice = eventService.getCatalogEvents(
                ciudad, categoria, null, null, null, PageRequest.of(page, size)
        );
        model.addAttribute("eventsSlice", eventsSlice);
        model.addAttribute("events", eventsSlice.getContent());
        model.addAttribute("venues", venueService.getAllVenuesList());
        model.addAttribute("categories", eventService.getAllCategories());
        model.addAttribute("ciudad", ciudad);
        model.addAttribute("categoria", categoria);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
    }
}