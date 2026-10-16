package com.example.eventify.controller.web;

import com.example.eventify.dto.VenueCreateDTO;
import com.example.eventify.exception.BusinessRuleViolationException;
import com.example.eventify.exception.DuplicateResourceException;
import com.example.eventify.exception.ResourceNotFoundException;
import com.example.eventify.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/venues")
public class AdminVenueController {

    private final VenueService venueService;

    public AdminVenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping
    public String showVenuesPanel(Model model) {
        model.addAttribute("venues", venueService.getAllVenuesList());
        if (!model.containsAttribute("venue")) {
            model.addAttribute("venue", new VenueCreateDTO());
        }
        return "admin/venues";
    }

    @PostMapping
    public String saveVenue(
            @Valid @ModelAttribute("venue") VenueCreateDTO venueDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("venues", venueService.getAllVenuesList());
            return "admin/venues";
        }

        try {
            venueService.createVenue(venueDto);
            redirectAttributes.addFlashAttribute("successMessage", "¡Lugar (Venue) registrado exitosamente!");
        } catch (DuplicateResourceException | BusinessRuleViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/venues";
    }

    @PostMapping("/{id}/delete")
    public String deleteVenue(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            venueService.deleteVenue(id);
            redirectAttributes.addFlashAttribute("successMessage", "Lugar eliminado correctamente.");
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/venues";
    }
}